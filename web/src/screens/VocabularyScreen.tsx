import { useMemo } from 'react';
import {
  CheckmarkIcon,
  SearchIcon,
  VolumeUpIcon,
} from '../components/ExpeditionIcons';
import { getDailyWordsOfTheDay } from '../data/questionGenerator';
import type { VocabularyItem } from '../data/vocabCatalog';
import { getTodayIsoDate } from '../utils/sound';

interface VocabularyScreenProps {
  allVocabulary: VocabularyItem[];
  filteredVocabulary: VocabularyItem[];
  searchQuery: string;
  activeCategory: string;
  timeUntilMidnightReset: string;
  onSearchChange: (q: string) => void;
  onCategoryChange: (category: string) => void;
  onToggleLearned: (vocabId: string) => void;
  onSpeak: (word: string) => void;
}

const CATEGORIES = [
  'All',
  'Exploration',
  'Nature',
  'Travel',
  'History',
  'Geography',
  'Traits',
  'Science',
];

export function VocabularyScreen({
  allVocabulary,
  filteredVocabulary,
  searchQuery,
  activeCategory,
  timeUntilMidnightReset,
  onSearchChange,
  onCategoryChange,
  onToggleLearned,
  onSpeak,
}: VocabularyScreenProps) {
  const todayIso = getTodayIsoDate();
  const dailyWords = useMemo(
    () => getDailyWordsOfTheDay(allVocabulary, todayIso),
    [allVocabulary, todayIso]
  );

  return (
    <div className="app-content-frame">
      <div className="lexicon-page">
        {/* Header Row */}
        <header className="section-header-row">
          <div>
            <h1 className="section-title">Explorer Lexicon</h1>
            <p className="section-subtitle">
              Kamus saku petualangan — pelajari pelafalan, arti, dan contoh kalimat.
            </p>
          </div>
          <span className="pill-badge pill-badge--available">{filteredVocabulary.length} Kata</span>
        </header>

        {/* Words of the Day Strip */}
        {dailyWords.length > 0 && (
          <section className="daily-words-card" data-testid="words_of_the_day_section">
            <div className="daily-words-head">
              <div className="daily-words-title">
                <span aria-hidden="true">☀️</span>
                <h2>KATA HARI INI</h2>
              </div>
              <span className="daily-words-timer">
                Kata baru: {timeUntilMidnightReset}
              </span>
            </div>
            <div className="daily-words-grid">
              {dailyWords.map((item) => (
                <article key={`daily_${item.id}`} className="daily-word-tile">
                  <div className="daily-word-tile-top">
                    <strong>{item.word}</strong>
                    <button
                      type="button"
                      className="speak-btn"
                      aria-label={`Dengarkan pelafalan ${item.word}`}
                      onClick={() => onSpeak(item.word)}
                    >
                      <VolumeUpIcon size={18} />
                    </button>
                  </div>
                  <p className="daily-word-meaning">Arti: {item.indonesian}</p>
                  {item.exampleSentence && (
                    <p className="daily-word-example">&ldquo;{item.exampleSentence}&rdquo;</p>
                  )}
                </article>
              ))}
            </div>
          </section>
        )}

        {/* Search Input & Category Filters */}
        <div className="lexicon-controls">
          <div className="lexicon-search-wrap">
            <SearchIcon size={18} />
            <input
              type="search"
              value={searchQuery}
              onChange={(e) => onSearchChange(e.target.value)}
              placeholder="Cari kata Inggris atau artinya..."
              aria-label="Cari kosakata"
              className="lexicon-search-input"
              data-testid="vocab_search_field"
            />
          </div>

          <div className="chip-scroll-row" role="tablist" aria-label="Filter kategori kosakata">
            {CATEGORIES.map((category) => {
              const isSelected = activeCategory === category;
              return (
                <button
                  key={category}
                  type="button"
                  role="tab"
                  aria-selected={isSelected}
                  className={`filter-chip ${isSelected ? 'is-selected' : ''}`}
                  onClick={() => onCategoryChange(category)}
                >
                  {category}
                </button>
              );
            })}
          </div>
        </div>

        {/* Responsive Vocab Card Grid */}
        {filteredVocabulary.length === 0 ? (
          <div className="empty-state-card">
            <p style={{ margin: 0, fontWeight: 600 }}>
              Tidak ditemukan kosakata untuk pencarian &ldquo;{searchQuery}&rdquo;.
            </p>
          </div>
        ) : (
          <div className="lexicon-grid">
            {filteredVocabulary.map((vocab) => (
              <article key={vocab.id} className="vocab-card">
                <div className="vocab-card-top">
                  <div className="vocab-word-block">
                    <div className="vocab-word-line">
                      <h3 className="vocab-word">{vocab.word}</h3>
                      <span className="vocab-pron">{vocab.pronunciation}</span>
                    </div>
                    <span className="vocab-category">
                      {vocab.partOfSpeech} • {vocab.category}
                    </span>
                  </div>

                  <div className="vocab-card-actions">
                    <span className="pill-badge pill-badge--cefr">{vocab.cefr}</span>
                    <button
                      type="button"
                      className="speak-btn"
                      aria-label={`Dengarkan ${vocab.word}`}
                      onClick={() => onSpeak(vocab.word)}
                    >
                      <VolumeUpIcon size={18} />
                    </button>
                    <button
                      type="button"
                      className={`learned-toggle-btn ${vocab.isLearned ? 'is-learned' : ''}`}
                      aria-label={vocab.isLearned ? `Tandai ${vocab.word} belum dikuasai` : `Tandai ${vocab.word} sudah dikuasai`}
                      aria-pressed={vocab.isLearned}
                      onClick={() => onToggleLearned(vocab.id)}
                    >
                      {vocab.isLearned ? <CheckmarkIcon size={16} /> : <span className="learned-circle" />}
                    </button>
                  </div>
                </div>

                <p className="vocab-meaning">
                  <strong>{vocab.indonesian}</strong> — {vocab.englishDef}
                </p>
                <div className="vocab-example-box">
                  <p className="vocab-example-en">{vocab.exampleSentence}</p>
                  <p className="vocab-example-id">{vocab.exampleTranslation}</p>
                </div>
              </article>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
