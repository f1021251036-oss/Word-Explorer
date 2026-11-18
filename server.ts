import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import cors from 'cors';
import express, { type NextFunction, type Request, type Response } from 'express';
import { OAuth2Client } from 'google-auth-library';
import nodemailer from 'nodemailer';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

function loadLocalEnvIfPresent(): void {
  const candidates = [
    path.resolve(process.cwd(), '.env'),
    path.resolve(__dirname, '.env'),
  ];
  for (const envPath of candidates) {
    if (!fs.existsSync(envPath)) continue;
    try {
      const content = fs.readFileSync(envPath, 'utf8');
      for (const rawLine of content.split(/\r?\n/)) {
        const line = rawLine.trim();
        if (!line || line.startsWith('#')) continue;
        const eqIdx = line.indexOf('=');
        if (eqIdx <= 0) continue;
        const key = line.slice(0, eqIdx).trim();
        let val = line.slice(eqIdx + 1).trim();
        if (
          (val.startsWith('"') && val.endsWith('"')) ||
          (val.startsWith("'") && val.endsWith("'"))
        ) {
          val = val.slice(1, -1);
        }
        if (process.env[key] === undefined) {
          process.env[key] = val;
        }
      }
      break;
    } catch {
      // Ignore unreadable local .env file
    }
  }
}

loadLocalEnvIfPresent();

const isProduction = process.env.NODE_ENV === 'production';
const PORT = Number(process.env.PORT ?? 3000) || 3000;

const envPepper = process.env.OTP_PEPPER_SECRET?.trim();
const OTP_PEPPER_SECRET =
  envPepper && envPepper.length >= 16
    ? envPepper
    : crypto.randomBytes(32).toString('hex');

if (!envPepper) {
  console.warn(
    '[WordExplorer Server] OTP_PEPPER_SECRET tidak diisi; menggunakan secret acak sementara per proses.'
  );
}

const GOOGLE_WEB_CLIENT_ID = (
  process.env.GOOGLE_WEB_CLIENT_ID ??
  process.env.VITE_GOOGLE_WEB_CLIENT_ID ??
  ''
).trim();

const googleClient = GOOGLE_WEB_CLIENT_ID
  ? new OAuth2Client(GOOGLE_WEB_CLIENT_ID)
  : null;

interface SmtpConfig {
  configured: boolean;
  host: string;
  port: number;
  username: string;
  password: string;
  fromEmail: string;
}

function readSmtpConfig(): SmtpConfig {
  const host = (process.env.SMTP_HOST ?? '').trim();
  const port = Number(process.env.SMTP_PORT ?? 587) || 587;
  const username = (process.env.SMTP_USERNAME ?? '').trim();
  const password = (process.env.SMTP_PASSWORD ?? '').trim();
  const fromEmail =
    (process.env.SMTP_FROM_EMAIL ?? '').trim() ||
    (username.includes('@') ? username : 'noreply@wordexplorer.app');
  const configured =
    Boolean(host && username && password) &&
    !username.includes('your-') &&
    !password.includes('your-');
  return { configured, host, port, username, password, fromEmail };
}

interface OtpRecord {
  otpHash: string;
  expiresAt: number;
  attempts: number;
  lastSentAt: number;
  purpose: string;
}

const otpStore = new Map<string, OtpRecord>();

interface RateLimitBucket {
  count: number;
  resetAt: number;
}

const rateBuckets = new Map<string, RateLimitBucket>();

function consumeRateLimit(key: string, maxRequests: number, windowMs: number): boolean {
  const now = Date.now();
  const existing = rateBuckets.get(key);
  if (!existing || now > existing.resetAt) {
    rateBuckets.set(key, { count: 1, resetAt: now + windowMs });
    return true;
  }
  if (existing.count >= maxRequests) {
    return false;
  }
  existing.count += 1;
  return true;
}

// Periodically clean expired OTP & rate-limit entries
setInterval(() => {
  const now = Date.now();
  for (const [k, v] of otpStore.entries()) {
    if (now > v.expiresAt + 60_000) otpStore.delete(k);
  }
  for (const [k, v] of rateBuckets.entries()) {
    if (now > v.resetAt) rateBuckets.delete(k);
  }
}, 5 * 60_000).unref();

