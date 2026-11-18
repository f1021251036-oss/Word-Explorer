import { useMemo, useState, type CSSProperties } from 'react';
import {
  CheckmarkIcon,
  CompassIcon,
  CrossIcon,
  renderPictureTapIcon,
  ResetIcon,
  ScrollIcon,
  SoundIcon,
} from './ExpeditionIcons';
import { ParchmentDossierCard } from './SignatureWidgets';
import { javaStringHashCode, shuffleWithRng } from '../data/questionGenerator';
import type { PictureOption, Question } from '../data/vocabCatalog';

function deterministicRng(seedStr: string): () => number {
  let state = (Math.abs(javaStringHashCode(seedStr)) + 1) >>> 0;
  return () => {
    state = (state * 1664525 + 1013904223) >>> 0;
    return state / 4294967296;
  };
}

/* =========================================================
   TWO-STEP CLUE DRAWER (Shared by FillBlank & ListenAndType)
   ========================================================= */
function ClueDrawer({
  question,
  clueStep,
  onAdvanceClue,
}: {
  question: Question;
  clueStep: number;
  onAdvanceClue: () => void;
}) {
  if (!question.hintsEnabled) {
    return (
      <p className="quiz-no-hint-note">
        Tidak ada kisi-kisi di tingkat master
      </p>
    );
  }
  const clue1 = question.clue ?? question.hint;
  if (!clue1) return null;
  const clue2 =
    question.clue2 ?? `Kata ini terdiri dari ${question.targetWord.length} huruf.`;

  return (
    <div className="clue-drawer">
      <button
        type="button"
        className="clue-toggle-btn"
        onClick={onAdvanceClue}
        data-testid="fill_blank_hint_button"
      >
        <ScrollIcon size={16} />
        <span>
          {clueStep === 0
            ? 'BUTUH KISI-KISI?'
            : clueStep === 1
            ? 'KISI-KISI TAMBAHAN (2/2)'
            : 'TUTUP KISI-KISI'}
        </span>
      </button>

      {clueStep > 0 && (
        <div className="clue-box">
          <p>
            <strong>Petunjuk Fungsi:</strong> {clue1}
          </p>
          {clueStep >= 2 && (
            <p style={{ marginTop: '6px' }}>
              <strong>Detail Tambahan:</strong> {clue2}
            </p>
          )}
        </div>
      )}
    </div>
  );
}

/* =========================================================
   1. DRAG AND MATCH (Mouse DnD + Touch/Click + Keyboard)
   ========================================================= */
