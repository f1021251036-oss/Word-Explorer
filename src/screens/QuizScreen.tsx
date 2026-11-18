import {
  BackpackIcon,
  BookIcon,
  CheckmarkIcon,
  CompassIcon,
  CrossIcon,
  ExitIcon,
  MapIcon,
  ScrollIcon,
  SoundIcon,
  StarIcon,
} from '../components/ExpeditionIcons';
import {
  CategorySortRenderer,
  DragAndMatchRenderer,
  ImageVocabularyRenderer,
  InteractiveFillBlankRenderer,
  ListenAndTypeRenderer,
  MemoryFlipRenderer,
  PictureTapRenderer,
  SentenceBuilderRenderer,
  SentenceCompletionRenderer,
  TrueFalseRenderer,
  WordHuntRenderer,
  WordUnscrambleRenderer,
} from '../components/InteractiveQuizRenderers';
import { ParchmentDossierCard, TactileSignpostButton } from '../components/SignatureWidgets';
import type { QuestionType, QuizUiState } from '../data/models';

interface QuizScreenProps {
  state: QuizUiState;
  isSoundEnabled: boolean;
  onToggleSound: () => void;
  onOptionSelected: (opt: string) => void;
  onCheckAnswer: () => void;
  onNextQuestion: () => void;
  onExit: () => void;
  // Interactive Question State
  matchingSelectedEnglish: string | null;
  matchingAnswers: Record<string, string>;
  onSelectMatchingEnglish: (eng: string) => void;
  onPairMatchingIndonesian: (indo: string) => void;
  onAssignMatchingDirect: (eng: string, indo: string) => void;
  onUnpairMatching: (eng: string) => void;
  typedAnswer: string;
  onTypedAnswerChange: (val: string) => void;
  clueStep: number;
  onAdvanceClue: () => void;
  scrambledSlots: string[];
  availableLetters: Array<{ id: number; char: string }>;
  onPickScrambledLetter: (id: number, char: string) => void;
  onRemoveScrambledLetter: (slotIndex: number) => void;
  onResetScrambledLetters: () => void;
  sentenceBuilderSlots: string[];
  availableSentenceChips: Array<{ id: number; token: string }>;
  onPickSentenceChip: (id: number, token: string) => void;
  onRemoveSentenceChip: (slotIndex: number) => void;
  onResetSentenceBuilder: () => void;
  categoryBucketAssignments: Record<string, string>;
  selectedCategoryWord: string | null;
  onSelectCategoryWord: (word: string) => void;
  onAssignCategoryWord: (word: string, bucket: string) => void;
  onUnassignCategoryWord: (word: string) => void;
  memoryFlippedIndices: number[];
  memoryMatchedCards: string[];
  onFlipMemoryCard: (cardIndex: number, cardText: string, pairs: Record<string, string>) => void;
  selectedTrueFalse: boolean | null;
  onSelectTrueFalse: (value: boolean) => void;
  selectedGridCoords: Array<[number, number]>;
  onToggleGridCoord: (row: number, col: number) => void;
  onClearGridCoords: () => void;
  // Remedial
  onSelectRemedialOption: (opt: string) => void;
  onCheckRemedialAnswer: () => void;
  onRetryRemedialTask: () => void;
  onNextRemedialTask: () => void;
  onSpeak: (word: string) => void;
}

const QUESTION_TYPE_LABEL: Record<QuestionType, string> = {
  DRAG_AND_MATCH: 'PENCOCOKAN KATA',
  FILL_BLANK: 'MELENGKAPI LOG',
  IMAGE_VOCABULARY: 'IDENTIFIKASI VISUAL',
  WORD_UNSCRAMBLE: 'DEKODE HURUF',
  SCRAMBLED_LETTERS: 'DEKODE HURUF',
  SENTENCE_COMPLETION: 'SINTAKSIS KALIMAT',
  MULTIPLE_CHOICE: 'INQUIRI JALUR',
  SENTENCE_BUILDER: 'SUSUN KALIMAT',
  LISTEN_AND_TYPE: 'DENGAR & KETIK',
  PICTURE_TAP: 'KETUK GAMBAR',
  CATEGORY_SORT: 'SORTIR KATEGORI',
  MEMORY_FLIP: 'KARTU MEMORI',
  TRUE_OR_FALSE: 'BENAR ATAU SALAH',
  WORD_HUNT: 'CARI KATA',
};

