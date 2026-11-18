import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { defineConfig, type Plugin } from 'vite';
import react from '@vitejs/plugin-react';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const androidResDir = path.resolve(__dirname, '../app/src/main/res');

const bridgedAssets: Record<string, { src: string; mime: string }> = {
  '/assets/peta_petualangan_portrait.jpg': {
    src: path.join(androidResDir, 'drawable/peta_petualangan_portrait.jpg'),
    mime: 'image/jpeg',
  },
  '/assets/img_login_hero_adventure.jpg': {
    src: path.join(androidResDir, 'drawable/img_login_hero_adventure_1794230784519.jpg'),
    mime: 'image/jpeg',
  },
  '/assets/char_adventurer_explorer.jpg': {
    src: path.join(androidResDir, 'drawable/char_adventurer_explorer_1793116866013.jpg'),
    mime: 'image/jpeg',
  },
  '/assets/fonts/cinzel.ttf': {
    src: path.join(androidResDir, 'font/cinzel.ttf'),
    mime: 'font/ttf',
  },
  '/assets/fonts/plus_jakarta_sans.ttf': {
    src: path.join(androidResDir, 'font/plus_jakarta_sans.ttf'),
    mime: 'font/ttf',
  },
};

function androidAssetsBridgePlugin(): Plugin {
  return {
    name: 'word-explorer-android-assets-bridge',
    configureServer(server) {
      server.middlewares.use((req, res, next) => {
        const cleanUrl = req.url?.split('?')[0] ?? '';
        const asset = bridgedAssets[cleanUrl];
        if (!asset || !fs.existsSync(asset.src)) {
          next();
          return;
        }
        res.setHeader('Content-Type', asset.mime);
        res.setHeader('Cache-Control', 'public, max-age=86400');
        fs.createReadStream(asset.src).pipe(res);
      });
    },
    generateBundle() {
      for (const [publicPath, asset] of Object.entries(bridgedAssets)) {
        if (fs.existsSync(asset.src)) {
          this.emitFile({
            type: 'asset',
            fileName: publicPath.replace(/^\//, ''),
            source: fs.readFileSync(asset.src),
          });
        }
      }
    },
  };
}

export default defineConfig({
  plugins: [react(), androidAssetsBridgePlugin()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks: {
          'react-vendor': ['react', 'react-dom'],
        },
      },
    },
  },
});