export function DragAndMatchRenderer({
  question,
  matchingSelectedEnglish,
  matchingAnswers,
  isAnswerChecked,
  onSelectEnglish,
  onPairIndonesian,
  onAssignDirect,
  onUnpair,
  onSpeak,
}: {
  question: Question;
  matchingSelectedEnglish: string | null;
  matchingAnswers: Record<string, string>;
  isAnswerChecked: boolean;
  onSelectEnglish: (eng: string) => void;
  onPairIndonesian: (indo: string) => void;
  onAssignDirect: (eng: string, indo: string) => void;
  onUnpair: (eng: string) => void;
  onSpeak: (word: string) => void;
}) {
  const [dragHoverIndo, setDragHoverIndo] = useState<string | null>(null);
  const pairs = question.matchingPairs ?? {};
  const englishWords = useMemo(() => Object.keys(pairs), [pairs]);
  const indonesianWords = useMemo(
    () => shuffleWithRng(Object.values(pairs), deterministicRng(`${question.id}_match`)),
    [pairs, question.id]
  );

  return (
    <div className="quiz-block">
      <p className="quiz-instruction">
        Seret kata Inggris ke artinya (mouse) atau ketuk berpasangan (sentuh/keyboard):
      </p>

      <div className="match-columns">
        <div className="match-col">
          {englishWords.map((eng) => {
            const isSelected = matchingSelectedEnglish === eng;
            const isPaired = Boolean(matchingAnswers[eng]);
            return (
              <button
                key={eng}
                type="button"
                draggable={!isAnswerChecked}
                onDragStart={(e) => {
                  e.dataTransfer.setData('text/plain', eng);
                  e.dataTransfer.effectAllowed = 'move';
                }}
                onClick={() => {
                  if (!isAnswerChecked) {
                    onSelectEnglish(eng);
                    onSpeak(eng);
                  }
                }}
                className={`match-card match-card--eng ${isSelected ? 'is-selected' : ''} ${
                  isPaired ? 'is-paired' : ''
                }`}
                data-testid={`match_eng_${eng}`}
              >
                <span className="match-eng-text">{eng}</span>
                <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
                  {isPaired && <CheckmarkIcon size={15} />}
                  {isSelected && !isPaired && (
                    <span className="state-tag state-tag--selected">Dipilih</span>
                  )}
                  <SoundIcon size={16} enabled />
                </span>
              </button>
            );
          })}
        </div>

        <div className="match-col">
          {indonesianWords.map((indo) => {
            const pairedEntry = Object.entries(matchingAnswers).find(([, val]) => val === indo);
            const pairedEng = pairedEntry?.[0] ?? null;
            const isDragOver = dragHoverIndo === indo;

            return (
              <button
                key={indo}
                type="button"
                onDragOver={(e) => {
                  if (!isAnswerChecked) {
                    e.preventDefault();
                    setDragHoverIndo(indo);
                  }
                }}
                onDragLeave={() => setDragHoverIndo((prev) => (prev === indo ? null : prev))}
                onDrop={(e) => {
                  e.preventDefault();
                  setDragHoverIndo(null);
                  if (isAnswerChecked) return;
                  const droppedEng = e.dataTransfer.getData('text/plain');
                  if (droppedEng && englishWords.includes(droppedEng)) {
                    onAssignDirect(droppedEng, indo);
                  }
                }}
                onClick={() => {
                  if (isAnswerChecked) return;
                  if (matchingSelectedEnglish) {
                    onPairIndonesian(indo);
                  } else if (pairedEng) {
                    onUnpair(pairedEng);
                  }
                }}
                className={`match-card match-card--indo ${pairedEng ? 'is-selected' : ''} ${
                  isDragOver ? 'is-drop-hover' : ''
                }`}
                data-testid={`match_indo_${indo}`}
              >
                <span className="match-indo-text">{indo}</span>
                {pairedEng && (
                  <span className="match-paired-tag">
                    ➔ {pairedEng} {!isAnswerChecked && <small>(Batal)</small>}
                  </span>
                )}
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}

/* =========================================================
   2. FILL IN THE BLANK
   ========================================================= */
export function InteractiveFillBlankRenderer({
  question,
  typedAnswer,
  clueStep,
  isAnswerChecked,
  onTypedChange,
  onAdvanceClue,
  onSpeak,
  onSubmitEnter,
}: {
  question: Question;
  typedAnswer: string;
  clueStep: number;
  isAnswerChecked: boolean;
  onTypedChange: (value: string) => void;
  onAdvanceClue: () => void;
  onSpeak: (word: string) => void;
  onSubmitEnter?: () => void;
}) {
  return (
    <div className="quiz-block">
      <div className="quiz-surface-card">
        <span className="quiz-surface-kicker">LENGKAPI LOG KALIMAT SURVEYOR:</span>
        <p className="quiz-sentence-text">{question.sentenceWithBlank ?? question.prompt}</p>
      </div>

      <div className="quiz-input-wrap">
        <input
          type="text"
          value={typedAnswer}
          disabled={isAnswerChecked}
          onChange={(e) => onTypedChange(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter' && onSubmitEnter) {
              e.preventDefault();
              onSubmitEnter();
            }
          }}
          placeholder="Ketik kata bahasa Inggris..."
          aria-label="Ketik jawaban"
          className="quiz-text-input"
          data-testid="fill_blank_input_field"
        />
        <button
          type="button"
          className="quiz-input-speak-btn"
          aria-label="Dengarkan kata kunci"
          onClick={() => onSpeak(question.correctAnswer)}
        >
          <SoundIcon size={18} enabled />
        </button>
      </div>

      {isAnswerChecked && (
        <p className="quiz-answer-key">Kunci Jawaban: {question.correctAnswer}</p>
      )}

      <ClueDrawer question={question} clueStep={clueStep} onAdvanceClue={onAdvanceClue} />
    </div>
  );
}

/* =========================================================
   3. WORD UNSCRAMBLE / SCRAMBLED LETTERS
   ========================================================= */
export function WordUnscrambleRenderer({
  slots,
  availableLetters,
  isAnswerChecked,
  onPickLetter,
  onRemoveSlot,
  onReset,
}: {
  slots: string[];
  availableLetters: Array<{ id: number; char: string }>;
  isAnswerChecked: boolean;
  onPickLetter: (id: number, char: string) => void;
  onRemoveSlot: (slotIndex: number) => void;
  onReset: () => void;
}) {
  return (
    <div className="quiz-block">
      <p className="quiz-instruction">Ketuk huruf di bawah untuk menyusun kata:</p>

      <div className="scramble-slots-box">
        {slots.length === 0 ? (
          <span className="quiz-placeholder-text">Ketuk huruf-huruf di bawah...</span>
        ) : (
          <div className="scramble-row">
            {slots.map((char, idx) => (
              <button
                key={`${idx}_${char}`}
                type="button"
                disabled={isAnswerChecked}
                className="scramble-tile is-placed"
                onClick={() => onRemoveSlot(idx)}
                data-testid={`scramble_slot_${idx}`}
                aria-label={`Hapus huruf ${char} di posisi ${idx + 1}`}
              >
                {char}
              </button>
            ))}
          </div>
        )}
      </div>

      <div className="scramble-row">
        {availableLetters.map((item) => (
          <button
            key={item.id}
            type="button"
            disabled={isAnswerChecked}
            className="scramble-tile is-available"
            onClick={() => onPickLetter(item.id, item.char)}
            data-testid={`letter_tile_${item.id}`}
          >
            {item.char}
          </button>
        ))}
      </div>

      {!isAnswerChecked && slots.length > 0 && (
        <button type="button" className="quiz-reset-btn" onClick={onReset}>
          <ResetIcon size={16} />
          <span>ULANGI SUSUNAN</span>
        </button>
      )}
    </div>
  );
}

/* =========================================================
   4. SENTENCE BUILDER (Mouse DnD + Tap/Click)
   ========================================================= */
export function SentenceBuilderRenderer({
  slots,
  availableChips,
  isAnswerChecked,
  onPickChip,
  onRemoveSlot,
  onReset,
}: {
  slots: string[];
  availableChips: Array<{ id: number; token: string }>;
  isAnswerChecked: boolean;
  onPickChip: (id: number, token: string) => void;
  onRemoveSlot: (slotIndex: number) => void;
  onReset: () => void;
}) {
  const [dropActive, setDropActive] = useState(false);

  return (
    <div className="quiz-block">
      <p className="quiz-instruction">
        Seret atau ketuk kepingan kata berurutan untuk menyusun kalimat ekspedisi:
      </p>

      <div
        className={`sentence-dropzone ${dropActive ? 'is-drop-hover' : ''}`}
        onDragOver={(e) => {
          if (!isAnswerChecked) {
            e.preventDefault();
            setDropActive(true);
          }
        }}
        onDragLeave={() => setDropActive(false)}
        onDrop={(e) => {
          e.preventDefault();
          setDropActive(false);
          if (isAnswerChecked) return;
          const idStr = e.dataTransfer.getData('application/x-chip-id');
          const token = e.dataTransfer.getData('text/plain');
          const idNum = Number(idStr);
          if (Number.isFinite(idNum) && token) {
            onPickChip(idNum, token);
          }
        }}
      >
        {slots.length === 0 ? (
          <span className="quiz-placeholder-text">Kalimat akan tersusun di sini...</span>
        ) : (
          <div className="chip-wrap-row">
            {slots.map((token, idx) => (
              <button
                key={`${idx}_${token}`}
                type="button"
                disabled={isAnswerChecked}
                className="sentence-chip is-placed"
                onClick={() => onRemoveSlot(idx)}
                data-testid={`sentence_slot_${idx}`}
              >
                <span>{token}</span>
                {!isAnswerChecked && <span className="chip-remove-mark">✕</span>}
              </button>
            ))}
          </div>
        )}
      </div>

      <div className="sentence-pool-card">
        <span className="quiz-surface-kicker">KEPINGAN KATA TERSEDIA:</span>
        <div className="chip-wrap-row" style={{ marginTop: '8px' }}>
          {availableChips.map((chip) => (
            <button
              key={chip.id}
              type="button"
              draggable={!isAnswerChecked}
              disabled={isAnswerChecked}
              onDragStart={(e) => {
                e.dataTransfer.setData('application/x-chip-id', String(chip.id));
                e.dataTransfer.setData('text/plain', chip.token);
                e.dataTransfer.effectAllowed = 'move';
              }}
              className="sentence-chip is-available"
              onClick={() => onPickChip(chip.id, chip.token)}
              data-testid={`sentence_chip_${chip.id}`}
            >
              {chip.token}
            </button>
          ))}
        </div>
      </div>

      {!isAnswerChecked && slots.length > 0 && (
        <button type="button" className="quiz-reset-btn" onClick={onReset}>
          <ResetIcon size={16} />
          <span>ULANGI SUSUNAN KALIMAT</span>
        </button>
      )}
    </div>
  );
}

/* =========================================================
   5. LISTEN AND TYPE
   ========================================================= */
export function ListenAndTypeRenderer({
  question,
  typedAnswer,
  clueStep,
  isAnswerChecked,
  onTypedChange,
  onAdvanceClue,
  onSpeak,
  onSubmitEnter,
}: {
  question: Question;
  typedAnswer: string;
  clueStep: number;
  isAnswerChecked: boolean;
  onTypedChange: (value: string) => void;
  onAdvanceClue: () => void;
  onSpeak: (word: string) => void;
  onSubmitEnter?: () => void;
}) {
  return (
    <div className="quiz-block quiz-block--centered">
      <button
        type="button"
        className="listen-big-btn"
        aria-label="Putar audio pelafalan kata"
        onClick={() => onSpeak(question.audioPromptWord ?? question.targetWord)}
        data-testid="listen_audio_replay_button"
      >
        <SoundIcon size={36} enabled />
      </button>
      <p className="quiz-instruction" style={{ textAlign: 'center' }}>
        Dengarkan pelafalan penjelajah dan ketik kata yang terdengar:
      </p>

      <div className="quiz-input-wrap" style={{ width: '100%' }}>
        <input
          type="text"
          value={typedAnswer}
          disabled={isAnswerChecked}
          onChange={(e) => onTypedChange(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter' && onSubmitEnter) {
              e.preventDefault();
              onSubmitEnter();
            }
          }}
          placeholder="Ketik kata yang kamu dengar..."
          aria-label="Ketik kata yang kamu dengar"
          className="quiz-text-input"
          data-testid="listen_type_input_field"
        />
      </div>

      <ClueDrawer question={question} clueStep={clueStep} onAdvanceClue={onAdvanceClue} />
    </div>
  );
}

/* =========================================================
   6. PICTURE TAP (2x2 Vector Grid)
   ========================================================= */
export function PictureTapRenderer({
  question,
  selectedOption,
  isAnswerChecked,
  isAnswerCorrect,
  onSelectOption,
}: {
  question: Question;
  selectedOption: string | null;
  isAnswerChecked: boolean;
  isAnswerCorrect: boolean;
  onSelectOption: (label: string) => void;
}) {
  const options: PictureOption[] = question.pictureOptions ?? [
    { id: 'opt_compass', vectorIconKey: 'compass', label: 'Navigational Compass' },
    { id: 'opt_tree', vectorIconKey: 'tree', label: 'Woodland Pine Tree' },
    { id: 'opt_tent', vectorIconKey: 'tent', label: 'Explorer Camp Tent' },
    { id: 'opt_map', vectorIconKey: 'map', label: 'Surveyor Route Map' },
  ];

  return (
    <div className="picture-tap-grid">
      {options.map((opt, idx) => {
        const isSelected = selectedOption === opt.label;
        const isRight = opt.label === question.correctAnswer;
        let stateClass = '';
        if (isAnswerChecked) {
          if (isRight) stateClass = 'is-correct';
          else if (isSelected && !isAnswerCorrect) stateClass = 'is-wrong';
        } else if (isSelected) {
          stateClass = 'is-selected';
        }
        return (
          <button
            key={opt.id}
            type="button"
            disabled={isAnswerChecked}
            className={`picture-tap-card ${stateClass}`}
            onClick={() => onSelectOption(opt.label)}
            data-testid={`picture_option_${idx}`}
          >
            <span className={`picture-tap-icon ${isSelected ? 'is-selected' : ''}`}>
              {renderPictureTapIcon(opt.vectorIconKey, 34)}
            </span>
            <span className="picture-tap-label">{opt.label}</span>
            {isAnswerChecked && isRight && (
              <span className="state-tag state-tag--correct">
                <CheckmarkIcon size={12} /> Benar
              </span>
            )}
            {isAnswerChecked && isSelected && !isAnswerCorrect && (
              <span className="state-tag state-tag--wrong">
                <CrossIcon size={12} /> Salah
              </span>
            )}
            {!isAnswerChecked && isSelected && (
              <span className="state-tag state-tag--selected">
                <CheckmarkIcon size={12} /> Dipilih
              </span>
            )}
          </button>
        );
      })}
    </div>
  );
}

/* =========================================================
   7. CATEGORY SORT (Mouse DnD + Tap/Click)
   ========================================================= */
export function CategorySortRenderer({
  question,
  assignments,
  selectedWord,
  isAnswerChecked,
  onSelectWord,
  onAssignWord,
  onUnassignWord,
}: {
  question: Question;
  assignments: Record<string, string>;
  selectedWord: string | null;
  isAnswerChecked: boolean;
  onSelectWord: (word: string) => void;
  onAssignWord: (word: string, bucket: string) => void;
  onUnassignWord: (word: string) => void;
}) {
  const [hoveredBucket, setHoveredBucket] = useState<string | null>(null);
  const buckets = question.categoryBuckets ?? {};
  const bucketNames = useMemo(() => Object.keys(buckets), [buckets]);
  const allWords = useMemo(
    () => shuffleWithRng(Object.values(buckets).flat(), deterministicRng(`${question.id}_cat`)),
    [buckets, question.id]
  );
  const unassigned = allWords.filter((w) => !assignments[w]);

  return (
    <div className="quiz-block">
      <p className="quiz-instruction">
        Seret kata ke kantung survei (mouse) atau ketuk kata lalu ketuk kantung tujuan:
      </p>

      <div className="sort-pool-card">
        <span className="quiz-surface-kicker">KATA YANG BELUM DIPILAH:</span>
        {unassigned.length === 0 ? (
          <p className="quiz-placeholder-text" style={{ margin: '8px 0 0' }}>
            Semua kata telah dimasukkan ke dalam kantung!
          </p>
        ) : (
          <div className="chip-wrap-row" style={{ marginTop: '8px' }}>
            {unassigned.map((w) => {
              const isSelected = selectedWord === w;
              return (
                <button
                  key={w}
                  type="button"
                  draggable={!isAnswerChecked}
                  disabled={isAnswerChecked}
                  onDragStart={(e) => {
                    e.dataTransfer.setData('text/plain', w);
                    e.dataTransfer.effectAllowed = 'move';
                  }}
                  onClick={() => onSelectWord(w)}
                  className={`sort-word-chip ${isSelected ? 'is-selected' : ''}`}
                  data-testid={`sort_word_${w}`}
                >
                  {isSelected && <CheckmarkIcon size={14} />}
                  <span>{w}</span>
                </button>
              );
            })}
          </div>
        )}
      </div>

      <div className="sort-buckets-grid">
        {bucketNames.map((bucket) => {
          const assignedWords = Object.entries(assignments)
            .filter(([, b]) => b === bucket)
            .map(([w]) => w);
          const isHover = hoveredBucket === bucket;
          return (
            <div
              key={bucket}
              role="button"
              tabIndex={isAnswerChecked ? -1 : 0}
              onDragOver={(e) => {
                if (!isAnswerChecked) {
                  e.preventDefault();
                  setHoveredBucket(bucket);
                }
              }}
              onDragLeave={() => setHoveredBucket((prev) => (prev === bucket ? null : prev))}
              onDrop={(e) => {
                e.preventDefault();
                setHoveredBucket(null);
                if (isAnswerChecked) return;
                const droppedWord = e.dataTransfer.getData('text/plain');
                if (droppedWord && allWords.includes(droppedWord)) {
                  onAssignWord(droppedWord, bucket);
                }
              }}
              onClick={() => {
                if (!isAnswerChecked && selectedWord) {
                  onAssignWord(selectedWord, bucket);
                }
              }}
              onKeyDown={(e) => {
                if ((e.key === 'Enter' || e.key === ' ') && !isAnswerChecked && selectedWord) {
                  e.preventDefault();
                  onAssignWord(selectedWord, bucket);
                }
              }}
              className={`sort-bucket-box ${selectedWord ? 'is-armed' : ''} ${
                isHover ? 'is-drop-hover' : ''
              }`}
              data-testid={`bucket_${bucket}`}
            >
              <span className="sort-bucket-title">{bucket}</span>
              {assignedWords.length === 0 ? (
                <span className="sort-bucket-empty">Taruh kata di sini</span>
              ) : (
                <div className="sort-bucket-items">
                  {assignedWords.map((w) => (
                    <button
                      key={w}
                      type="button"
                      disabled={isAnswerChecked}
                      className="sort-assigned-chip"
                      onClick={(e) => {
                        e.stopPropagation();
                        if (!isAnswerChecked) onUnassignWord(w);
                      }}
                    >
                      <span>{w}</span>
                      {!isAnswerChecked && <span style={{ color: 'var(--color-error-text)' }}>✕</span>}
                    </button>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

/* =========================================================
   8. MEMORY FLIP
   ========================================================= */
export function MemoryFlipRenderer({
  question,
  flippedIndices,
  matchedCards,
  isAnswerChecked,
  onFlipCard,
}: {
  question: Question;
  flippedIndices: number[];
  matchedCards: string[];
  isAnswerChecked: boolean;
  onFlipCard: (cardIndex: number, cardText: string, pairs: Record<string, string>) => void;
}) {
  const pairs = question.matchingPairs ?? {};
  const allCards = useMemo(
    () =>
      Object.entries(pairs)
        .flatMap(([k, v]) => [k, v])
        .sort(),
    [pairs]
  );

  return (
    <div className="quiz-block">
      <p className="quiz-instruction">Buka kartu dan temukan pasangan kata dengan maknanya:</p>
      <div className="memory-grid">
        {allCards.map((cardText, idx) => {
          const isMatched = matchedCards.includes(cardText);
          const isFlipped = flippedIndices.includes(idx) || isMatched;
          return (
            <button
              key={`${idx}_${cardText}`}
              type="button"
              disabled={isAnswerChecked}
              className={`memory-card ${isMatched ? 'is-matched' : isFlipped ? 'is-flipped' : ''}`}
              onClick={() => onFlipCard(idx, cardText, pairs)}
              data-testid={`memory_card_${idx}`}
              aria-label={isFlipped ? cardText : `Kartu tertutup ${idx + 1}`}
            >
              {isFlipped ? (
                <span className="memory-card-text" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
                  {isMatched && <CheckmarkIcon size={14} />}
                  <span>{cardText}</span>
                </span>
              ) : (
                <CompassIcon size={24} />
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
}

/* =========================================================
   9. TRUE OR FALSE
   ========================================================= */
export function TrueFalseRenderer({
  question,
  selectedTrueFalse,
  isAnswerChecked,
  isAnswerCorrect,
  onSelectTrueFalse,
}: {
  question: Question;
  selectedTrueFalse: boolean | null;
  isAnswerChecked: boolean;
  isAnswerCorrect: boolean;
  onSelectTrueFalse: (value: boolean) => void;
}) {
  const renderChoice = (val: boolean, label: string, testId: string) => {
    const isSelected = selectedTrueFalse === val;
    const isRight = question.isStatementTrue === val;
    let stateClass = '';
    if (isAnswerChecked) {
      if (isRight) stateClass = 'is-correct';
      else if (isSelected && !isAnswerCorrect) stateClass = 'is-wrong';
    } else if (isSelected) {
      stateClass = 'is-selected';
    }
    return (
      <button
        type="button"
        disabled={isAnswerChecked}
        className={`tf-btn ${stateClass}`}
        onClick={() => onSelectTrueFalse(val)}
        data-testid={testId}
      >
        {isAnswerChecked && isRight && <CheckmarkIcon size={18} />}
        {isAnswerChecked && isSelected && !isAnswerCorrect && <CrossIcon size={18} />}
        {!isAnswerChecked && isSelected && <CheckmarkIcon size={18} />}
        <span>{label}</span>
      </button>
    );
  };

  return (
    <div className="quiz-block">
      <div className="quiz-surface-card">
        <span className="quiz-surface-kicker">PERNYATAAN LOG SURVEI:</span>
        <p className="quiz-sentence-text">{question.subText ?? question.prompt}</p>
      </div>
      <div className="tf-grid">
        {renderChoice(true, 'BENAR (TRUE)', 'true_button')}
        {renderChoice(false, 'SALAH (FALSE)', 'false_button')}
      </div>
    </div>
  );
}

/* =========================================================
   10. WORD HUNT (Responsive Grid 6x6 / 8x8)
   ========================================================= */
export function WordHuntRenderer({
  question,
  selectedCoords,
  isAnswerChecked,
  onToggleCoord,
  onClearCoords,
}: {
  question: Question;
  selectedCoords: Array<[number, number]>;
  isAnswerChecked: boolean;
  onToggleCoord: (row: number, col: number) => void;
  onClearCoords: () => void;
}) {
  const grid = question.gridLetters ?? [
    ['F', 'T', 'R', 'E', 'E'],
    ['P', 'O', 'M', 'A', 'P'],
    ['A', 'T', 'O', 'S', 'S'],
    ['T', 'M', 'O', 'S', 'S'],
    ['H', 'W', 'O', 'O', 'D'],
  ];
  const cols = grid[0]?.length ?? 5;
  const currentSpelled = selectedCoords
    .map(([r, c]) => grid[r]?.[c] ?? '')
    .join('');

  return (
    <div className="quiz-block quiz-block--centered">
      <div className="word-hunt-status">
        <span>
          Kata Terpilih:{' '}
          <strong>{currentSpelled || '(Ketuk huruf berurutan lurus pada kisi)'}</strong>
        </span>
        {!isAnswerChecked && selectedCoords.length > 0 && (
          <button type="button" className="word-hunt-reset" onClick={onClearCoords}>
            Reset
          </button>
        )}
      </div>

      <div
        className="word-hunt-board"
        style={{ '--wh-cols': cols } as CSSProperties}
      >
        {grid.map((rowList, r) =>
          rowList.map((char, c) => {
            const isSelected = selectedCoords.some(([sr, sc]) => sr === r && sc === c);
            return (
              <button
                key={`${r}_${c}`}
                type="button"
                disabled={isAnswerChecked}
                className={`word-hunt-tile ${isSelected ? 'is-selected' : ''}`}
                onClick={() => onToggleCoord(r, c)}
                data-testid={`grid_tile_${r}_${c}`}
                aria-pressed={isSelected}
              >
                {char}
              </button>
            );
          })
        )}
      </div>
    </div>
  );
}

/* =========================================================
   11 & 12. MULTIPLE CHOICE / SENTENCE COMPLETION / IMAGE VOCAB
   ========================================================= */
export function ImageVocabularyRenderer({
  question,
  selectedOption,
  isAnswerChecked,
  isAnswerCorrect,
  onOptionSelected,
}: {
  question: Question;
  selectedOption: string | null;
  isAnswerChecked: boolean;
  isAnswerCorrect: boolean;
  onOptionSelected: (opt: string) => void;
}) {
  return (
    <div className="quiz-block">
      <ParchmentDossierCard>
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '10px', textAlign: 'center' }}>
          <span className="picture-tap-icon is-selected">
            <CompassIcon size={34} />
          </span>
          <p style={{ margin: 0, fontWeight: 600 }}>{question.subText ?? ''}</p>
        </div>
      </ParchmentDossierCard>
      <SentenceCompletionRenderer
        question={question}
        selectedOption={selectedOption}
        isAnswerChecked={isAnswerChecked}
        isAnswerCorrect={isAnswerCorrect}
        onOptionSelected={onOptionSelected}
      />
    </div>
  );
}

export function SentenceCompletionRenderer({
  question,
  selectedOption,
  isAnswerChecked,
  isAnswerCorrect,
  onOptionSelected,
}: {
  question: Question;
  selectedOption: string | null;
  isAnswerChecked: boolean;
  isAnswerCorrect: boolean;
  onOptionSelected: (opt: string) => void;
}) {
  return (
    <div className="quiz-options-list">
      {question.options.map((opt, idx) => {
        const isSelected = selectedOption === opt;
        const isRight = opt === question.correctAnswer;
        let stateClass = '';
        if (isAnswerChecked) {
          if (isRight) stateClass = 'is-correct';
          else if (isSelected && !isAnswerCorrect) stateClass = 'is-wrong';
        } else if (isSelected) {
          stateClass = 'is-selected';
        }
        return (
          <button
            key={opt}
            type="button"
            disabled={isAnswerChecked}
            className={`quiz-option-btn ${stateClass}`}
            onClick={() => onOptionSelected(opt)}
            data-testid={`quiz_option_${idx}`}
          >
            <span className={`option-letter ${isSelected ? 'is-selected' : ''}`}>
              {String.fromCharCode(65 + idx)}
            </span>
            <span className="quiz-option-label">{opt}</span>
            {!isAnswerChecked && isSelected && (
              <span className="state-tag state-tag--selected">
                <CheckmarkIcon size={12} /> Dipilih
              </span>
            )}
            {isAnswerChecked && isRight && (
              <CheckmarkIcon size={18} style={{ color: 'var(--color-on-success)', flexShrink: 0 }} />
            )}
            {isAnswerChecked && isSelected && !isAnswerCorrect && (
              <CrossIcon size={18} style={{ color: 'var(--color-error-text)', flexShrink: 0 }} />
            )}
          </button>
        );
      })}
    </div>
  );
}