function renderTypeBadgeIcon(type: QuestionType) {
  switch (type) {
    case 'DRAG_AND_MATCH':
    case 'WORD_HUNT':
      return <MapIcon size={14} />;
    case 'FILL_BLANK':
    case 'SENTENCE_COMPLETION':
    case 'SENTENCE_BUILDER':
      return <ScrollIcon size={14} />;
    case 'WORD_UNSCRAMBLE':
    case 'SCRAMBLED_LETTERS':
    case 'MEMORY_FLIP':
      return <BookIcon size={14} />;
    case 'LISTEN_AND_TYPE':
      return <SoundIcon size={14} enabled />;
    case 'CATEGORY_SORT':
      return <BackpackIcon size={14} />;
    default:
      return <CompassIcon size={14} />;
  }
}

export function QuizScreen(props: QuizScreenProps) {
  const { state } = props;

  if (!state.isQuizFinished) {
    const currentQ = state.questions[state.currentQuestionIndex];
    if (!currentQ) return null;
    const totalQuestions = state.questions.length;
    const pct = Math.round(((state.currentQuestionIndex + 1) / Math.max(1, totalQuestions)) * 100);

    const canCheck = (() => {
      switch (currentQ.type) {
        case 'DRAG_AND_MATCH': {
          const pairs = currentQ.matchingPairs ?? {};
          const keys = Object.keys(pairs);
          return keys.length > 0 && Object.keys(props.matchingAnswers).length === keys.length;
        }
        case 'FILL_BLANK':
        case 'LISTEN_AND_TYPE':
          return props.typedAnswer.trim().length > 0;
        case 'WORD_UNSCRAMBLE':
        case 'SCRAMBLED_LETTERS':
          return props.scrambledSlots.length > 0;
        case 'SENTENCE_BUILDER':
          return props.sentenceBuilderSlots.length > 0;
        case 'CATEGORY_SORT': {
          const totalWords = Object.values(currentQ.categoryBuckets ?? {}).reduce(
            (sum, list) => sum + list.length,
            0
          );
          return (
            totalWords > 0 && Object.keys(props.categoryBucketAssignments).length === totalWords
          );
        }
        case 'MEMORY_FLIP': {
          const count = Object.keys(currentQ.matchingPairs ?? {}).length;
          return count > 0 && props.memoryMatchedCards.length === count * 2;
        }
        case 'TRUE_OR_FALSE':
          return props.selectedTrueFalse !== null;
        case 'WORD_HUNT':
          return props.selectedGridCoords.length === currentQ.targetWord.length;
        default:
          return state.selectedOption !== null;
      }
    })();

    return (
      <div className="quiz-page">
        <div className="quiz-container">
          {/* Surveyor Header Bar */}
          <header className="quiz-header-bar">
            <div className="quiz-header-actions">
              <button
                type="button"
                className="icon-btn quiz-icon-btn"
                aria-label="Keluar dari kuis ke peta"
                onClick={props.onExit}
                data-testid="exit_quiz_button"
              >
                <ExitIcon size={20} />
              </button>
              <button
                type="button"
                className="icon-btn quiz-icon-btn"
                aria-label={props.isSoundEnabled ? 'Matikan efek suara' : 'Aktifkan efek suara'}
                onClick={props.onToggleSound}
                data-testid="quiz_sound_toggle_button"
              >
                <SoundIcon size={20} enabled={props.isSoundEnabled} />
              </button>
            </div>

            <span className="quiz-stage-seal">
              WILAYAH {state.stage}-{state.level}
            </span>

            <span className="quiz-score-seal">SKOR: {state.firstAttemptScore}/10</span>
          </header>

          {/* Expedition Waypoint Track */}
          <div className="waypoint-track" aria-label={`Pertanyaan ${state.currentQuestionIndex + 1} dari ${totalQuestions}`}>
            <div className="waypoint-track-labels">
              <span>
                LOG POS SURVEYOR {state.currentQuestionIndex + 1} / {totalQuestions}
              </span>
              <span>{pct}% SELESAI</span>
            </div>
            <div className="waypoint-track-bar">
              <div className="waypoint-track-dash" aria-hidden="true" />
              <div
                className="waypoint-track-fill"
                style={{
                  width:
                    totalQuestions > 1
                      ? `${(state.currentQuestionIndex / (totalQuestions - 1)) * 100}%`
                      : '100%',
                }}
                aria-hidden="true"
              />
              <div className="waypoint-dots-row">
                {Array.from({ length: totalQuestions }, (_, idx) => {
                  const isDone = idx < state.currentQuestionIndex;
                  const isCurrent = idx === state.currentQuestionIndex;
                  return (
                    <span
                      key={idx}
                      className={`waypoint-dot ${
                        isCurrent ? 'is-current' : isDone ? 'is-done' : ''
                      }`}
                    />
                  );
                })}
              </div>
            </div>
          </div>

          {/* Question Dossier Sheet */}
          <ParchmentDossierCard className="quiz-prompt-card">
            <div className="quiz-prompt-top">
              <span className="quiz-type-badge">
                {renderTypeBadgeIcon(currentQ.type)}
                <span>{QUESTION_TYPE_LABEL[currentQ.type]}</span>
              </span>
              <button
                type="button"
                className="quiz-listen-badge"
                onClick={() => props.onSpeak(currentQ.targetWord)}
                data-testid="pronunciation_speaker_button"
              >
                <SoundIcon size={14} enabled />
                <span>DENGAR</span>
              </button>
            </div>

            <h1 className="quiz-prompt-heading">{currentQ.prompt}</h1>
            {currentQ.subText && <p className="quiz-prompt-sub">{currentQ.subText}</p>}
          </ParchmentDossierCard>

          {/* Interactive Exercise Renderer */}
          <div className="quiz-renderer-stage">
            {(() => {
              switch (currentQ.type) {
                case 'DRAG_AND_MATCH':
                  return (
                    <DragAndMatchRenderer
                      question={currentQ}
                      matchingSelectedEnglish={props.matchingSelectedEnglish}
                      matchingAnswers={props.matchingAnswers}
                      isAnswerChecked={state.isAnswerChecked}
                      onSelectEnglish={props.onSelectMatchingEnglish}
                      onPairIndonesian={props.onPairMatchingIndonesian}
                      onAssignDirect={props.onAssignMatchingDirect}
                      onUnpair={props.onUnpairMatching}
                      onSpeak={props.onSpeak}
                    />
                  );
                case 'FILL_BLANK':
                  return (
                    <InteractiveFillBlankRenderer
                      question={currentQ}
                      typedAnswer={props.typedAnswer}
                      clueStep={props.clueStep}
                      isAnswerChecked={state.isAnswerChecked}
                      onTypedChange={props.onTypedAnswerChange}
                      onAdvanceClue={props.onAdvanceClue}
                      onSpeak={props.onSpeak}
                      onSubmitEnter={() => {
                        if (!state.isAnswerChecked) {
                          if (canCheck) props.onCheckAnswer();
                        } else {
                          props.onNextQuestion();
                        }
                      }}
                    />
                  );
                case 'WORD_UNSCRAMBLE':
                case 'SCRAMBLED_LETTERS':
                  return (
                    <WordUnscrambleRenderer
                      slots={props.scrambledSlots}
                      availableLetters={props.availableLetters}
                      isAnswerChecked={state.isAnswerChecked}
                      onPickLetter={props.onPickScrambledLetter}
                      onRemoveSlot={props.onRemoveScrambledLetter}
                      onReset={props.onResetScrambledLetters}
                    />
                  );
                case 'SENTENCE_BUILDER':
                  return (
                    <SentenceBuilderRenderer
                      slots={props.sentenceBuilderSlots}
                      availableChips={props.availableSentenceChips}
                      isAnswerChecked={state.isAnswerChecked}
                      onPickChip={props.onPickSentenceChip}
                      onRemoveSlot={props.onRemoveSentenceChip}
                      onReset={props.onResetSentenceBuilder}
                    />
                  );
                case 'LISTEN_AND_TYPE':
                  return (
                    <ListenAndTypeRenderer
                      question={currentQ}
                      typedAnswer={props.typedAnswer}
                      clueStep={props.clueStep}
                      isAnswerChecked={state.isAnswerChecked}
                      onTypedChange={props.onTypedAnswerChange}
                      onAdvanceClue={props.onAdvanceClue}
                      onSpeak={props.onSpeak}
                      onSubmitEnter={() => {
                        if (!state.isAnswerChecked) {
                          if (canCheck) props.onCheckAnswer();
                        } else {
                          props.onNextQuestion();
                        }
                      }}
                    />
                  );
                case 'PICTURE_TAP':
                  return (
                    <PictureTapRenderer
                      question={currentQ}
                      selectedOption={state.selectedOption}
                      isAnswerChecked={state.isAnswerChecked}
                      isAnswerCorrect={state.isAnswerCorrect}
                      onSelectOption={props.onOptionSelected}
                    />
                  );
                case 'CATEGORY_SORT':
                  return (
                    <CategorySortRenderer
                      question={currentQ}
                      assignments={props.categoryBucketAssignments}
                      selectedWord={props.selectedCategoryWord}
                      isAnswerChecked={state.isAnswerChecked}
                      onSelectWord={props.onSelectCategoryWord}
                      onAssignWord={props.onAssignCategoryWord}
                      onUnassignWord={props.onUnassignCategoryWord}
                    />
                  );
                case 'MEMORY_FLIP':
                  return (
                    <MemoryFlipRenderer
                      question={currentQ}
                      flippedIndices={props.memoryFlippedIndices}
                      matchedCards={props.memoryMatchedCards}
                      isAnswerChecked={state.isAnswerChecked}
                      onFlipCard={props.onFlipMemoryCard}
                    />
                  );
                case 'TRUE_OR_FALSE':
                  return (
                    <TrueFalseRenderer
                      question={currentQ}
                      selectedTrueFalse={props.selectedTrueFalse}
                      isAnswerChecked={state.isAnswerChecked}
                      isAnswerCorrect={state.isAnswerCorrect}
                      onSelectTrueFalse={props.onSelectTrueFalse}
                    />
                  );
                case 'WORD_HUNT':
                  return (
                    <WordHuntRenderer
                      question={currentQ}
                      selectedCoords={props.selectedGridCoords}
                      isAnswerChecked={state.isAnswerChecked}
                      onToggleCoord={props.onToggleGridCoord}
                      onClearCoords={props.onClearGridCoords}
                    />
                  );
                case 'IMAGE_VOCABULARY':
                  return (
                    <ImageVocabularyRenderer
                      question={currentQ}
                      selectedOption={state.selectedOption}
                      isAnswerChecked={state.isAnswerChecked}
                      isAnswerCorrect={state.isAnswerCorrect}
                      onOptionSelected={props.onOptionSelected}
                    />
                  );
                default:
                  return (
                    <SentenceCompletionRenderer
                      question={currentQ}
                      selectedOption={state.selectedOption}
                      isAnswerChecked={state.isAnswerChecked}
                      isAnswerCorrect={state.isAnswerCorrect}
                      onOptionSelected={props.onOptionSelected}
                    />
                  );
              }
            })()}
          </div>

          {/* Immediate Feedback Card */}
          {state.isAnswerChecked && (
            <div
              className={`quiz-feedback-card ${state.isAnswerCorrect ? 'is-correct' : 'is-wrong'}`}
              role="status"
            >
              <div className="quiz-feedback-head">
                {state.isAnswerCorrect ? <CheckmarkIcon size={20} /> : <CrossIcon size={20} />}
                <strong>
                  {state.isAnswerCorrect
                    ? 'Langkah Tepat Terverifikasi!'
                    : 'Analisis Jalur Kurang Tepat!'}
                </strong>
              </div>
              <p className="quiz-feedback-body">
                {state.isAnswerCorrect
                  ? currentQ.explanation
                  : currentQ.failureFeedback ?? currentQ.explanation}
              </p>
            </div>
          )}

          {/* Action CTA */}
          <div className="quiz-action-footer">
            {!state.isAnswerChecked ? (
              <TactileSignpostButton
                label="PERIKSA JAWABAN"
                onClick={props.onCheckAnswer}
                disabled={!canCheck}
                testTag="quiz_check_button"
              />
            ) : (
              <TactileSignpostButton
                label={
                  state.currentQuestionIndex < totalQuestions - 1
                    ? 'PERTANYAAN BERIKUTNYA'
                    : 'SELESAIKAN MISI WILAYAH'
                }
                onClick={props.onNextQuestion}
                testTag="quiz_next_button"
              />
            )}
          </div>
        </div>
      </div>
    );
  }

  /* =========================================================
     REMEDIAL OBLIGATION MODE
     ========================================================= */
  if (state.isRemedialMode && !state.isLevelCompleted) {
    const task = state.remedialTasks[state.currentRemedialIndex];
    if (!task) return null;

    return (
      <div className="quiz-page">
        <div className="quiz-container">
          <header className="quiz-header-bar">
            <button
              type="button"
              className="icon-btn quiz-icon-btn"
              aria-label="Keluar dari remedial ke peta"
              onClick={props.onExit}
            >
              <ExitIcon size={20} />
            </button>
            <span className="quiz-stage-seal">
              PEMBETULAN {state.currentRemedialIndex + 1}/{state.initialRemedialCount}
            </span>
            <span className="quiz-score-seal">SKOR AWAL: {state.firstAttemptScore}/10</span>
          </header>

          <ParchmentDossierCard>
            <div className="remedial-banner-row">
              <span className="remedial-compass-seal">
                <CompassIcon size={22} />
              </span>
              <div>
                <h1 className="dossier-card-heading">MISI PEMBETULAN JALUR</h1>
                <p className="dossier-subtext">
                  Kuasai kata kunci untuk membuka kembali jalur peta (
                  {state.currentRemedialIndex + 1}/{state.initialRemedialCount})
                </p>
              </div>
            </div>
          </ParchmentDossierCard>

          <ParchmentDossierCard>
            <h2 className="quiz-prompt-heading">{task.question.prompt}</h2>
            {task.question.subText && (
              <p className="quiz-prompt-sub">{task.question.subText}</p>
            )}
          </ParchmentDossierCard>

          <div className="quiz-options-list">
            {task.question.options.map((opt, idx) => {
              const isSelected = state.remedialSelectedOption === opt;
              const isRight = opt === task.question.correctAnswer;
              let stateClass = '';
              if (state.isRemedialChecked) {
                if (isRight) stateClass = 'is-correct';
                else if (isSelected && !state.isRemedialCorrect) stateClass = 'is-wrong';
              } else if (isSelected) {
                stateClass = 'is-selected';
              }
              return (
                <button
                  key={opt}
                  type="button"
                  disabled={state.isRemedialChecked}
                  className={`quiz-option-btn ${stateClass}`}
                  onClick={() => props.onSelectRemedialOption(opt)}
                  data-testid={`remedial_option_${idx}`}
                >
                  <span className={`option-letter ${isSelected ? 'is-selected' : ''}`}>
                    {String.fromCharCode(65 + idx)}
                  </span>
                  <span className="quiz-option-label">{opt}</span>
                </button>
              );
            })}
          </div>

          <div className="quiz-action-footer">
            {!state.isRemedialChecked ? (
              <TactileSignpostButton
                label="PERIKSA JAWABAN REMEDIAL"
                onClick={props.onCheckRemedialAnswer}
                disabled={state.remedialSelectedOption === null}
                testTag="remedial_check_button"
              />
            ) : state.isRemedialCorrect ? (
              <TactileSignpostButton
                label="LANJUT KE MATERI BERIKUTNYA"
                onClick={props.onNextRemedialTask}
                testTag="remedial_next_button"
              />
            ) : (
              <TactileSignpostButton
                label="COBA LAGI"
                variant="terracotta"
                onClick={props.onRetryRemedialTask}
                testTag="remedial_retry_button"
              />
            )}
          </div>
        </div>
      </div>
    );
  }

  /* =========================================================
     LEVEL CELEBRATION CERTIFICATE
     ========================================================= */
  return (
    <div className="quiz-page quiz-page--celebration">
      <div className="quiz-container quiz-container--narrow">
        <ParchmentDossierCard className="celebration-card">
          <div className="celebration-seal">
            <StarIcon size={40} />
          </div>
          <span className="celebration-kicker">SERTIFIKAT WILAYAH</span>
          <h1 className="celebration-title">Wilayah Berhasil Dikuasai!</h1>
          <p className="celebration-body">
            Misi Stage {state.stage}-{state.level} (Skor Pertama: {state.firstAttemptScore}/10)
            berhasil disurvei dan dicatat dalam jurnal kanonikal.
          </p>

          <div className="celebration-stars-row" aria-label={`${state.starsAwarded} dari 3 bintang`}>
            {[0, 1, 2].map((idx) => {
              const earned = idx < state.starsAwarded;
              return (
                <span key={idx} className={`celebration-star-pip ${earned ? 'is-earned' : ''}`}>
                  <StarIcon size={26} filled={earned} />
                </span>
              );
            })}
          </div>

          <p className="celebration-stars-label">
            {state.starsAwarded} DARI 3 BINTANG DIPEROLEH
          </p>

          <TactileSignpostButton
            label="KEMBALI KE PETA"
            onClick={props.onExit}
            testTag="celebration_card_back_button"
          />
        </ParchmentDossierCard>
      </div>
    </div>
  );
}
