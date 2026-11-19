import { useEffect, useRef, useState } from 'react';
import {
  BookIcon,
  CheckmarkIcon,
  CompassIcon,
  FlameIcon,
  HelpIcon,
  LanternIcon,
  LockIcon,
  MapIcon,
  StarIcon,
  TentIcon,
} from '../components/ExpeditionIcons';
import {
  HandDrawnWindingTrail,
  ParchmentDossierCard,
  TactileSignpostButton,
  WaxSealBadge,
} from '../components/SignatureWidgets';
import {
  canAccessPath,
  DAILY_LEVEL_LIMIT,
  LEARNING_PATH_META,
  type DailyVocabAssignment,
  type LearningPath,
  type LevelInfo,
  type UserProfile,
} from '../data/models';

interface DashboardScreenProps {
  profile: UserProfile | null;
  levels: LevelInfo[];
  selectedPath: LearningPath;
  dailyCompletedCount: number;
  timeUntilMidnightReset: string;
  dailyVocabAssignments: DailyVocabAssignment[];
  onContinueToMap: (level: LevelInfo | null) => void;
  onReplayGuide: () => void;
  onChangePath: (path: LearningPath) => void;
}

const CHAPTER_LABELS: Record<number, string> = {
  1: 'Chapter 1 — Whispering Woods',
  2: 'Chapter 2 — Lost Island',
  3: 'Chapter 3 — Ancient Rome',
  4: 'Chapter 4 — Mountain Peaks',
  5: 'Chapter 5 — Crystal Cave',
};

