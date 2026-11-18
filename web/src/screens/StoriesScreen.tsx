import { useState } from 'react';
import { AccessibleDialog } from '../components/AccessibleDialog';
import { CheckmarkIcon, VolumeUpIcon } from '../components/ExpeditionIcons';
import { ADVENTURE_STORIES, type AdventureStory } from '../data/vocabCatalog';

interface StoriesScreenProps {
  completedStoryIds: Set<string>;
  activeStory: AdventureStory | null;
  onOpenStory: (story: AdventureStory) => void;
  onCloseStory: () => void;
  onCompleteStory: (storyId: string) => void;
  onSpeak: (text: string) => void;
}

export function StoriesScreen({
  completedStoryIds,
  activeStory,
  onOpenStory,
  onCloseStory,
  onCompleteStory,
  onSpeak,
}: StoriesScreenProps) {
  return (
    <div className="app-content-frame">
      <header className="section-header-row">
        <div>
          <h1 className="section-title">Adventure Lore</h1>
          <p className="section-subtitle">
            Kisah tiap wilayah peta &amp; uji pemahaman bacaan singkat (+25 XP saat selesai).
          </p>
        </div>
        <span className="pill-badge pill-badge--available">
          {completedStoryIds.size} / {ADVENTURE_STORIES.length} Selesai
        </span>
      </header>

      <div className="lore-grid">
        {ADVENTURE_STORIES.map((story) => {
          const isDone = completedStoryIds.has(story.id);
          return (
            <button
              key={story.id}
              type="button"
              className="lore-card"
              data-testid={`story_card_${story.id}`}
              onClick={() => onOpenStory(story)}
            >
              <div className="lore-card-top">
                <span className="lore-region-tag">
                  {story.region} • {story.cefr}
                </span>
                {isDone && (
                  <span className="lore-done-badge">
                    <CheckmarkIcon size={14} /> Selesai
                  </span>
                )}
              </div>
              <h2 className="lore-card-title">{story.title}</h2>
              <p className="lore-card-synopsis">{story.synopsis}</p>
              <div className="lore-pill-row">
                {story.targetWords.slice(0, 4).map((w) => (
                  <span key={w} className="lore-word-pill">
                    {w}
                  </span>
                ))}
              </div>
            </button>
          );
        })}
      </div>

      {activeStory && (
        <StoryReaderModal
          story={activeStory}
          isAlreadyCompleted={completedStoryIds.has(activeStory.id)}
          onClose={onCloseStory}
          onComplete={() => onCompleteStory(activeStory.id)}
          onSpeak={onSpeak}
        />
      )}
    </div>
  );
}

function StoryReaderModal({
  story,
  isAlreadyCompleted,
  onClose,
  onComplete,
  onSpeak,
}: {
  story: AdventureStory;
  isAlreadyCompleted: boolean;
  onClose: () => void;
  onComplete: () => void;
  onSpeak: (text: string) => void;
}) {
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const [quizAnswered, setQuizAnswered] = useState(false);

  const firstQ = story.comprehensionQuestions[0] ?? null;
  const quizCorrect = Boolean(firstQ && selectedOption === firstQ.correctAnswer);

  return (
    <AccessibleDialog title={story.title} onClose={onClose} maxWidthPx={680}>
      <div className="story-reader-meta">
        <span className="lore-region-tag">
          {story.region} • CEFR {story.cefr}
        </span>
        <button
          type="button"
          className="btn btn--secondary"
          style={{ minHeight: '44px', padding: '6px 12px', fontSize: '0.82rem' }}
          onClick={() => onSpeak(story.paragraphs.join(' '))}
        >
          <VolumeUpIcon size={16} />
          <span>Bacakan Cerita</span>
        </button>
      </div>

      <div className="story-paragraphs">
        {story.paragraphs.map((p, idx) => (
          <p key={idx} className="story-paragraph">
            {p}
          </p>
        ))}
      </div>

      {firstQ && (
        <div className="story-quiz-card">
          <h3 className="story-quiz-heading">Uji Pemahaman Bacaan:</h3>
          <p className="story-quiz-prompt">{firstQ.prompt}</p>
          <div className="story-quiz-options">
            {firstQ.options.map((opt) => {
              const isSelected = selectedOption === opt;
              const isRight = opt === firstQ.correctAnswer;
              let stateClass = '';
              if (quizAnswered) {
                if (isRight) stateClass = 'is-correct';
                else if (isSelected && !quizCorrect) stateClass = 'is-wrong';
              } else if (isSelected) {
                stateClass = 'is-selected';
              }
              return (
                <button
                  key={opt}
                  type="button"
                  disabled={quizAnswered}
                  className={`story-option-btn ${stateClass}`}
                  onClick={() => {
                    if (!quizAnswered) {
                      setSelectedOption(opt);
                      setQuizAnswered(true);
                    }
                  }}
                >
                  {opt}
                </button>
              );
            })}
          </div>
          {quizAnswered && (
            <p className={`story-feedback ${quizCorrect ? 'is-correct' : 'is-wrong'}`}>
              {firstQ.explanation}
            </p>
          )}
        </div>
      )}

      <div className="story-modal-footer">
        <button type="button" className="btn btn--secondary" onClick={onClose}>
          Tutup
        </button>
        <button
          type="button"
          className="btn btn--primary"
          disabled={Boolean(firstQ && !quizAnswered && !isAlreadyCompleted)}
          onClick={onComplete}
        >
          <CheckmarkIcon size={18} />
          <span>{isAlreadyCompleted ? 'Selesai Dibaca' : 'Tandai Selesai (+25 XP)'}</span>
        </button>
      </div>
    </AccessibleDialog>
  );
}
