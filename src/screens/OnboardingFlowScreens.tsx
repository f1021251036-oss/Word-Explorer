import { useState } from 'react';
import {
  ArrowForwardIcon,
  CheckmarkIcon,
  CompassIcon,
} from '../components/ExpeditionIcons';
import { LEARNING_PATH_META, type LearningPath } from '../data/models';
import { DIAGNOSTIC_QUESTIONS, QUESTIONNAIRE_QUESTIONS } from '../data/vocabCatalog';

interface OnboardingScreenProps {
  onComplete: () => void;
}

export function OnboardingScreen({ onComplete }: OnboardingScreenProps) {
  const [step, setStep] = useState(0);
  const slides = [
    {
      icon: '🤠',
      title: 'Selamat Datang di Word Explorer!',
      body: 'Game petualangan edukatif untuk siswa SMP. Taklukkan 5 dunia fantasi dan kuasai ribuan kosakata bahasa Inggris dengan seru!',
      cta: 'Selanjutnya',
    },
    {
      icon: '🗺️',
      title: 'Peta Petualangan Interaktif',
      body: 'Jelajahi Green Forest, Lost Island, Ancient Rome, Mountain Passage, dan Crystal Cave. Pecahkan teka-teki kata dan buka pos berikutnya!',
      cta: 'Selanjutnya',
    },
    {
      icon: '🦊',
      title: 'Kuis Adaptif & Sahabat Hewan',
      body: 'Sistem remedial adaptif memastikanmu benar-benar paham. Raih streak harian untuk membuka sahabat hewan mitologis!',
      cta: 'Mulai Kuesioner',
    },
  ];
  const slide = slides[step];

  return (
    <div className="flow-page">
      <main className="flow-container flow-container--centered">
        <div className="onboarding-card">
          <div className="onboarding-emblem" aria-hidden="true">
            <span>{slide.icon}</span>
          </div>
          <h1 className="flow-heading">{slide.title}</h1>
          <p className="flow-subcopy">{slide.body}</p>

          <div className="onboarding-dots" role="tablist" aria-label="Tahap pengenalan">
            {slides.map((_, idx) => (
              <span
                key={idx}
                className={`onboarding-dot ${idx === step ? 'is-active' : ''}`}
              />
            ))}
          </div>

          <button
            type="button"
            className="btn btn--primary flow-cta-btn"
            data-testid="onboarding_continue_button"
            onClick={() => {
              if (step < slides.length - 1) setStep((s) => s + 1);
              else onComplete();
            }}
          >
            <span>{slide.cta}</span>
            <ArrowForwardIcon size={18} />
          </button>
        </div>
      </main>
    </div>
  );
}

interface QuestionnaireScreenProps {
  answers: Record<number, string>;
  onSelectAnswer: (index: number, option: string) => void;
  onComplete: () => void;
}

export function QuestionnaireScreen({
  answers,
  onSelectAnswer,
  onComplete,
}: QuestionnaireScreenProps) {
  const [currentQIndex, setCurrentQIndex] = useState(0);
  const currentQ = QUESTIONNAIRE_QUESTIONS[currentQIndex];
  const selectedAns = answers[currentQIndex] ?? null;
  const pct = Math.round(((currentQIndex + 1) / QUESTIONNAIRE_QUESTIONS.length) * 100);

  return (
    <div className="flow-page">
      <div className="flow-container flow-container--shell">
        <header className="flow-header">
          <div className="flow-header-row">
            <h1 className="flow-title">Kuesioner Pengalaman Belajar</h1>
            <span className="flow-badge">
              {currentQIndex + 1} / {QUESTIONNAIRE_QUESTIONS.length}
            </span>
          </div>
          <div className="flow-progress-track" role="progressbar" aria-valuenow={pct} aria-valuemin={0} aria-valuemax={100}>
            <div className="flow-progress-fill" style={{ width: `${pct}%` }} />
          </div>
        </header>

        <main className="flow-scroll-body">
          <div className="flow-question-card">
            <span className="flow-question-kicker">
              PERTANYAAN {currentQIndex + 1} DARI {QUESTIONNAIRE_QUESTIONS.length}
            </span>
            <h2 className="flow-question-text">{currentQ.question}</h2>
          </div>

          <div className="flow-options-list">
            {currentQ.options.map((opt, idx) => {
              const isSelected = selectedAns === opt;
              return (
                <button
                  key={opt}
                  type="button"
                  className={`flow-option-card ${isSelected ? 'is-selected' : ''}`}
                  data-testid={`option_${opt.slice(0, 8)}`}
                  onClick={() => onSelectAnswer(currentQIndex, opt)}
                >
                  <span className={`option-letter ${isSelected ? 'is-selected' : ''}`}>
                    {String.fromCharCode(65 + idx)}
                  </span>
                  <span className="flow-option-label">{opt}</span>
                  <span className="flow-option-check">
                    {isSelected ? <CheckmarkIcon size={18} /> : <span className="flow-radio-empty" />}
                  </span>
                </button>
              );
            })}
          </div>
        </main>

        <footer className="flow-footer-bar">
          {!selectedAns && (
            <p className="flow-footer-hint">Pilih salah satu jawaban di atas untuk melanjutkan</p>
          )}
          <div className="flow-footer-actions">
            {currentQIndex > 0 && (
              <button
                type="button"
                className="btn btn--secondary"
                data-testid="questionnaire_back_button"
                onClick={() => setCurrentQIndex((i) => Math.max(0, i - 1))}
              >
                Kembali
              </button>
            )}
            <button
              type="button"
              className="btn btn--primary flow-next-btn"
              disabled={!selectedAns}
              data-testid="questionnaire_next_button"
              onClick={() => {
                if (currentQIndex < QUESTIONNAIRE_QUESTIONS.length - 1) {
                  setCurrentQIndex((i) => i + 1);
                } else {
                  onComplete();
                }
              }}
            >
              <span>
                {currentQIndex < QUESTIONNAIRE_QUESTIONS.length - 1
                  ? 'Pertanyaan Berikutnya'
                  : 'Lanjut ke Diagnostic Test'}
              </span>
              {selectedAns && <ArrowForwardIcon size={18} />}
            </button>
          </div>
        </footer>
      </div>
    </div>
  );
}

