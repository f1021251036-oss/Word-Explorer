import type { AuthScreenMode, NavTab } from '../data/models';

export type AppRoute =
  | { kind: 'root' }
  | { kind: 'auth'; mode: AuthScreenMode }
  | { kind: 'onboarding' }
  | { kind: 'questionnaire' }
  | { kind: 'diagnostic' }
  | { kind: 'tab'; tab: NavTab; storyId?: string }
  | { kind: 'quiz'; levelId: string }
  | { kind: 'not_found'; path: string };

const TAB_PATH_MAP: Record<NavTab, string> = {
  map: '/map',
  camp: '/camp',
  lexicon: '/lexicon',
  lore: '/lore',
  beasts: '/beasts',
  settings: '/settings',
};

const PATH_TAB_MAP: Record<string, NavTab> = {
  '/map': 'map',
  '/camp': 'camp',
  '/lexicon': 'lexicon',
  '/lore': 'lore',
  '/beasts': 'beasts',
  '/settings': 'settings',
};

const VALID_LEVEL_ID_REGEX = /^([1-5])-([1-9]|10)$/;

export function normalizePathname(rawPathname: string): string {
  const trimmed = rawPathname.trim();
  if (!trimmed || trimmed === '/') return '/';
  const noTrailing = trimmed.replace(/\/+$/, '');
  return noTrailing.startsWith('/') ? noTrailing : `/${noTrailing}`;
}

export function isValidLevelId(levelId: string): boolean {
  return VALID_LEVEL_ID_REGEX.test(levelId);
}

export function parseUrlToRoute(rawPathname: string, rawSearch = ''): AppRoute {
  const pathname = normalizePathname(rawPathname);
  if (pathname === '/') {
    return { kind: 'root' };
  }

  if (pathname === '/login') return { kind: 'auth', mode: 'login' };
  if (pathname === '/register') return { kind: 'auth', mode: 'register' };
  if (pathname === '/verify') return { kind: 'auth', mode: 'verify' };
  if (pathname === '/forgot') return { kind: 'auth', mode: 'forgot' };

  if (pathname === '/onboarding') return { kind: 'onboarding' };
  if (pathname === '/questionnaire') return { kind: 'questionnaire' };
  if (pathname === '/diagnostic') return { kind: 'diagnostic' };

  const matchedTab = PATH_TAB_MAP[pathname];
  if (matchedTab) {
    if (matchedTab === 'lore' && rawSearch) {
      const params = new URLSearchParams(rawSearch);
      const storyId = params.get('story')?.trim() || undefined;
      return { kind: 'tab', tab: 'lore', storyId };
    }
    return { kind: 'tab', tab: matchedTab };
  }

  if (pathname.startsWith('/quiz/')) {
    const levelId = pathname.slice('/quiz/'.length).trim();
    if (isValidLevelId(levelId)) {
      return { kind: 'quiz', levelId };
    }
    return { kind: 'not_found', path: pathname };
  }

  return { kind: 'not_found', path: pathname };
}

export function getTabHref(tab: NavTab): string {
  return TAB_PATH_MAP[tab] ?? '/map';
}

export function buildUrlForRoute(route: AppRoute): string {
  switch (route.kind) {
    case 'root':
      return '/';
    case 'auth':
      if (route.mode === 'register') return '/register';
      if (route.mode === 'verify') return '/verify';
      if (route.mode === 'forgot') return '/forgot';
      return '/login';
    case 'onboarding':
      return '/onboarding';
    case 'questionnaire':
      return '/questionnaire';
    case 'diagnostic':
      return '/diagnostic';
    case 'tab':
      if (route.tab === 'lore' && route.storyId) {
        return `/lore?story=${encodeURIComponent(route.storyId)}`;
      }
      return getTabHref(route.tab);
    case 'quiz':
      return `/quiz/${route.levelId}`;
    case 'not_found':
      return route.path || '/';
  }
}

export function getDocumentTitleForRoute(route: AppRoute): string {
  switch (route.kind) {
    case 'auth':
      if (route.mode === 'register') return 'Daftar Akun — Word Explorer';
      if (route.mode === 'verify') return 'Verifikasi Email — Word Explorer';
      if (route.mode === 'forgot') return 'Pulihkan Sandi — Word Explorer';
      return 'Masuk — Word Explorer';
    case 'onboarding':
      return 'Selamat Datang — Word Explorer';
    case 'questionnaire':
      return 'Kuesioner Belajar — Word Explorer';
    case 'diagnostic':
      return 'Diagnostic Test — Word Explorer';
    case 'tab':
      switch (route.tab) {
        case 'map':
          return 'Peta Petualangan — Word Explorer';
        case 'camp':
          return 'Camp Penjelajah — Word Explorer';
        case 'lexicon':
          return 'Explorer Lexicon — Word Explorer';
        case 'lore':
          return 'Adventure Lore — Word Explorer';
        case 'beasts':
          return 'Sahabat Hewan — Word Explorer';
        case 'settings':
          return 'Pengaturan — Word Explorer';
      }
      break;
    case 'quiz':
      return `Misi Level ${route.levelId} — Word Explorer`;
    case 'not_found':
      return 'Halaman Tidak Ditemukan — Word Explorer';
    case 'root':
      return 'Word Explorer — Petualangan Kosakata SMP';
  }
}

export function navigateBrowserUrl(url: string, replace = false): void {
  if (typeof window === 'undefined') return;
  const current = `${window.location.pathname}${window.location.search}`;
  if (current === url) return;
  try {
    if (replace) {
      window.history.replaceState(null, '', url);
    } else {
      window.history.pushState(null, '', url);
    }
  } catch {
    // Graceful fallback if history API is restricted
  }
}
