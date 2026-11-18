import { useEffect, useRef, useState, type MouseEvent as ReactMouseEvent, type ReactNode } from 'react';
import {
  LEARNING_PATH_META,
  canAccessPath,
  type LearningPath,
  type NavTab,
  type UserProfile,
} from '../data/models';
import { getTabHref } from '../utils/router';
import {
  BeastPawIcon,
  BookIcon,
  CampIcon,
  CheckmarkIcon,
  ChevronDownIcon,
  CompassIcon,
  ExitIcon,
  LockIcon,
  MapIcon,
  ScrollIcon,
  SettingsIcon,
  SoundIcon,
} from './ExpeditionIcons';

interface AppShellProps {
  profile: UserProfile;
  currentTab: NavTab;
  selectedPath: LearningPath;
  isSoundEnabled: boolean;
  onSelectTab: (tab: NavTab) => void;
  onSelectPath: (path: LearningPath) => void;
  onToggleSound: () => void;
  onLogout: () => void;
  children: ReactNode;
}

const NAV_ITEMS: Array<{ id: NavTab; label: string }> = [
  { id: 'map', label: 'MAP' },
  { id: 'camp', label: 'CAMP' },
  { id: 'lexicon', label: 'LEXICON' },
  { id: 'lore', label: 'LORE' },
  { id: 'beasts', label: 'BEASTS' },
];

function renderNavIcon(tab: NavTab) {
  switch (tab) {
    case 'map':
      return <MapIcon size={20} />;
    case 'camp':
      return <CampIcon size={20} />;
    case 'lexicon':
      return <BookIcon size={20} />;
    case 'lore':
      return <ScrollIcon size={20} />;
    case 'beasts':
      return <BeastPawIcon size={20} />;
    case 'settings':
      return <SettingsIcon size={20} />;
  }
}