function hashOtp(email: string, code: string): string {
  return crypto
    .createHmac('sha256', OTP_PEPPER_SECRET)
    .update(`${email.trim().toLowerCase()}::${code}`)
    .digest('hex');
}

function timingSafeHexEquals(aHex: string, bHex: string): boolean {
  const aBuf = Buffer.from(aHex, 'utf8');
  const bBuf = Buffer.from(bHex, 'utf8');
  if (aBuf.length !== bBuf.length) return false;
  return crypto.timingSafeEqual(aBuf, bBuf);
}

function getClientIp(req: Request): string {
  const forwarded = req.headers['x-forwarded-for'];
  if (typeof forwarded === 'string' && forwarded.length > 0) {
    return forwarded.split(',')[0].trim();
  }
  return req.socket.remoteAddress ?? 'unknown';
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const allowedOrigins = (process.env.ALLOWED_ORIGIN ?? '')
  .split(',')
  .map((s) => s.trim())
  .filter(Boolean);

async function startServer() {
  const app = express();
  app.disable('x-powered-by');
  app.set('trust proxy', 1);

  app.use(
    cors({
      origin: (origin, callback) => {
        if (!origin) {
          callback(null, true);
          return;
        }
        if (!isProduction) {
          callback(null, true);
          return;
        }
        if (allowedOrigins.length === 0 || allowedOrigins.includes(origin)) {
          callback(null, true);
          return;
        }
        callback(new Error('Origin tidak diizinkan oleh kebijakan CORS.'));
      },
      methods: ['GET', 'POST', 'OPTIONS'],
      allowedHeaders: ['Content-Type'],
      credentials: false,
    })
  );

  // Security headers compatible with iframe preview & Google Identity Services
  app.use((_req: Request, res: Response, next: NextFunction) => {
    res.setHeader('X-Content-Type-Options', 'nosniff');
    res.setHeader('Referrer-Policy', 'strict-origin-when-cross-origin');
    res.setHeader('Permissions-Policy', 'camera=(), microphone=(), geolocation=()');
    res.setHeader('Cross-Origin-Opener-Policy', 'same-origin-allow-popups');
    next();
  });

  app.use(express.json({ limit: '32kb' }));

  // Health endpoint (strictly boolean configuration flags, zero secrets)
  app.get('/api/health', (_req: Request, res: Response) => {
    const smtp = readSmtpConfig();
    res.json({
      ok: true,
      smtpConfigured: smtp.configured,
      googleOAuthConfigured: Boolean(GOOGLE_WEB_CLIENT_ID),
      mode: isProduction ? 'production' : 'development',
      timestamp: new Date().toISOString(),
    });
  });

  // Send 6-digit email OTP
  app.post('/api/auth/otp/send', async (req: Request, res: Response) => {
    const ip = getClientIp(req);
    if (!consumeRateLimit(`ip_otp_send:${ip}`, 20, 15 * 60_000)) {
      res.status(429).json({
        ok: false,
        error: 'Terlalu banyak permintaan dari alamat IP ini. Coba lagi nanti.',
      });
      return;
    }

    const rawEmail = typeof req.body?.email === 'string' ? req.body.email : '';
    const email = rawEmail.trim().toLowerCase();
    const purpose = typeof req.body?.purpose === 'string' ? req.body.purpose : 'verify';

    if (!email || !EMAIL_REGEX.test(email)) {
      res.status(400).json({ ok: false, error: 'Alamat email tidak valid.' });
      return;
    }

    const existing = otpStore.get(email);
    const now = Date.now();
    if (existing && now - existing.lastSentAt < 60_000) {
      const waitSec = Math.ceil((60_000 - (now - existing.lastSentAt)) / 1000);
      res.status(429).json({
        ok: false,
        error: `Tunggu ${waitSec} detik sebelum mengirim ulang kode OTP.`,
        retryAfterSeconds: waitSec,
      });
      return;
    }

    if (!consumeRateLimit(`email_otp_send:${email}`, 5, 15 * 60_000)) {
      res.status(429).json({
        ok: false,
        error: 'Batas pengiriman OTP untuk email ini tercapai. Coba lagi dalam 15 menit.',
      });
      return;
    }

    const otp = String(crypto.randomInt(100000, 1000000));
    const otpHash = hashOtp(email, otp);
    otpStore.set(email, {
      otpHash,
      expiresAt: now + 5 * 60_000,
      attempts: 0,
      lastSentAt: now,
      purpose,
    });

    const smtp = readSmtpConfig();
    if (smtp.configured) {
      try {
        const transporter = nodemailer.createTransport({
          host: smtp.host,
          port: smtp.port,
          secure: smtp.port === 465,
          auth: {
            user: smtp.username,
            pass: smtp.password,
          },
          tls: {
            rejectUnauthorized: true,
          },
        });

        await transporter.sendMail({
          from: `"Word Explorer" <${smtp.fromEmail}>`,
          to: email,
          subject: 'Kode Verifikasi OTP Word Explorer',
          text: `Halo Penjelajah!\n\nKode OTP verifikasi akun Word Explorer kamu adalah: ${otp}\n\nKode ini berlaku selama 5 menit. Jangan bagikan kode ini kepada siapa pun.`,
          html: `
            <div style="font-family: sans-serif; max-width: 480px; margin: 0 auto; padding: 24px; background: #FFFDF7; border: 2px solid #15370D; border-radius: 16px;">
              <h2 style="color: #15370D; margin-top: 0;">Word Explorer — Verifikasi Email</h2>
              <p style="color: #2D4D23;">Gunakan kode OTP 6-digit berikut untuk memverifikasi akun petualanganmu:</p>
              <div style="font-size: 28px; font-weight: 800; letter-spacing: 6px; text-align: center; padding: 14px; background: #FFF2C4; border: 1.5px solid #D9A441; border-radius: 12px; color: #15370D; margin: 16px 0;">
                ${otp}
              </div>
              <p style="color: #53714B; font-size: 13px;">Kode berlaku selama 5 menit. Jika kamu tidak meminta kode ini, abaikan email ini.</p>
            </div>
          `,
        });

        res.json({
          ok: true,
          delivery: 'smtp',
          expiresInSeconds: 300,
        });
        return;
      } catch (err) {
        otpStore.delete(email);
        console.error('[WordExplorer Server] SMTP send error:', err instanceof Error ? err.message : err);
        res.status(502).json({
          ok: false,
          error: 'Gagal mengirim email OTP melalui server SMTP. Periksa konfigurasi SMTP.',
        });
        return;
      }
    }

    if (isProduction) {
      otpStore.delete(email);
      res.status(503).json({
        ok: false,
        error: 'Layanan pengiriman email OTP (SMTP) belum dikonfigurasi pada server.',
      });
      return;
    }

    // Development-only sandbox fallback when SMTP is intentionally not set
    res.json({
      ok: true,
      delivery: 'sandbox',
      expiresInSeconds: 300,
      sandboxCode: otp,
    });
  });

  // Verify 6-digit email OTP
  app.post('/api/auth/otp/verify', (req: Request, res: Response) => {
    const ip = getClientIp(req);
    if (!consumeRateLimit(`ip_otp_verify:${ip}`, 30, 15 * 60_000)) {
      res.status(429).json({
        ok: false,
        error: 'Terlalu banyak percobaan verifikasi. Tunggu beberapa menit.',
      });
      return;
    }

    const rawEmail = typeof req.body?.email === 'string' ? req.body.email : '';
    const email = rawEmail.trim().toLowerCase();
    const code = typeof req.body?.code === 'string' ? req.body.code.trim() : '';

    if (!email || !EMAIL_REGEX.test(email) || !/^\d{6}$/.test(code)) {
      res.status(400).json({ ok: false, error: 'Masukkan email dan kode OTP 6-digit yang valid.' });
      return;
    }

    const record = otpStore.get(email);
    if (!record) {
      res.status(400).json({ ok: false, error: 'Sesi OTP tidak ditemukan atau sudah digunakan.' });
      return;
    }

    if (record.attempts >= 5) {
      otpStore.delete(email);
      res.status(429).json({
        ok: false,
        error: 'Terlalu banyak percobaan salah (maks 5x). Silakan minta kode OTP baru.',
      });
      return;
    }

    if (Date.now() > record.expiresAt) {
      otpStore.delete(email);
      res.status(400).json({
        ok: false,
        error: 'Kode OTP telah kadaluarsa (> 5 menit). Silakan kirim ulang kode.',
      });
      return;
    }

    const candidateHash = hashOtp(email, code);
    if (!timingSafeHexEquals(candidateHash, record.otpHash)) {
      record.attempts += 1;
      res.status(401).json({
        ok: false,
        error: `Kode OTP tidak cocok (${record.attempts}/5 percobaan gagal).`,
        attemptsRemaining: Math.max(0, 5 - record.attempts),
      });
      return;
    }

    otpStore.delete(email);
    res.json({
      ok: true,
      verified: true,
      email,
    });
  });

  // Verify Google Identity Services ID token
  app.post('/api/auth/google', async (req: Request, res: Response) => {
    const ip = getClientIp(req);
    if (!consumeRateLimit(`ip_google_auth:${ip}`, 30, 15 * 60_000)) {
      res.status(429).json({
        ok: false,
        error: 'Terlalu banyak permintaan autentikasi Google.',
      });
      return;
    }

    if (!googleClient || !GOOGLE_WEB_CLIENT_ID) {
      res.status(503).json({
        ok: false,
        error: 'GOOGLE_WEB_CLIENT_ID belum dikonfigurasi pada server.',
      });
      return;
    }

    const idToken = typeof req.body?.idToken === 'string' ? req.body.idToken.trim() : '';
    if (!idToken) {
      res.status(400).json({ ok: false, error: 'Token kredensial Google tidak ditemukan.' });
      return;
    }

    try {
      const ticket = await googleClient.verifyIdToken({
        idToken,
        audience: GOOGLE_WEB_CLIENT_ID,
      });
      const payload = ticket.getPayload();
      if (!payload?.email || !payload.email_verified) {
        res.status(401).json({
          ok: false,
          error: 'Email akun Google belum terverifikasi.',
        });
        return;
      }

      const email = payload.email.trim().toLowerCase();
      const displayName = (payload.name ?? email.split('@')[0] ?? 'Explorer').trim();
      res.json({
        ok: true,
        user: {
          sub: payload.sub,
          email,
          displayName,
        },
      });
    } catch (err) {
      console.error('[WordExplorer Server] Google verify error:', err instanceof Error ? err.message : err);
      res.status(401).json({
        ok: false,
        error: 'Verifikasi token Google gagal atau kadaluarsa.',
      });
    }
  });

  app.use('/api/*', (_req: Request, res: Response) => {
    res.status(404).json({ ok: false, error: 'Endpoint API tidak ditemukan.' });
  });

  if (!isProduction) {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: {
        middlewareMode: true,
        hmr: false,
      },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    const distDir = path.resolve(__dirname, 'dist');
    const indexHtml = path.join(distDir, 'index.html');
    app.use(
      '/assets',
      express.static(path.join(distDir, 'assets'), {
        maxAge: '365d',
        immutable: true,
      })
    );
    app.use(
      express.static(distDir, {
        index: false,
        maxAge: '1h',
      })
    );
    app.get('*', (req: Request, res: Response, next: NextFunction) => {
      if (req.path.startsWith('/api/')) {
        next();
        return;
      }
      res.setHeader('Cache-Control', 'no-cache');
      res.sendFile(indexHtml);
    });
  }

  app.use((err: Error, _req: Request, res: Response, _next: NextFunction) => {
    if (err.message.includes('CORS')) {
      res.status(403).json({ ok: false, error: err.message });
      return;
    }
    console.error('[WordExplorer Server] Unhandled error:', err.message);
    res.status(500).json({ ok: false, error: 'Kesalahan internal server.' });
  });

  app.listen(PORT, '0.0.0.0', () => {
    const smtp = readSmtpConfig();
    console.log(
      `[WordExplorer Server] Berjalan di http://0.0.0.0:${PORT} (${isProduction ? 'production' : 'development'}) | SMTP=${smtp.configured} | GoogleOAuth=${Boolean(GOOGLE_WEB_CLIENT_ID)}`
    );
  });
}

void startServer();
