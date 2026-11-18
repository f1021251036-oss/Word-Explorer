# Word Explorer (React + Express Web App)

Educational English vocabulary adventure game for junior high students, rewritten as a full-stack React + TypeScript + Express web application.

## Core Features

- **Authentication & Verification**: Email/password sign-in and registration with 6-digit SHA-256/HMAC OTP email verification (SMTP or local dev sandbox), password reset, Google Identity Services sign-in, and Quick Demo ("Arka Explorer") mode.
- **Onboarding & Diagnostic Flow**: 3-slide welcome intro, learning preferences questionnaire, and 10-question CEFR placement diagnostic (`LOW` / `MIDDLE` / `HIGH` learning paths).
- **Interactive Adventure Map**: 5 fantasy regions (*Forest*, *Island*, *Ancient Rome*, *Mountain*, *Cave*) with 50 level waypoints, pan/zoom/pinch/keyboard controls, region jump strip, legend, first-time 3-step tour, and daily 3-new-levels limit.
- **14 Interactive Quiz Modes & Adaptive Remedial**: Multiple choice, drag-and-match, fill-in-the-blank, word unscramble, sentence builder, listen-and-type (SpeechSynthesis), picture tap, category sort, memory flip, true/false, and word hunt grid, paired with Web Audio API sound effects and mandatory adaptive remedial review on missed questions.
- **Camp, Lexicon, Lore, Beasts & Settings**: Daily expedition log with midnight reset countdown, searchable daily vocabulary dictionary with pronunciation audio, regional stories with comprehension checks (+25 XP), streak-unlocked animal companions, high-contrast/reduced-motion accessibility toggles, and JSON progress export/import.

## Running Locally

```bash
npm install
npm run dev
```

The server listens on port `3000` (`http://0.0.0.0:3000`), serving both the React frontend (via Vite middleware in development or `dist/` in production) and the `/api/*` authentication endpoints.

## Scripts

- `npm run dev` — Start Express + Vite dev server on port 3000
- `npm run lint` — Type-check frontend and server with `tsc --noEmit`
- `npm run build` — Build production frontend bundle to `dist/`
- `npm start` — Run production server (`NODE_ENV=production node server.ts`)