export function AppShell({
  profile,
  currentTab,
  selectedPath,
  isSoundEnabled,
  onSelectTab,
  onSelectPath,
  onToggleSound,
  onLogout,
  children,
}: AppShellProps) {
  const [tierOpen, setTierOpen] = useState(false);
  const tierWrapperRef = useRef<HTMLDivElement>(null);
  const sidebarTierRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!tierOpen) return;
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as Node;
      if (
        !tierWrapperRef.current?.contains(target) &&
        !sidebarTierRef.current?.contains(target)
      ) {
        setTierOpen(false);
      }
    };
    const handleKey = (event: KeyboardEvent) => {
      if (event.key === 'Escape') setTierOpen(false);
    };
    window.addEventListener('mousedown', handleClickOutside);
    window.addEventListener('keydown', handleKey);
    return () => {
      window.removeEventListener('mousedown', handleClickOutside);
      window.removeEventListener('keydown', handleKey);
    };
  }, [tierOpen]);

  const renderTierMenu = () => {
    if (!tierOpen) return null;
    const paths: LearningPath[] = ['LOW', 'MIDDLE', 'HIGH'];
    return (
      <div className="tier-menu" role="menu" aria-label="Pilih jalur belajar">
        <div className="tier-menu-header">
          <span className="tier-menu-kicker">PILIH JALUR BELAJAR</span>
          <span className="tier-menu-sub">Sesuaikan tingkat kosakata &amp; bahasa pengantar</span>
        </div>
        {paths.map((pathCode) => {
          const meta = LEARNING_PATH_META[pathCode];
          const isCurrent = selectedPath === pathCode;
          const isUnlocked = canAccessPath(profile.initialPath, pathCode);
          return (
            <button
              key={pathCode}
              type="button"
              role="menuitemradio"
              aria-checked={isCurrent}
              disabled={!isUnlocked}
              data-testid={`tier_option_${pathCode.toLowerCase()}`}
              className={`tier-option-btn ${
                isCurrent ? 'is-active' : isUnlocked ? 'is-available' : 'is-locked'
              }`}
              onClick={() => {
                if (!isUnlocked) return;
                onSelectPath(pathCode);
                setTierOpen(false);
              }}
            >
              <div>
                <span className="tier-option-title">{meta.title}</span>
                <span className="tier-option-desc">{meta.uiLanguage}</span>
              </div>
              <div className="tier-option-badges">
                <span
                  className={`pill-badge ${
                    isCurrent
                      ? 'pill-badge--active'
                      : isUnlocked
                        ? 'pill-badge--available'
                        : 'pill-badge--locked'
                  }`}
                >
                  {isCurrent && <CheckmarkIcon size={12} />}
                  {!isUnlocked && <LockIcon size={12} />}
                  {isCurrent ? 'Aktif' : isUnlocked ? 'Tersedia' : 'Terkunci'}
                </span>
                <span className="pill-badge pill-badge--cefr">{meta.cefrLabel}</span>
              </div>
            </button>
          );
        })}
      </div>
    );
  };

  const handleAnchorClick =
    (tab: NavTab) => (event: ReactMouseEvent<HTMLAnchorElement>) => {
      if (
        event.metaKey ||
        event.ctrlKey ||
        event.shiftKey ||
        event.altKey ||
        event.button !== 0
      ) {
        return;
      }
      event.preventDefault();
      onSelectTab(tab);
    };

  return (
    <div className="app-shell">
      <a href="#main-content" className="skip-link">
        Lewati ke konten utama
      </a>
      {/* Phone TopBar (< 600px) */}
      <header className="app-topbar">
        <div className="tier-wrapper" ref={tierWrapperRef}>
          <button
            type="button"
            className="tier-chip-btn"
            aria-expanded={tierOpen}
            aria-haspopup="menu"
            data-testid="path_selector_button"
            onClick={() => setTierOpen((prev) => !prev)}
          >
            <CompassIcon size={16} />
            <span>{selectedPath}</span>
            <ChevronDownIcon size={16} className={`tier-chevron ${tierOpen ? 'is-open' : ''}`} />
          </button>
          {renderTierMenu()}
        </div>

        <div className="topbar-actions">
          <div className="streak-badge" aria-label={`Streak ${profile.currentStreak} hari`}>
            <span aria-hidden="true">🔥</span>
            <span>{profile.currentStreak}d</span>
          </div>

          <button
            type="button"
            className="icon-btn icon-btn--accent"
            aria-label={isSoundEnabled ? 'Matikan suara efek' : 'Nyalakan suara efek'}
            data-testid="sound_toggle_button"
            onClick={onToggleSound}
          >
            <SoundIcon size={20} enabled={isSoundEnabled} />
          </button>

          <a
            href={getTabHref('settings')}
            className="icon-btn"
            aria-label="Pengaturan"
            data-testid="topbar_settings_button"
            onClick={handleAnchorClick('settings')}
          >
            <SettingsIcon size={20} />
          </a>

          <button
            type="button"
            className="icon-btn"
            aria-label="Keluar dari akun"
            data-testid="logout_icon_button"
            onClick={onLogout}
          >
            <ExitIcon size={20} />
          </button>
        </div>
      </header>

      {/* Tablet Compact Rail (600px+) & Desktop Left Sidebar (1024px+) */}
      <aside className="app-sidebar" aria-label="Navigasi utama">
        <div style={{ width: '100%' }}>
          <div className="sidebar-brand">
            <div className="sidebar-logo-mark" aria-hidden="true">
              <CompassIcon size={22} />
            </div>
            <div>
              <div className="sidebar-title">WORD EXPLORER</div>
              <div className="sidebar-subtitle">Petualangan Kosakata SMP</div>
            </div>
          </div>

          <div className="sidebar-tier-block tier-wrapper" ref={sidebarTierRef}>
            <button
              type="button"
              className="tier-chip-btn"
              style={{ width: '100%', justifyContent: 'space-between' }}
              aria-expanded={tierOpen}
              aria-haspopup="menu"
              onClick={() => setTierOpen((prev) => !prev)}
            >
              <span style={{ display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
                <CompassIcon size={16} />
                <span>Jalur: {selectedPath}</span>
              </span>
              <ChevronDownIcon size={16} className={`tier-chevron ${tierOpen ? 'is-open' : ''}`} />
            </button>
            {renderTierMenu()}
          </div>
        </div>

        <nav className="sidebar-nav" aria-label="Menu eksplorasi">
          {NAV_ITEMS.map((item) => {
            const isActive = currentTab === item.id;
            return (
              <a
                key={item.id}
                href={getTabHref(item.id)}
                className={`sidebar-link ${isActive ? 'is-active' : ''}`}
                aria-current={isActive ? 'page' : undefined}
                data-testid={`nav_tab_${item.id}`}
                onClick={handleAnchorClick(item.id)}
              >
                {renderNavIcon(item.id)}
                <span>{item.label}</span>
              </a>
            );
          })}
          <a
            href={getTabHref('settings')}
            className={`sidebar-link ${currentTab === 'settings' ? 'is-active' : ''}`}
            aria-current={currentTab === 'settings' ? 'page' : undefined}
            data-testid="nav_tab_settings"
            onClick={handleAnchorClick('settings')}
          >
            {renderNavIcon('settings')}
            <span>SETTINGS</span>
          </a>
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-streak-card">
            <span className="sidebar-streak-text">🔥 Streak {profile.currentStreak} hari</span>
            <span className="sidebar-streak-text" style={{ color: 'var(--color-reward)' }}>
              {profile.xp} XP
            </span>
          </div>
          <div className="sidebar-utility-row">
            <button
              type="button"
              className="icon-btn icon-btn--accent"
              aria-label={isSoundEnabled ? 'Matikan suara efek' : 'Nyalakan suara efek'}
              onClick={onToggleSound}
            >
              <SoundIcon size={20} enabled={isSoundEnabled} />
            </button>
            <button
              type="button"
              className="icon-btn"
              aria-label="Keluar dari akun"
              onClick={onLogout}
            >
              <ExitIcon size={20} />
            </button>
          </div>
        </div>
      </aside>

      {/* Main Content Workspace */}
      <main
        id="main-content"
        tabIndex={-1}
        className={`app-main ${currentTab === 'map' ? 'app-main--map' : ''}`}
      >
        {children}
      </main>

      {/* Phone Bottom Navigation (< 600px) */}
      <nav className="app-bottombar" aria-label="Navigasi bawah">
        {NAV_ITEMS.map((item) => {
          const isActive = currentTab === item.id;
          return (
            <a
              key={item.id}
              href={getTabHref(item.id)}
              className={`bottombar-link ${isActive ? 'is-active' : ''}`}
              aria-current={isActive ? 'page' : undefined}
              data-testid={`bottom_nav_${item.id}`}
              onClick={handleAnchorClick(item.id)}
            >
              {renderNavIcon(item.id)}
              <span className="bottombar-label">{item.label}</span>
            </a>
          );
        })}
      </nav>
    </div>
  );
}
