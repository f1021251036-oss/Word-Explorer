import { DAILY_LEVEL_LIMIT, type ExplorationRegion, type LevelInfo } from '../data/models';
import { AccessibleDialog } from '../components/AccessibleDialog';
import { CloseIcon, LockIcon, PlayIcon, StarIcon } from '../components/ExpeditionIcons';
import { IsometricWorldMap } from '../components/IsometricWorldMap';

interface MapScreenProps {
  levels: LevelInfo[];
  activeCompanionId: string;
  dailyCompletedCount: number;
  selectedLevel: LevelInfo | null;
  selectedRegion: ExplorationRegion | null;
  showFirstTimeGuide: boolean;
  guideStep: number;
  isNextStepChipDismissed: boolean;
  isRegionUnlocked: (stageNumber: number) => boolean;
  onSelectLevel: (level: LevelInfo | null) => void;
  onSelectRegion: (region: ExplorationRegion | null) => void;
  onStartQuiz: (level: LevelInfo) => void;
  onAdvanceGuide: () => void;
  onCompleteGuide: (openFirstLevel: boolean) => void;
  onReplayGuide: () => void;
  onDismissNextStepChip: () => void;
}

export function MapScreen({
  levels,
  activeCompanionId,
  dailyCompletedCount,
  selectedLevel,
  selectedRegion,
  showFirstTimeGuide,
  guideStep,
  isNextStepChipDismissed,
  isRegionUnlocked,
  onSelectLevel,
  onSelectRegion,
  onStartQuiz,
  onAdvanceGuide,
  onCompleteGuide,
  onReplayGuide,
  onDismissNextStepChip,
}: MapScreenProps) {
  const completedCount = levels.filter((lvl) => lvl.status === 'COMPLETED').length;
  const totalStars = levels.reduce((acc, lvl) => acc + lvl.stars, 0);
  const availableLevels = levels.filter((lvl) => lvl.status === 'AVAILABLE');
  const firstPlayableLevel = availableLevels[availableLevels.length - 1] ?? levels[0] ?? null;
  const isDailyLimitReached = dailyCompletedCount >= DAILY_LEVEL_LIMIT;

  const isSelectedCompleted = selectedLevel?.status === 'COMPLETED';
  const isLockedByDailyLimit = Boolean(
    selectedLevel && !isSelectedCompleted && isDailyLimitReached
  );

  return (
    <section className="map-page" aria-label="Peta Petualangan Word Explorer">
      <IsometricWorldMap
        levels={levels}
        activeCompanionId={activeCompanionId}
        isRegionUnlocked={isRegionUnlocked}
        onLevelClick={(lvl) => onSelectLevel(lvl)}
        onRegionClick={(reg) => onSelectRegion(reg)}
        onReplayGuide={onReplayGuide}
        totalStars={totalStars}
        completedCount={completedCount}
      />

      {/* Daily Limit Persistent Banner */}
      {isDailyLimitReached && (
        <div className="map-floating-banner map-floating-banner--limit" role="status">
          <LockIcon size={18} style={{ flexShrink: 0 }} />
          <div style={{ fontSize: '0.8rem' }}>
            <strong>Batas Hari Ini: 3/3 Level Selesai!</strong>
            <div style={{ color: 'var(--color-text-muted-on-paper)' }}>
              Kembalilah besok untuk level baru, atau latihan ulang level yang sudah selesai.
            </div>
          </div>
        </div>
      )}

      {/* Next-Step Action Chip for New Explorers */}
      {!showFirstTimeGuide &&
        !isNextStepChipDismissed &&
        completedCount === 0 &&
        !selectedLevel &&
        !selectedRegion &&
        !isDailyLimitReached &&
        firstPlayableLevel && (
          <div className="map-floating-banner map-floating-banner--hint" data-testid="next_step_guide_chip">
            <button
              type="button"
              className="banner-action-btn"
              onClick={() => onSelectLevel(firstPlayableLevel)}
            >
              <span
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  width: 28,
                  height: 28,
                  borderRadius: '999px',
                  background: 'var(--color-primary)',
                  border: '1px solid var(--color-outline)',
                  flexShrink: 0,
                }}
              >
                <PlayIcon size={14} />
              </span>
              <span>
                Mulai di sini: Ketuk untuk buka Level {firstPlayableLevel.id}
              </span>
            </button>
            <button
              type="button"
              aria-label="Tutup petunjuk awal"
              data-testid="dismiss_next_step_chip"
              onClick={onDismissNextStepChip}
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                width: 44,
                height: 44,
                border: 0,
                borderRadius: '10px',
                background: 'transparent',
                color: 'var(--color-text-on-paper)',
                cursor: 'pointer',
              }}
            >
              <CloseIcon size={18} />
            </button>
          </div>
        )}

      {/* 3-Step First-Time Tour Modal */}
      <AccessibleDialog
        open={showFirstTimeGuide}
        onClose={() => onCompleteGuide(false)}
        labelledBy="first-time-guide-title"
        maxWidthPx={440}
      >
        <div data-testid="first_time_guide_card" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span className="pill-badge pill-badge--active">
              PANDUAN {guideStep + 1} / 3
            </span>
            <button
              type="button"
              data-testid="guide_skip_button"
              onClick={() => onCompleteGuide(false)}
              style={{
                minHeight: '44px',
                padding: '6px 12px',
                border: 0,
                background: 'transparent',
                color: 'var(--color-text-muted-on-paper)',
                fontWeight: 700,
                cursor: 'pointer',
              }}
            >
              Lewati
            </button>
          </div>

          <h2
            id="first-time-guide-title"
            style={{
              margin: 0,
              fontFamily: 'var(--font-display)',
              fontSize: '1.15rem',
              color: 'var(--color-text-on-paper)',
            }}
          >
            {guideStep === 0 && 'Langkah 1: Mulai Misi Pertamamu'}
            {guideStep === 1 && 'Langkah 2: Jelajahi Peta & Legenda'}
            {guideStep === 2 && 'Langkah 3: Camp, Lexicon, Lore & Beasts'}
          </h2>

          <p style={{ margin: 0, color: 'var(--color-text-muted-on-paper)', fontSize: '0.94rem' }}>
            {guideStep === 0 &&
              `Klik angka level emas di peta untuk membuka Level ${firstPlayableLevel?.id ?? '1-1'}. Kamu bisa menyelesaikan hingga 3 level baru setiap hari.`}
            {guideStep === 1 &&
              'Pilih wilayah di bilah atas, geser peta dengan mouse/sentuhan atau tombol panah keyboard, perbesar lewat scroll wheel atau tombol +/-, dan cek Legenda di kiri bawah.'}
            {guideStep === 2 &&
              'Gunakan menu navigasi untuk memantau target harian di Camp, melatih pelafalan di Lexicon, membaca Lore, dan membuka Beasts lewat streak harian.'}
          </p>

          <div style={{ display: 'flex', justifyContent: 'center', gap: '6px' }}>
            {[0, 1, 2].map((idx) => (
              <span
                key={idx}
                style={{
                  width: idx === guideStep ? '24px' : '8px',
                  height: '8px',
                  borderRadius: '4px',
                  background: idx === guideStep ? 'var(--color-primary-pressed)' : 'var(--color-locked-soft)',
                }}
              />
            ))}
          </div>

          {guideStep < 2 ? (
            <button
              type="button"
              className="btn btn--primary"
              data-testid="guide_next_button"
              onClick={onAdvanceGuide}
            >
              Lanjut ({guideStep + 1}/3)
            </button>
          ) : (
            <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
              <button
                type="button"
                className="btn btn--secondary"
                data-testid="guide_close_button"
                onClick={() => onCompleteGuide(false)}
              >
                Nanti
              </button>
              <button
                type="button"
                className="btn btn--primary"
                style={{ flex: 1 }}
                data-testid="guide_start_level_button"
                onClick={() => onCompleteGuide(true)}
              >
                Mulai Level {firstPlayableLevel?.id ?? '1-1'}!
              </button>
            </div>
          )}
        </div>
      </AccessibleDialog>

      {/* Level Detail Dialog / Bottom Sheet */}
      <AccessibleDialog
        open={Boolean(selectedLevel)}
        onClose={() => onSelectLevel(null)}
        labelledBy="level-modal-title"
      >
        {selectedLevel && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <span
                style={{
                  padding: '5px 12px',
                  borderRadius: '10px',
                  background: 'var(--color-surface)',
                  color: 'var(--color-text-strong-on-dark)',
                  fontWeight: 700,
                  fontSize: '0.88rem',
                }}
              >
                Level {selectedLevel.id}
              </span>
              <button
                type="button"
                aria-label="Tutup detail level"
                data-testid="close_level_sheet"
                onClick={() => onSelectLevel(null)}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  width: '44px',
                  height: '44px',
                  borderRadius: '10px',
                  border: '1px solid var(--color-outline)',
                  background: 'var(--color-field)',
                  color: 'var(--color-text-on-paper)',
                  cursor: 'pointer',
                }}
              >
                <CloseIcon size={18} />
              </button>
            </div>

            <h2
              id="level-modal-title"
              style={{
                margin: 0,
                fontFamily: 'var(--font-display)',
                fontSize: '1.25rem',
                color: 'var(--color-text-on-paper)',
              }}
            >
              {selectedLevel.title}
            </h2>

            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <div style={{ display: 'inline-flex', gap: '4px' }}>
                {[1, 2, 3].map((s) => (
                  <StarIcon
                    key={s}
                    size={22}
                    filled={s <= selectedLevel.stars}
                    style={{
                      color: s <= selectedLevel.stars ? 'var(--color-reward-strong)' : 'var(--color-locked-on-paper)',
                    }}
                  />
                ))}
              </div>
              <span style={{ fontWeight: 600, fontSize: '0.9rem', color: 'var(--color-text-muted-on-paper)' }}>
                {selectedLevel.stars > 0
                  ? `${selectedLevel.stars}/3 Bintang Diperoleh`
                  : 'Belum diselesaikan'}
              </span>
            </div>

            <div>
              <div style={{ fontWeight: 700, fontSize: '0.85rem', marginBottom: '8px' }}>
                Target Kosakata:
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                {selectedLevel.targetWords.map((word) => (
                  <span
                    key={word}
                    style={{
                      padding: '5px 11px',
                      borderRadius: '999px',
                      background: 'var(--color-field)',
                      border: '1.5px solid var(--color-outline)',
                      fontSize: '0.84rem',
                      fontWeight: 600,
                    }}
                  >
                    📖 {word}
                  </span>
                ))}
              </div>
            </div>

            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(3, minmax(0, 1fr))',
                gap: '8px',
                padding: '12px',
                borderRadius: '14px',
                background: 'var(--color-field)',
                border: '1.5px solid var(--color-outline)',
                textAlign: 'center',
              }}
            >
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted-on-paper)', fontWeight: 600 }}>Soal</div>
                <div style={{ fontWeight: 700, fontSize: '0.9rem' }}>10 Soal</div>
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted-on-paper)', fontWeight: 600 }}>Remedial</div>
                <div style={{ fontWeight: 700, fontSize: '0.9rem' }}>Adaptif</div>
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted-on-paper)', fontWeight: 600 }}>Maks Bintang</div>
                <div style={{ fontWeight: 700, fontSize: '0.9rem' }}>⭐⭐⭐</div>
              </div>
            </div>

            {isLockedByDailyLimit && (
              <div
                style={{
                  padding: '12px',
                  borderRadius: '12px',
                  background: 'var(--color-error-soft)',
                  border: '1.5px solid var(--color-error)',
                  color: 'var(--color-error-text)',
                  fontSize: '0.86rem',
                }}
              >
                <strong>Batas Harian Tercapai (3/3).</strong> Kembalilah besok untuk membuka level baru!
              </div>
            )}

            <button
              type="button"
              className="btn btn--primary"
              style={{ width: '100%' }}
              disabled={selectedLevel.status === 'LOCKED' || isLockedByDailyLimit}
              data-testid="start_quiz_button"
              onClick={() => onStartQuiz(selectedLevel)}
            >
              {isLockedByDailyLimit ? <LockIcon size={18} /> : <PlayIcon size={18} />}
              <span>
                {isLockedByDailyLimit
                  ? 'Batas Harian Tercapai (3/3 Selesai)'
                  : isSelectedCompleted
                    ? 'Latihan Ulang Level (Review)'
                    : 'Mulai Kuis Petualangan'}
              </span>
            </button>
          </div>
        )}
      </AccessibleDialog>

      {/* Region Detail Dialog / Bottom Sheet */}
      <AccessibleDialog
        open={Boolean(selectedRegion)}
        onClose={() => onSelectRegion(null)}
        labelledBy="region-modal-title"
      >
        {selectedRegion && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            {(() => {
              const unlocked = isRegionUnlocked(selectedRegion.stageNumber);
              const stageLevels = levels.filter(
                (lvl) => lvl.stageNumber === selectedRegion.stageNumber
              );
              const regionCompleted = stageLevels.filter(
                (lvl) => lvl.status === 'COMPLETED'
              ).length;
              const regionStars = stageLevels.reduce((sum, lvl) => sum + lvl.stars, 0);
              const firstAvailableInRegion =
                stageLevels.find((lvl) => lvl.status === 'AVAILABLE') ?? stageLevels[0];

              return (
                <>
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <span
                      className={`pill-badge ${
                        !unlocked
                          ? 'pill-badge--locked'
                          : regionCompleted === stageLevels.length
                            ? 'pill-badge--available'
                            : 'pill-badge--active'
                      }`}
                    >
                      {!unlocked ? 'TERKUNCI' : regionCompleted === 10 ? 'SELESAI' : 'AKTIF'}
                    </span>
                    <button
                      type="button"
                      aria-label="Tutup detail wilayah"
                      data-testid="close_region_sheet"
                      onClick={() => onSelectRegion(null)}
                      style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        width: '44px',
                        height: '44px',
                        borderRadius: '10px',
                        border: '1px solid var(--color-outline)',
                        background: 'var(--color-field)',
                        color: 'var(--color-text-on-paper)',
                        cursor: 'pointer',
                      }}
                    >
                      <CloseIcon size={18} />
                    </button>
                  </div>

                  <div>
                    <h2
                      id="region-modal-title"
                      style={{ margin: 0, fontFamily: 'var(--font-display)', fontSize: '1.3rem' }}
                    >
                      {selectedRegion.iconEmoji} {selectedRegion.name}
                    </h2>
                    <div style={{ color: 'var(--color-text-muted-on-paper)', fontWeight: 600 }}>
                      {selectedRegion.subtitle}
                    </div>
                  </div>

                  <p style={{ margin: 0, fontSize: '0.94rem' }}>{selectedRegion.description}</p>

                  <div
                    style={{
                      display: 'grid',
                      gridTemplateColumns: 'repeat(3, minmax(0, 1fr))',
                      gap: '8px',
                      padding: '12px',
                      borderRadius: '14px',
                      background: 'var(--color-field)',
                      border: '1.5px solid var(--color-outline)',
                      textAlign: 'center',
                    }}
                  >
                    <div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted-on-paper)' }}>Level</div>
                      <div style={{ fontWeight: 800 }}>{regionCompleted} / 10</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted-on-paper)' }}>Bintang</div>
                      <div style={{ fontWeight: 800 }}>⭐ {regionStars} / 30</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--color-text-muted-on-paper)' }}>Status</div>
                      <div
                        style={{
                          fontWeight: 800,
                          color: unlocked ? 'var(--color-success-strong)' : 'var(--color-error-text)',
                        }}
                      >
                        {unlocked ? 'Terbuka' : 'Terkunci'}
                      </div>
                    </div>
                  </div>

                  {unlocked && firstAvailableInRegion && (
                    <button
                      type="button"
                      className="btn btn--primary"
                      data-testid="enter_region_button"
                      onClick={() => {
                        onSelectRegion(null);
                        onSelectLevel(firstAvailableInRegion);
                      }}
                    >
                      Masuk Wilayah {selectedRegion.shortName}
                    </button>
                  )}
                </>
              );
            })()}
          </div>
        )}
      </AccessibleDialog>
    </section>
  );
}