interface DiagnosticScreenProps {
  qIndex: number;
  score: number;
  finished: boolean;
  recommendedPath: LearningPath;
  onAnswer: (optionIndex: number) => void;
  onStartAdventure: () => void;
}

export function DiagnosticScreen({
  qIndex,
  score,
  finished,
  recommendedPath,
  onAnswer,
  onStartAdventure,
}: DiagnosticScreenProps) {
  const currentQ = DIAGNOSTIC_QUESTIONS[qIndex];
  const pathMeta = LEARNING_PATH_META[recommendedPath];

  if (finished || !currentQ) {
    return (
      <div className="flow-page">
        <main className="flow-container flow-container--centered">
          <div className="diagnostic-result-card">
            <div className="onboarding-emblem" aria-hidden="true">
              <span>🎯</span>
            </div>
            <h1 className="flow-heading">Hasil Diagnostic Test</h1>
            <p className="diagnostic-score-text">
              Skor Kamu: {score} / {DIAGNOSTIC_QUESTIONS.length}
            </p>

            <div className="assigned-path-box">
              <span className="assigned-path-kicker">Jalur Pembelajaranmu:</span>
              <strong className="assigned-path-title">{pathMeta.title}</strong>
              <span className="assigned-path-cefr">
                Standar Target: {pathMeta.cefrLabel} | Target: {pathMeta.targetWordsPerDay} kata/hari
              </span>
              <p className="assigned-path-desc">{pathMeta.description}</p>
            </div>

            <p className="diagnostic-disclaimer">
              Catatan: Hasil ini adalah panduan awal belajar di Word Explorer, bukan sertifikasi
              resmi CEFR. Kamu tetap dapat berpindah jalur kapan saja sesuai cakupan aksesmu.
            </p>

            <button
              type="button"
              className="btn btn--primary flow-cta-btn"
              data-testid="enter_adventure_button"
              onClick={onStartAdventure}
            >
              <CompassIcon size={20} />
              <span>Masuki Peta Petualangan!</span>
            </button>
          </div>
        </main>
      </div>
    );
  }

  const pct = Math.round(((qIndex + 1) / DIAGNOSTIC_QUESTIONS.length) * 100);

  return (
    <div className="flow-page">
      <div className="flow-container flow-container--shell">
        <header className="flow-header">
          <div className="flow-header-row">
            <div>
              <h1 className="flow-title">Diagnostic Test</h1>
              <p className="flow-subtitle">Menentukan jalur belajar yang paling sesuai</p>
            </div>
            <span className="flow-badge">
              Soal {qIndex + 1}/{DIAGNOSTIC_QUESTIONS.length}
            </span>
          </div>
          <div className="flow-progress-track" role="progressbar" aria-valuenow={pct} aria-valuemin={0} aria-valuemax={100}>
            <div className="flow-progress-fill flow-progress-fill--leaf" style={{ width: `${pct}%` }} />
          </div>
        </header>

        <main className="flow-scroll-body">
          <div className="flow-question-card">
            <span className="pill-badge pill-badge--available">CEFR {currentQ.cefrLevel}</span>
            <h2 className="flow-question-text" style={{ marginTop: '10px' }}>
              {currentQ.prompt}
            </h2>
          </div>

          <div className="flow-options-list">
            {currentQ.options.map((optionText, optIndex) => (
              <button
                key={optionText}
                type="button"
                className="diag-option-card"
                data-testid={`diag_opt_${optIndex}`}
                onClick={() => onAnswer(optIndex)}
              >
                <span className="option-letter">{String.fromCharCode(65 + optIndex)}</span>
                <span className="diag-option-text">{optionText}</span>
              </button>
            ))}
          </div>
        </main>
      </div>
    </div>
  );
}