export function DashboardScreen({
  profile,
  levels,
  selectedPath,
  dailyCompletedCount,
  timeUntilMidnightReset,
  dailyVocabAssignments,
  onContinueToMap,
  onReplayGuide,
  onChangePath,
}: DashboardScreenProps) {
  const pathMeta = LEARNING_PATH_META[selectedPath];
  const totalAssignedVocab =
    dailyVocabAssignments.length > 0 ? dailyVocabAssignments.length : pathMeta.dailyWordPoolSize;
  const completedVocabCount = dailyVocabAssignments.filter(
    (v) => v.status === 'MASTERED' || v.status === 'LEARNING'
  ).length;
  const masteredVocabCount = dailyVocabAssignments.filter((v) => v.status === 'MASTERED').length;
  const remainingDailyLevels = Math.max(0, DAILY_LEVEL_LIMIT - dailyCompletedCount);

  const completedLevels = levels.filter((lvl) => lvl.status === 'COMPLETED').length;
  const totalStars = levels.reduce((acc, lvl) => acc + lvl.stars, 0);
  const nextAvailableLevel =
    levels.find((lvl) => lvl.status === 'AVAILABLE') ?? levels[0] ?? null;
  const currentChapterName = CHAPTER_LABELS[nextAvailableLevel?.stageNumber ?? 1] ?? CHAPTER_LABELS[1];

  const currentStreak = profile?.currentStreak ?? 0;
  const dailyLevelsGoalAchieved = dailyCompletedCount >= DAILY_LEVEL_LIMIT;
  const dailyVocabGoalAchieved =
    totalAssignedVocab > 0 && completedVocabCount >= totalAssignedVocab;
  const isDailyGoalAchieved = dailyLevelsGoalAchieved || dailyVocabGoalAchieved;

  const [isStreakBurstActive, setIsStreakBurstActive] = useState(isDailyGoalAchieved);
  const prevGoalRef = useRef(isDailyGoalAchieved);
  const prevStreakRef = useRef(currentStreak);

  useEffect(() => {
    const newlyAchievedGoal = isDailyGoalAchieved && !prevGoalRef.current;
    const streakIncreased = currentStreak > prevStreakRef.current;

    if (newlyAchievedGoal || streakIncreased) {
      setIsStreakBurstActive(true);
    } else if (!isDailyGoalAchieved) {
      setIsStreakBurstActive(false);
    }

    prevGoalRef.current = isDailyGoalAchieved;
    prevStreakRef.current = currentStreak;
  }, [isDailyGoalAchieved, currentStreak]);

  useEffect(() => {
    if (!isStreakBurstActive) return;
    const timer = window.setTimeout(() => {
      setIsStreakBurstActive(false);
    }, 2400);
    return () => window.clearTimeout(timer);
  }, [isStreakBurstActive]);

  return (
    <div className="app-content-frame">
      <div className="camp-grid">
        {/* 1. Adventurer Dossier Sheet */}
        <ParchmentDossierCard className="camp-card--dossier">
          <div className="dossier-profile-row">
            <div className="dossier-avatar-ring">
              <img
                src="/assets/char_adventurer_explorer.jpg"
                alt="Avatar Penjelajah"
                className="dossier-avatar-img"
                loading="lazy"
                decoding="async"
              />
            </div>
            <div className="dossier-profile-meta">
              <div className="dossier-name-row">
                <h1 className="dossier-name">{profile?.displayName ?? 'Explorer'}</h1>
                <WaxSealBadge
                  size={28}
                  icon={<StarIcon size={14} />}
                />
              </div>
              <div className="dossier-tier-row">
                <CompassIcon size={14} style={{ color: 'var(--color-success-strong)' }} />
                <span>
                  {pathMeta.title} • {pathMeta.cefrLabel} • {profile?.xp ?? 0} XP
                </span>
              </div>
              <p className="dossier-subtext">
                Target Harian: {totalAssignedVocab} kata • Maks {DAILY_LEVEL_LIMIT} pos/hari
              </p>
            </div>
          </div>

          <button
            type="button"
            className="btn btn--secondary"
            style={{ width: '100%', marginTop: '14px' }}
            onClick={onReplayGuide}
            data-testid="camp_replay_guide_button"
          >
            <HelpIcon size={18} />
            <span>Putar Ulang Panduan Peta</span>
          </button>
        </ParchmentDossierCard>

        {/* 2. Signature Dark Expedition Log */}
        <section
          className="expedition-log-card"
          data-testid="daily_progress_dashboard_card"
          aria-label="Log Ekspedisi Hari Ini"
        >
          <div className="expedition-log-head">
            <div className="expedition-log-title">
              <TentIcon size={20} style={{ color: 'var(--accent-camp)' }} />
              <h2>TODAY&apos;S EXPEDITION LOG</h2>
            </div>
            <span className="pocketwatch-badge" title="Reset batas harian">
              <LanternIcon size={14} />
              <span>{timeUntilMidnightReset}</span>
            </span>
          </div>

          <div className="expedition-log-inserts">
            <div className="log-insert-card">
              <div className="log-insert-kicker">
                <BookIcon size={14} />
                <span>VOCABULARY</span>
              </div>
              <strong className="log-insert-value">{totalAssignedVocab} Words</strong>
              <span className="log-insert-stat">
                <CheckmarkIcon size={12} style={{ color: 'var(--color-success-strong)' }} /> {completedVocabCount} Active
              </span>
              <span className="log-insert-muted">Mastered: {masteredVocabCount}</span>
            </div>

            <div className={`log-insert-card ${dailyCompletedCount >= DAILY_LEVEL_LIMIT ? 'is-complete' : ''}`}>
              <div className="log-insert-kicker">
                <MapIcon size={14} />
                <span>DAILY WAYPOINTS</span>
              </div>
              <strong className="log-insert-value">
                {dailyCompletedCount} / {DAILY_LEVEL_LIMIT} Done
              </strong>
              <HandDrawnWindingTrail
                completedSteps={dailyCompletedCount}
                totalSteps={DAILY_LEVEL_LIMIT}
                className="log-insert-trail"
              />
              <span
                className="log-insert-stat"
                style={{ color: remainingDailyLevels === 0 ? 'var(--color-success-strong)' : 'var(--color-text-on-paper)' }}
              >
                {remainingDailyLevels === 0
                  ? 'Batas Harian Tercapai ✓'
                  : `${remainingDailyLevels} Waypoints Tersisa`}
              </span>
            </div>
          </div>

          <div className="expedition-log-footer">
            <div>
              <span className="log-footer-kicker">WILAYAH AKTIF</span>
              <strong className="log-footer-value">{currentChapterName}</strong>
            </div>
            <div style={{ textAlign: 'right' }}>
              <span className="log-footer-kicker">POS AKTIF</span>
              <strong className="log-footer-stage">Level {nextAvailableLevel?.id ?? '1-1'}</strong>
            </div>
          </div>
        </section>

        {/* 3. Primary Tactile Signpost CTA (Full-width across grid on desktop/tablet) */}
        <div className="camp-cta-row">
          <TactileSignpostButton
            onClick={() => onContinueToMap(nextAvailableLevel)}
            label={
              dailyCompletedCount >= DAILY_LEVEL_LIMIT
                ? 'Lihat Peta Petualangan'
                : 'Lanjutkan Petualangan'
            }
            subLabel={
              dailyCompletedCount >= DAILY_LEVEL_LIMIT
                ? `3 pos hari ini selesai • Reset dalam ${timeUntilMidnightReset}`
                : `Menuju ${nextAvailableLevel?.title ?? 'Stage 1-1'}`
            }
            testTag="continue_adventure_button"
            icon={<CompassIcon size={24} />}
          />
        </div>

        {/* 4. Streak & Stars Wax Medal Cards */}
        <ParchmentDossierCard
          className={`stat-seal-card streak-display-card ${
            isDailyGoalAchieved ? 'is-goal-achieved' : ''
          } ${isStreakBurstActive ? 'is-celebrating' : ''}`}
        >
          <span className="streak-shimmer-layer" aria-hidden="true" />
          <div className="stat-seal-header">
            <WaxSealBadge
              size={34}
              className={`streak-wax-seal ${isDailyGoalAchieved ? 'is-lit' : ''}`}
              icon={
                <span className="streak-flame-wrap">
                  <FlameIcon size={16} />
                </span>
              }
            />
            <span className="stat-seal-label">STREAK BELAJAR</span>
          </div>
          <p className="stat-seal-number streak-number-value" data-testid="dashboard_streak_value">
            {currentStreak} Hari
          </p>
          <p className="stat-seal-caption">Rekor Terbaik: {profile?.bestStreak ?? 0} hari</p>
          {isDailyGoalAchieved && (
            <span className="streak-goal-badge" role="status" data-testid="streak_goal_achieved_badge">
              <CheckmarkIcon size={12} />
              <span>
                {dailyLevelsGoalAchieved && dailyVocabGoalAchieved
                  ? 'Target Ekspedisi & Kosakata Tercapai!'
                  : dailyLevelsGoalAchieved
                    ? 'Target 3 Pos Harian Tercapai!'
                    : 'Target Kosakata Harian Tercapai!'}
              </span>
            </span>
          )}
        </ParchmentDossierCard>

        <ParchmentDossierCard>
          <div className="stat-seal-header">
            <WaxSealBadge size={34} icon={<StarIcon size={16} />} />
            <span className="stat-seal-label">TOTAL BINTANG</span>
          </div>
          <p className="stat-seal-number">{totalStars} / 150</p>
          <p className="stat-seal-caption">{completedLevels} / 50 level diselesaikan</p>
        </ParchmentDossierCard>

        {/* 5. Expedition World Route */}
        <ParchmentDossierCard>
          <div className="world-route-head">
            <div>
              <h2 className="dossier-card-heading">RUTE DUNIA EKSPEDISI</h2>
              <p className="dossier-subtext">
                5 Wilayah × 10 Level • Batas {DAILY_LEVEL_LIMIT} level baru/hari
              </p>
            </div>
            <WaxSealBadge size={36} label={`${completedLevels}/50`} />
          </div>
          <HandDrawnWindingTrail
            completedSteps={Math.floor((completedLevels / 50) * 5)}
            totalSteps={5}
            className="world-route-trail"
          />
          <div className="world-route-labels">
            <span>Forest</span>
            <span>Island</span>
            <span>Rome</span>
            <span>Mountain</span>
            <span>Cave</span>
          </div>
        </ParchmentDossierCard>

        {/* 6. Active Expedition Cadence / Learning Path Selector */}
        <ParchmentDossierCard>
          <h2 className="dossier-card-heading">JALUR PEMBELAJARAN AKTIF</h2>
          <p className="dossier-subtext" style={{ marginBottom: '10px' }}>
            Pilih jalur belajar sesuai tingkat kemampuanmu:
          </p>
          <div className="path-selector-list">
            {(['LOW', 'MIDDLE', 'HIGH'] as LearningPath[]).map((pathCode) => {
              const meta = LEARNING_PATH_META[pathCode];
              const isSelected = selectedPath === pathCode;
              const unlocked = profile ? canAccessPath(profile.initialPath, pathCode) : pathCode === 'LOW';
              return (
                <button
                  key={pathCode}
                  type="button"
                  disabled={!unlocked}
                  className={`path-selector-btn ${isSelected ? 'is-selected' : ''} ${
                    !unlocked ? 'is-locked' : ''
                  }`}
                  onClick={() => unlocked && onChangePath(pathCode)}
                >
                  <span className="path-selector-seal">
                    {isSelected ? (
                      <CheckmarkIcon size={14} />
                    ) : !unlocked ? (
                      <LockIcon size={14} />
                    ) : null}
                  </span>
                  <span className="path-selector-copy">
                    <strong>
                      {meta.title} ({meta.cefrLabel})
                    </strong>
                    <small>
                      {meta.dailyWordPoolSize} kata harian • {meta.uiLanguage}
                    </small>
                  </span>
                </button>
              );
            })}
          </div>
        </ParchmentDossierCard>
      </div>
    </div>
  );
}
