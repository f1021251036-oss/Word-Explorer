import { useRef, useState, type ChangeEvent } from 'react';
import { ParchmentDossierCard } from '../components/SignatureWidgets';
import {
  CheckmarkIcon,
  CompassIcon,
  ExitIcon,
  HelpIcon,
  LockIcon,
  SoundIcon,
} from '../components/ExpeditionIcons';
import {
  canAccessPath,
  LEARNING_PATH_META,
  type LearningPath,
  type UserProfile,
} from '../data/models';

interface SettingsScreenProps {
  profile: UserProfile | null;
  selectedPath: LearningPath;
  isSoundEnabled: boolean;
  highContrast: boolean;
  reducedMotion: boolean;
  onToggleSound: () => void;
  onToggleHighContrast: () => void;
  onToggleReducedMotion: () => void;
  onChangePath: (path: LearningPath) => void;
  onReplayGuide: () => void;
  onExportBackup: () => string | null;
  onImportBackup: (rawJson: string) => { ok: boolean; message: string };
  onLogout: () => void;
}

export function SettingsScreen({
  profile,
  selectedPath,
  isSoundEnabled,
  highContrast,
  reducedMotion,
  onToggleSound,
  onToggleHighContrast,
  onToggleReducedMotion,
  onChangePath,
  onReplayGuide,
  onExportBackup,
  onImportBackup,
  onLogout,
}: SettingsScreenProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [backupStatus, setBackupStatus] = useState<{ ok: boolean; message: string } | null>(null);

  const handleExportClick = () => {
    const json = onExportBackup();
    if (!json) {
      setBackupStatus({ ok: false, message: 'Tidak ada data progres aktif untuk diekspor.' });
      return;
    }
    const blob = new Blob([json], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    const dateStamp = new Date().toISOString().slice(0, 10);
    link.href = url;
    link.download = `word-explorer-backup-${dateStamp}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    setBackupStatus({ ok: true, message: `Cadangan word-explorer-backup-${dateStamp}.json berhasil diunduh.` });
  };

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      const content = typeof reader.result === 'string' ? reader.result : '';
      const result = onImportBackup(content);
      setBackupStatus(result);
    };
    reader.onerror = () => {
      setBackupStatus({ ok: false, message: 'Gagal membaca file dari perangkat.' });
    };
    reader.readAsText(file);
  };

  return (
    <div className="app-content-frame">
      <div className="settings-stack">
        <header className="section-header-row">
          <div>
            <h1 className="section-title">Pengaturan Ekspedisi</h1>
            <p className="section-subtitle">
              Kelola suara efek, aksesibilitas tampilan, jalur belajar, cadangan data, dan akunmu.
            </p>
          </div>
        </header>

        {/* Audio & Accessibility */}
        <ParchmentDossierCard>
          <h2 className="dossier-card-heading">Audio &amp; Aksesibilitas</h2>

          <div className="settings-row">
            <div>
              <strong className="settings-item-title">Efek Suara &amp; Pelafalan</strong>
              <p className="settings-item-desc">
                Bunyi glockenspiel jawaban benar, ketukan papan kayu, dan fanfare kemenangan.
              </p>
            </div>
            <button
              type="button"
              className={`btn ${isSoundEnabled ? 'btn--primary' : 'btn--secondary'}`}
              onClick={onToggleSound}
              aria-pressed={isSoundEnabled}
            >
              <SoundIcon size={18} enabled={isSoundEnabled} />
              <span>{isSoundEnabled ? 'Aktif' : 'Mati'}</span>
            </button>
          </div>

          <div className="settings-row">
            <div>
              <strong className="settings-item-title">Mode Kontras Tinggi</strong>
              <p className="settings-item-desc">
                Mempertebal garis tepi dan kontras teks untuk kenyamanan membaca maksimal.
              </p>
            </div>
            <button
              type="button"
              className={`btn ${highContrast ? 'btn--primary' : 'btn--secondary'}`}
              onClick={onToggleHighContrast}
              aria-pressed={highContrast}
            >
              <span>{highContrast ? 'Aktif' : 'Standar'}</span>
            </button>
          </div>

          <div className="settings-row">
            <div>
              <strong className="settings-item-title">Kurangi Animasi Gerak</strong>
              <p className="settings-item-desc">
                Menonaktifkan animasi transisi &amp; pin bergerak agar lebih tenang di layar.
              </p>
            </div>
            <button
              type="button"
              className={`btn ${reducedMotion ? 'btn--primary' : 'btn--secondary'}`}
              onClick={onToggleReducedMotion}
              aria-pressed={reducedMotion}
            >
              <span>{reducedMotion ? 'Aktif' : 'Standar'}</span>
            </button>
          </div>
        </ParchmentDossierCard>

        {/* Learning Path & Tutorial */}
        <ParchmentDossierCard>
          <h2 className="dossier-card-heading">Jalur &amp; Panduan Peta</h2>
          <div className="path-selector-list" style={{ marginTop: '10px' }}>
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
                    <small>{meta.description}</small>
                  </span>
                </button>
              );
            })}
          </div>

          <button
            type="button"
            className="btn btn--secondary"
            style={{ width: '100%', marginTop: '14px' }}
            onClick={onReplayGuide}
          >
            <HelpIcon size={18} />
            <span>Putar Ulang Panduan Peta</span>
          </button>
        </ParchmentDossierCard>

        {/* Progress Backup & Restore (JSON) */}
        <ParchmentDossierCard>
          <h2 className="dossier-card-heading">Cadangan &amp; Pemulihan Progres</h2>
          <p className="settings-item-desc" style={{ marginTop: '6px' }}>
            Unduh progres XP, bintang level, streak, dan kosakata dalam format JSON agar bisa dipulihkan di browser atau perangkat lain.
          </p>
          <div className="backup-actions-row">
            <button
              type="button"
              className="btn btn--primary"
              data-testid="export_backup_button"
              onClick={handleExportClick}
            >
              Unduh Cadangan (.json)
            </button>
            <button
              type="button"
              className="btn btn--secondary"
              data-testid="import_backup_button"
              onClick={() => fileInputRef.current?.click()}
            >
              Pulihkan dari File JSON
            </button>
            <input
              ref={fileInputRef}
              type="file"
              accept=".json,application/json"
              aria-label="Pilih file cadangan JSON Word Explorer"
              style={{ display: 'none' }}
              onChange={handleFileChange}
            />
          </div>
          {backupStatus && (
            <p
              className={`backup-status-msg ${backupStatus.ok ? 'is-ok' : 'is-error'}`}
              role="status"
            >
              {backupStatus.message}
            </p>
          )}
        </ParchmentDossierCard>

        {/* Account Summary & Sign Out */}
        <ParchmentDossierCard>
          <h2 className="dossier-card-heading">Akun Penjelajah</h2>
          <div className="settings-account-box">
            <CompassIcon size={24} style={{ color: 'var(--color-outline)', flexShrink: 0 }} />
            <div>
              <strong>{profile?.displayName ?? 'Explorer'}</strong>
              <p className="settings-item-desc" style={{ margin: '2px 0 0' }}>
                {profile?.email ?? 'Belum masuk'} • {profile?.xp ?? 0} XP • Rekor {profile?.bestStreak ?? 0} hari
              </p>
            </div>
          </div>

          <button
            type="button"
            className="btn btn--secondary"
            style={{
              width: '100%',
              marginTop: '14px',
              borderColor: 'var(--color-error)',
              color: 'var(--color-error-text)',
            }}
            onClick={onLogout}
          >
            <ExitIcon size={18} />
            <span>Keluar dari Akun</span>
          </button>
        </ParchmentDossierCard>
      </div>
    </div>
  );
}
