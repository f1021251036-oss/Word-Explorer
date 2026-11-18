import { CheckmarkIcon, FlameIcon, LockIcon } from '../components/ExpeditionIcons';
import type { CompanionAnimal } from '../data/models';

interface CompanionsScreenProps {
  companions: Array<CompanionAnimal & { isUnlocked: boolean }>;
  activeCompanionId: string;
  currentStreak: number;
  onEquipCompanion: (companionId: string) => void;
}

export function CompanionsScreen({
  companions,
  activeCompanionId,
  currentStreak,
  onEquipCompanion,
}: CompanionsScreenProps) {
  return (
    <div className="app-content-frame">
      <header className="section-header-row">
        <div>
          <h1 className="section-title">Sahabat Hewan</h1>
          <p className="section-subtitle">
            Aktifkan hewan untuk menemanimu di peta petualangan.
          </p>
        </div>
        <span className="streak-badge" style={{ minHeight: '40px', padding: '6px 12px', fontSize: '0.85rem' }}>
          <FlameIcon size={16} style={{ color: 'var(--accent-beasts)' }} />
          <span>{currentStreak} Hari Streak</span>
        </span>
      </header>

      <div className="beasts-grid">
        {companions.map((companion) => {
          const isEquipped = companion.id === activeCompanionId;
          const isUnlocked = companion.isUnlocked;

          return (
            <article
              key={companion.id}
              className={`beast-card ${isEquipped ? 'is-equipped' : ''} ${
                !isUnlocked ? 'is-locked' : ''
              }`}
              data-testid={`companion_${companion.id}`}
            >
              <div className="beast-avatar-wrap">
                <div className={`beast-avatar ${isEquipped ? 'is-equipped' : ''}`}>
                  <span role="img" aria-label={companion.name}>
                    {companion.emoji}
                  </span>
                </div>
                {!isUnlocked && (
                  <div className="beast-lock-overlay" aria-label="Terkunci">
                    <LockIcon size={20} />
                  </div>
                )}
                {companion.isMythical && (
                  <span className="beast-mythic-pill">Mitologis</span>
                )}
              </div>

              <h2 className="beast-name">{companion.name}</h2>
              <p className="beast-trait">
                {companion.species} • {companion.trait}
              </p>
              <p className="beast-desc">{companion.description}</p>

              <div className="beast-action">
                {isUnlocked ? (
                  <button
                    type="button"
                    className={`btn ${isEquipped ? 'btn--secondary beast-btn-active' : 'btn--forest'}`}
                    style={{ width: '100%' }}
                    onClick={() => onEquipCompanion(companion.id)}
                  >
                    {isEquipped ? (
                      <>
                        <CheckmarkIcon size={16} />
                        <span>Aktif</span>
                      </>
                    ) : (
                      <span>Pilih</span>
                    )}
                  </button>
                ) : (
                  <div className="beast-locked-pill">
                    <LockIcon size={14} />
                    <span>Streak {companion.requiredStreak} hari</span>
                  </div>
                )}
              </div>
            </article>
          );
        })}
      </div>
    </div>
  );
}
