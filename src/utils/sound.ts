/**
 * Synthesized Web Audio API SoundManager + SpeechSynthesis wrapper + crypto helpers.
 * Mirrors SoundManager.kt glockenspiel chords & tactile thuds without copyrighted audio files.
 */
class WebSoundManager {
  private ctx: AudioContext | null = null;
  private enabled = true;
  private gestureHooked = false;

  constructor() {
    this.installFirstGestureUnlock();
  }

  setEnabled(value: boolean) {
    this.enabled = value;
  }

  private installFirstGestureUnlock() {
    if (typeof window === 'undefined' || this.gestureHooked) return;
    this.gestureHooked = true;
    const unlock = () => {
      if (this.ctx && this.ctx.state === 'suspended') {
        void this.ctx.resume().catch(() => {});
      }
      if ('speechSynthesis' in window) {
        try {
          window.speechSynthesis.getVoices();
        } catch {
          // Ignore SpeechSynthesis warmup errors
        }
      }
      window.removeEventListener('pointerdown', unlock, true);
      window.removeEventListener('keydown', unlock, true);
    };
    window.addEventListener('pointerdown', unlock, { capture: true, passive: true });
    window.addEventListener('keydown', unlock, { capture: true, passive: true });
  }

  private getContext(): AudioContext | null {
    if (typeof window === 'undefined') return null;
    const AudioCtx =
      window.AudioContext ||
      (window as unknown as { webkitAudioContext?: typeof AudioContext }).webkitAudioContext;
    if (!AudioCtx) return null;
    if (!this.ctx) {
      try {
        this.ctx = new AudioCtx();
      } catch {
        return null;
      }
    }
    if (this.ctx.state === 'suspended') {
      void this.ctx.resume().catch(() => {});
    }
    return this.ctx;
  }

  private playChimeSequence(frequencies: number[], durationsMs: number[], gainLevel = 0.16) {
    if (!this.enabled) return;
    const ctx = this.getContext();
    if (!ctx) return;
    let offsetSec = ctx.currentTime;
    for (let i = 0; i < frequencies.length; i += 1) {
      const freq = frequencies[i];
      const durSec = (durationsMs[i] ?? 80) / 1000;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = 'triangle';
      osc.frequency.setValueAtTime(freq, offsetSec);

      gain.gain.setValueAtTime(0.001, offsetSec);
      gain.gain.exponentialRampToValueAtTime(gainLevel, offsetSec + 0.015);
      gain.gain.exponentialRampToValueAtTime(0.001, offsetSec + durSec);

      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start(offsetSec);
      osc.stop(offsetSec + durSec + 0.01);
      offsetSec += Math.min(durSec, 0.085);
    }
  }

  private playSweep(startFreq: number, endFreq: number, durMs: number, gainLevel: number) {
    if (!this.enabled) return;
    const ctx = this.getContext();
    if (!ctx) return;
    const now = ctx.currentTime;
    const durSec = durMs / 1000;
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.type = 'sine';
    osc.frequency.setValueAtTime(startFreq, now);
    osc.frequency.exponentialRampToValueAtTime(Math.max(40, endFreq), now + durSec);

    gain.gain.setValueAtTime(gainLevel, now);
    gain.gain.exponentialRampToValueAtTime(0.001, now + durSec);

    osc.connect(gain);
    gain.connect(ctx.destination);
    osc.start(now);
    osc.stop(now + durSec + 0.01);
  }

  playCorrect() {
    this.playChimeSequence([523.25, 659.25, 783.99, 1046.5], [60, 60, 60, 220], 0.17);
  }

  playIncorrect() {
    this.playSweep(180, 110, 160, 0.16);
  }

  playTileTap() {
    this.playSweep(760, 410, 35, 0.09);
  }

  playMatchPair() {
    this.playChimeSequence([880.0, 1318.51], [80, 190], 0.14);
  }

  playVictoryFanfare() {
    this.playChimeSequence([392.0, 523.25, 659.25, 783.99, 1046.5], [80, 80, 80, 100, 340], 0.18);
  }

  playClick() {
    this.playSweep(580, 350, 25, 0.07);
  }
}

export const soundManager = new WebSoundManager();

export function speakWord(word: string): void {
  if (typeof window === 'undefined' || !word.trim()) return;
  try {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(word);
      utterance.lang = 'en-US';
      utterance.rate = 0.92;
      const voices = window.speechSynthesis.getVoices();
      const preferredVoice =
        voices.find((v) => v.lang.toLowerCase() === 'en-us') ??
        voices.find((v) => v.lang.toLowerCase().startsWith('en'));
      if (preferredVoice) {
        utterance.voice = preferredVoice;
      }
      window.speechSynthesis.speak(utterance);
    }
  } catch {
    // Graceful no-op when browser speechSynthesis is blocked or unsupported
  }
}

export async function sha256Hex(input: string): Promise<string> {
  if (typeof window !== 'undefined' && window.crypto?.subtle) {
    try {
      const encoded = new TextEncoder().encode(input);
      const digest = await window.crypto.subtle.digest('SHA-256', encoded);
      return Array.from(new Uint8Array(digest))
        .map((b) => b.toString(16).padStart(2, '0'))
        .join('');
    } catch {
      // Fall back to deterministic JS digest below
    }
  }
  let h1 = 0xdeadbeef ^ input.length;
  let h2 = 0x41c6ce57 ^ input.length;
  for (let i = 0; i < input.length; i += 1) {
    const ch = input.charCodeAt(i);
    h1 = Math.imul(h1 ^ ch, 2654435761);
    h2 = Math.imul(h2 ^ ch, 1597334677);
  }
  h1 = Math.imul(h1 ^ (h1 >>> 16), 2246822507) ^ Math.imul(h2 ^ (h2 >>> 13), 3266489909);
  h2 = Math.imul(h2 ^ (h2 >>> 16), 2246822507) ^ Math.imul(h1 ^ (h1 >>> 13), 3266489909);
  return ((h2 >>> 0).toString(16).padStart(8, '0') + (h1 >>> 0).toString(16).padStart(8, '0')).repeat(4);
}

export function getTodayIsoDate(): string {
  const now = new Date();
  const y = now.getFullYear();
  const m = String(now.getMonth() + 1).padStart(2, '0');
  const d = String(now.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

export function formatTimeUntilMidnight(): string {
  const now = new Date();
  const midnight = new Date(now);
  midnight.setHours(24, 0, 0, 0);
  const diff = Math.max(0, midnight.getTime() - now.getTime());
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const mins = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
  const secs = Math.floor((diff % (1000 * 60)) / 1000);
  return `${String(hours).padStart(2, '0')}:${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
}
