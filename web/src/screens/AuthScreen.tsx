import { useEffect, useRef, useState, type FormEvent } from 'react';
import { AccessibleDialog } from '../components/AccessibleDialog';
import {
  CheckmarkIcon,
  CompassIcon,
  EyeIcon,
  EyeOffIcon,
  LockIcon,
  MailIcon,
  PinIcon,
  ShieldIcon,
  UserIcon,
} from '../components/ExpeditionIcons';
import type { AuthScreenMode, BackendConfigStatus } from '../data/models';

interface AuthScreenProps {
  mode: AuthScreenMode;
  email: string;
  password: string;
  displayName: string;
  verificationCode: string;
  activeVerificationEmail: string;
  sentCode: string | null;
  resendCooldownSeconds: number;
  error: string | null;
  successMessage: string | null;
  isLoading: boolean;
  backendStatus: BackendConfigStatus;
  googleWebClientId?: string;
  showBackendDialog: boolean;
  onModeChange: (mode: AuthScreenMode) => void;
  onEmailChange: (value: string) => void;
  onPasswordChange: (value: string) => void;
  onDisplayNameChange: (value: string) => void;
  onVerificationCodeChange: (value: string) => void;
  onLogin: () => void;
  onRegister: () => void;
  onResetPassword: () => void;
  onVerifyCode: () => void;
  onResendCode: () => void;
  onGoogleSignIn: () => void;
  onGoogleCredential?: (idToken: string) => void;
  onQuickDemo: () => void;
  onToggleBackendDialog: (open: boolean) => void;
}

export function AuthScreen({
  mode,
  email,
  password,
  displayName,
  verificationCode,
  activeVerificationEmail,
  sentCode,
  resendCooldownSeconds,
  error,
  successMessage,
  isLoading,
  backendStatus,
  googleWebClientId,
  showBackendDialog,
  onModeChange,
  onEmailChange,
  onPasswordChange,
  onDisplayNameChange,
  onVerificationCodeChange,
  onLogin,
  onRegister,
  onResetPassword,
  onVerifyCode,
  onResendCode,
  onGoogleSignIn,
  onGoogleCredential,
  onQuickDemo,
  onToggleBackendDialog,
}: AuthScreenProps) {
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [gisRendered, setGisRendered] = useState(false);
  const gisContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!googleWebClientId || !onGoogleCredential || !gisContainerRef.current) return;
    if (mode !== 'login' && mode !== 'register') return;
    let attempts = 0;
    const initGis = () => {
      const gis = window.google?.accounts?.id;
      if (!gis || !gisContainerRef.current) return false;
      gis.initialize({
        client_id: googleWebClientId,
        callback: (resp) => {
          if (resp.credential) onGoogleCredential(resp.credential);
        },
      });
      gisContainerRef.current.innerHTML = '';
      gis.renderButton(gisContainerRef.current, {
        theme: 'outline',
        size: 'large',
        text: 'continue_with',
        shape: 'rectangular',
      });
      setGisRendered(true);
      return true;
    };
    if (initGis()) return;
    const timer = window.setInterval(() => {
      attempts += 1;
      if (initGis() || attempts > 20) {
        window.clearInterval(timer);
      }
    }, 300);
    return () => window.clearInterval(timer);
  }, [googleWebClientId, mode, onGoogleCredential]);

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (mode === 'login') onLogin();
    else if (mode === 'register') onRegister();
    else if (mode === 'forgot') onResetPassword();
    else onVerifyCode();
  };

  return (
    <div className="auth-page">
      <div className="auth-layout">
        {/* Left Hero Visual Column (Stacked top banner on mobile/tablet, side panel on desktop) */}
        <section className="auth-hero-card" aria-label="Pengantar Word Explorer">
          <img
            src="/assets/img_login_hero_adventure.jpg"
            alt="Word Explorer Adventure Realm"
            className="auth-hero-img"
            decoding="async"
            fetchPriority="high"
          />
          <div className="auth-hero-scrim" aria-hidden="true" />
          <div className="auth-hero-overlay">
            <span className="auth-hero-kicker">PETUALANGAN KOSAKATA SMP</span>
            <h1 className="auth-hero-title">WORD EXPLORER</h1>
            <p className="auth-hero-sub">
              Jelajahi 5 wilayah fantasi, selesaikan 50 pos survei interaktif, kumpulkan bintang
              masteri, dan buka sahabat hewan legendaris di browser mana saja.
            </p>
          </div>
        </section>

        {/* Right Form Card */}
        <section className="auth-form-col" aria-label="Formulir Masuk dan Pendaftaran">
          <div className="auth-card">
            {(mode === 'login' || mode === 'register') && (
              <div className="auth-tabs" role="tablist" aria-label="Mode akun">
                <button
                  type="button"
                  role="tab"
                  aria-selected={mode === 'login'}
                  className={`auth-tab-btn ${mode === 'login' ? 'is-active' : ''}`}
                  data-testid="tab_login"
                  onClick={() => onModeChange('login')}
                >
                  Masuk
                </button>
                <button
                  type="button"
                  role="tab"
                  aria-selected={mode === 'register'}
                  className={`auth-tab-btn ${mode === 'register' ? 'is-active' : ''}`}
                  data-testid="tab_register"
                  onClick={() => onModeChange('register')}
                >
                  Daftar
                </button>
              </div>
            )}

            {error && (
              <div className="auth-alert auth-alert--error" role="alert">
                {error}
              </div>
            )}
            {successMessage && (
              <div className="auth-alert auth-alert--success" role="status">
                {successMessage}
              </div>
            )}

            <form onSubmit={handleSubmit} className="auth-form">
              {mode === 'verify' ? (
                <>
                  <h2 className="auth-section-heading">Verifikasi Kode OTP Email</h2>
                  <p className="auth-muted-copy">
                    Masukkan 6-digit kode OTP yang dikirimkan ke: <strong>{activeVerificationEmail}</strong>
                  </p>

                  <div className="auth-info-box">
                    <div className="auth-info-head">
                      <ShieldIcon size={16} />
                      <strong>Keamanan OTP SHA-256</strong>
                    </div>
                    <ul className="auth-bullet-list">
                      <li>Berlaku 5 menit sejak dikirim</li>
                      <li>Maksimal 5 kali percobaan salah</li>
                      <li>Terikat pada akun kanonikal Word Explorer</li>
                    </ul>
                  </div>

                  {!backendStatus.smtpConfigured && sentCode && (
                    <div className="auth-sandbox-box">
                      <span className="auth-sandbox-kicker">Sandbox Developer Inspection</span>
                      <strong>SMTP belum aktif di server. Kode OTP uji: {sentCode}</strong>
                      <small>Validasi tetap memakai hash &amp; batas kadaluarsa 5 menit.</small>
                    </div>
                  )}

                  <label className="auth-field-label" htmlFor="auth-otp-input">
                    Kode OTP (6-Digit)
                  </label>
                  <div className="auth-input-wrap">
                    <PinIcon size={18} />
                    <input
                      id="auth-otp-input"
                      type="text"
                      inputMode="numeric"
                      maxLength={6}
                      value={verificationCode}
                      onChange={(e) =>
                        onVerificationCodeChange(e.target.value.replace(/\D/g, '').slice(0, 6))
                      }
                      placeholder="123456"
                      className="auth-input"
                      data-testid="auth_verification_code_field"
                    />
                  </div>

                  <button
                    type="submit"
                    className="btn btn--primary auth-submit-btn"
                    disabled={isLoading || verificationCode.length !== 6}
                    data-testid="verify_code_submit_button"
                  >
                    {isLoading ? 'Memverifikasi...' : 'Verifikasi OTP & Masuk'}
                  </button>

                  <div className="auth-verify-links">
                    <button
                      type="button"
                      className="auth-text-btn"
                      disabled={resendCooldownSeconds > 0 || isLoading}
                      onClick={onResendCode}
                      data-testid="resend_code_button"
                    >
                      {resendCooldownSeconds > 0
                        ? `Kirim Ulang (${resendCooldownSeconds} dtk)`
                        : 'Kirim Ulang OTP'}
                    </button>
                    <button
                      type="button"
                      className="auth-text-btn auth-text-btn--gold"
                      onClick={() => onModeChange('login')}
                    >
                      Kembali ke Masuk
                    </button>
                  </div>
                </>
              ) : (
                <>
                  {mode === 'register' && (
                    <div className="auth-field-group">
                      <label className="auth-field-label" htmlFor="auth-name-input">
                        Nama Lengkap Siswa
                      </label>
                      <div className="auth-input-wrap">
                        <UserIcon size={18} />
                        <input
                          id="auth-name-input"
                          type="text"
                          value={displayName}
                          onChange={(e) => onDisplayNameChange(e.target.value)}
                          placeholder="Contoh: Arka Pratama"
                          className="auth-input"
                          data-testid="auth_name_field"
                        />
                      </div>
                    </div>
                  )}

                  <div className="auth-field-group">
                    <label className="auth-field-label" htmlFor="auth-email-input">
                      Alamat Email Aktif
                    </label>
                    <div className="auth-input-wrap">
                      <MailIcon size={18} />
                      <input
                        id="auth-email-input"
                        type="email"
                        autoComplete="email"
                        value={email}
                        onChange={(e) => onEmailChange(e.target.value)}
                        placeholder="nama@gmail.com"
                        className="auth-input"
                        data-testid="auth_email_field"
                      />
                    </div>
                  </div>

                  <div className="auth-field-group">
                    <label className="auth-field-label" htmlFor="auth-password-input">
                      {mode === 'forgot' ? 'Kata Sandi Baru' : 'Kata Sandi'}
                    </label>
                    <div className="auth-input-wrap">
                      <LockIcon size={18} />
                      <input
                        id="auth-password-input"
                        type={passwordVisible ? 'text' : 'password'}
                        autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
                        value={password}
                        onChange={(e) => onPasswordChange(e.target.value)}
                        placeholder="Minimal 6 karakter"
                        className="auth-input"
                        data-testid="auth_password_field"
                      />
                      <button
                        type="button"
                        className="auth-eye-btn"
                        aria-label={passwordVisible ? 'Sembunyikan kata sandi' : 'Tampilkan kata sandi'}
                        onClick={() => setPasswordVisible((v) => !v)}
                      >
                        {passwordVisible ? <EyeIcon size={18} /> : <EyeOffIcon size={18} />}
                      </button>
                    </div>
                  </div>

                  {mode === 'login' && (
                    <div className="auth-forgot-row">
                      <button
                        type="button"
                        className="auth-text-btn auth-text-btn--gold"
                        onClick={() => onModeChange('forgot')}
                        data-testid="forgot_password_button"
                      >
                        Lupa Kata Sandi?
                      </button>
                    </div>
                  )}

                  <button
                    type="submit"
                    className="btn btn--primary auth-submit-btn"
                    disabled={isLoading}
                    data-testid="auth_submit_button"
                  >
                    {isLoading
                      ? 'Memproses...'
                      : mode === 'login'
                      ? 'Masuk ke Petualangan'
                      : mode === 'register'
                      ? 'Daftar Akun Baru (Kirim OTP)'
                      : 'Simpan Kata Sandi Baru'}
                  </button>

                  {mode === 'forgot' && (
                    <button
                      type="button"
                      className="auth-text-btn"
                      onClick={() => onModeChange('login')}
                    >
                      Kembali ke Masuk
                    </button>
                  )}

                  <div className="auth-divider">
                    <span>ATAU</span>
                  </div>

                  {googleWebClientId && (
                    <div
                      ref={gisContainerRef}
                      style={{ display: gisRendered ? 'flex' : 'none', justifyContent: 'center', marginBottom: '4px' }}
                    />
                  )}

                  {!gisRendered && (
                    <button
                      type="button"
                      className="btn auth-google-btn"
                      onClick={onGoogleSignIn}
                      data-testid="google_signin_button"
                    >
                      <span aria-hidden="true">G</span>
                      <span>Masuk dengan Google (Akun Kanonikal)</span>
                    </button>
                  )}

                  <button
                    type="button"
                    className="btn auth-demo-btn"
                    onClick={onQuickDemo}
                    data-testid="quick_demo_button"
                  >
                    <CompassIcon size={18} />
                    <span>Jelajahi Demo (Arka Explorer)</span>
                  </button>
                </>
              )}
            </form>
          </div>

          <button
            type="button"
            className="auth-status-btn"
            onClick={() => onToggleBackendDialog(true)}
            data-testid="backend_status_button"
          >
            <ShieldIcon size={16} />
            <span>Status Backend &amp; Keamanan Integrasi</span>
          </button>

          <p className="auth-footer-note">
            Dirancang untuk siswa SMP belajar kosakata bahasa Inggris dengan seru dan mandiri.
          </p>
        </section>
      </div>

      {showBackendDialog && (
        <AccessibleDialog
          title="Status Konfigurasi Backend"
          onClose={() => onToggleBackendDialog(false)}
          maxWidthPx={600}
        >
          <p style={{ marginTop: 0, color: 'var(--color-text-muted-on-paper)', fontSize: '0.88rem' }}>
            Keterbukaan status layanan autentikasi, pengiriman email OTP, dan batasan harian pada versi web:
          </p>
          <div className="backend-status-list">
            <StatusRow
              title="Google OAuth 2.0 Web Client ID"
              active={backendStatus.googleOAuthClientIdConfigured}
              details={
                backendStatus.googleOAuthClientIdConfigured
                  ? `Terkonfigurasi (${backendStatus.googleOAuthClientIdPreview ?? 'Server siap'}...)`
                  : 'Isi VITE_GOOGLE_WEB_CLIENT_ID & GOOGLE_WEB_CLIENT_ID di environment server.'
              }
            />
            <StatusRow
              title="Layanan Email Transaksional (SMTP)"
              active={backendStatus.smtpConfigured}
              details={
                backendStatus.smtpConfigured
                  ? 'Aktif di server backend (/api/auth/otp/send). Kredensial terlindung di server.'
                  : 'Isi SMTP_HOST, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD di environment server (/server).'
              }
            />
            <StatusRow
              title="Validasi OTP Enkripsi"
              active
              details={backendStatus.otpAlgorithm}
            />
            <StatusRow
              title="Penautan Akun Kanonikal"
              active
              details={backendStatus.accountLinkingStatus}
            />
            <StatusRow
              title="Batas Petualangan Harian"
              active
              details={backendStatus.dailyLimitStatus}
            />
          </div>
          <div style={{ marginTop: '16px', display: 'flex', justifyContent: 'flex-end' }}>
            <button
              type="button"
              className="btn btn--primary"
              onClick={() => onToggleBackendDialog(false)}
            >
              Tutup
            </button>
          </div>
        </AccessibleDialog>
      )}
    </div>
  );
}

function StatusRow({
  title,
  active,
  details,
}: {
  title: string;
  active: boolean;
  details: string;
}) {
  return (
    <div className={`backend-status-item ${active ? 'is-active' : 'is-pending'}`}>
      <div className="backend-status-top">
        <strong>{title}</strong>
        <span className={`pill-badge ${active ? 'pill-badge--available' : 'pill-badge--active'}`}>
          {active ? (
            <>
              <CheckmarkIcon size={12} /> AKTIF
            </>
          ) : (
            'PERLU ENV'
          )}
        </span>
      </div>
      <p style={{ margin: '4px 0 0', fontSize: '0.8rem', color: 'var(--color-text-muted-on-paper)' }}>{details}</p>
    </div>
  );
}
