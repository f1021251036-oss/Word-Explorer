import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  buildInitialLevels,
  canAccessPath,
  computeStarsForScore,
  DAILY_LEVEL_LIMIT,
  INITIAL_COMPANIONS,
  isRegionUnlocked,
  LEARNING_PATH_META,
  type AppScreen,
  type AuthScreenMode,
  type BackendConfigStatus,
  type CompanionAnimal,
  type DailyVocabAssignment,
  type ExplorationRegion,
  type LearningPath,
  type LevelInfo,
  type NavTab,
  type QuizUiState,
  type RegisteredAccount,
  type RemedialTask,
  type UserProfile,
} from '../data/models';
import {
  generateRemedialQuestion,
  generateTenQuestionsForLevel,
  javaStringHashCode,
  shuffleWithRng,
} from '../data/questionGenerator';
import {
  ADVENTURE_STORIES,
  DIAGNOSTIC_QUESTIONS,
  INITIAL_VOCABULARY,
  type AdventureStory,
  type Question,
  type VocabularyItem,
} from '../data/vocabCatalog';
import { formatTimeUntilMidnight, getTodayIsoDate, sha256Hex, soundManager, speakWord } from '../utils/sound';
import {
  buildUrlForRoute,
  getDocumentTitleForRoute,
  getTabHref,
  navigateBrowserUrl,
  parseUrlToRoute,
  type AppRoute,
} from '../utils/router';

const STORAGE_KEY = 'we_explorer_state_v1';
const QUIZ_SESSION_KEY_PREFIX = 'we_active_quiz_session_v1_';
const GUIDE_KEY_PREFIX = 'we_first_time_guide_done_';
const CHIP_KEY_PREFIX = 'we_next_step_chip_dismissed_';
const SOUND_KEY = 'we_sound_enabled_v1';
const HIGH_CONTRAST_KEY = 'we_high_contrast_v1';
const REDUCED_MOTION_KEY = 'we_reduced_motion_v1';

function getApiUrl(path: string): string {
  const base = (import.meta.env.VITE_API_BASE_URL ?? '').trim().replace(/\/+$/, '');
  return `${base}${path}`;
}

interface PersistedQuizSession {
  quizState: QuizUiState;
  matchingSelectedEnglish: string | null;
  matchingAnswers: Record<string, string>;
  typedAnswer: string;
  clueStep: number;
  scrambledSlots: string[];
  availableLetters: Array<{ id: number; char: string }>;
  sentenceBuilderSlots: string[];
  availableSentenceChips: Array<{ id: number; token: string }>;
  categoryBucketAssignments: Record<string, string>;
  selectedCategoryWord: string | null;
  memoryFlippedIndices: number[];
  memoryMatchedCards: string[];
  selectedTrueFalse: boolean | null;
  selectedGridCoords: Array<[number, number]>;
}

interface PersistedUserData {
  profile: UserProfile;
  levelsByPath: Record<LearningPath, LevelInfo[]>;
  dailyCompletedByDate: Record<string, string[]>; // date -> unique newly completed level keys `${path}:${id}`
  dailyVocabDate: string;
  dailyVocabAssignments: DailyVocabAssignment[];
  learnedVocabIds: string[];
  storyCompletions: string[];
  questionnaireAnswers: Record<number, string>;
}

interface PersistedRoot {
  activeUserId: string | null;
  accounts: Record<string, RegisteredAccount>;
  userData: Record<string, PersistedUserData>;
}

function safeReadJson<T>(key: string, fallback: T): T {
  try {
    const raw = window.localStorage.getItem(key);
    if (!raw) return fallback;
    return JSON.parse(raw) as T;
  } catch {
    return fallback;
  }
}

function safeWriteJson(key: string, value: unknown): boolean {
  try {
    window.localStorage.setItem(key, JSON.stringify(value));
    return true;
  } catch {
    return false;
  }
}

function safeGetBool(key: string, fallback: boolean): boolean {
  try {
    const v = window.localStorage.getItem(key);
    return v === null ? fallback : v === 'true';
  } catch {
    return fallback;
  }
}

function safeSetBool(key: string, value: boolean): void {
  try {
    window.localStorage.setItem(key, String(value));
  } catch {
    // Graceful no-op
  }
}

function safeRemoveKey(key: string): void {
  try {
    window.localStorage.removeItem(key);
  } catch {
    // Graceful no-op
  }
}

function buildAllPathLevels(): Record<LearningPath, LevelInfo[]> {
  return {
    LOW: buildInitialLevels('LOW'),
    MIDDLE: buildInitialLevels('MIDDLE'),
    HIGH: buildInitialLevels('HIGH'),
  };
}

function buildDailyVocabForPath(path: LearningPath, date: string): DailyVocabAssignment[] {
  const poolSize = LEARNING_PATH_META[path].dailyWordPoolSize;
  const seed = Math.abs(javaStringHashCode(`${date}_${path}`));
  let state = seed >>> 0;
  const rng = () => {
    state = (state * 1664525 + 1013904223) >>> 0;
    return state / 4294967296;
  };
  const shuffled = shuffleWithRng(INITIAL_VOCABULARY, rng).slice(0, poolSize);
  return shuffled.map((item) => ({
    word: item.word.toLowerCase(),
    status: 'NEW',
    date,
  }));
}

function createDemoUserData(today: string): PersistedUserData {
  const levelsByPath = buildAllPathLevels();
  const markDemoStage1 = (list: LevelInfo[]): LevelInfo[] =>
    list.map((lvl) => {
      if (lvl.id === '1-1') {
        return { ...lvl, status: 'COMPLETED', stars: 3, bestScore: 10 };
      }
      if (lvl.id === '1-2') {
        return { ...lvl, status: 'AVAILABLE' };
      }
      return lvl;
    });
  levelsByPath.LOW = markDemoStage1(levelsByPath.LOW);
  levelsByPath.MIDDLE = markDemoStage1(levelsByPath.MIDDLE);
  levelsByPath.HIGH = markDemoStage1(levelsByPath.HIGH);

  return {
    profile: {
      id: 'demo_arka_explorer',
      email: 'arka.explorer@wordexplorer.app',
      displayName: 'Arka Explorer',
      initialPath: 'HIGH',
      currentPath: 'LOW',
      dailyTarget: LEARNING_PATH_META.LOW.targetWordsPerDay,
      currentStreak: 7,
      bestStreak: 7,
      xp: 240,
      activeCompanionId: 'fox',
      lastStudyDate: today,
      onboardingCompleted: true,
      diagnosticScore: 6,
    },
    levelsByPath,
    dailyCompletedByDate: { [today]: [] },
    dailyVocabDate: today,
    dailyVocabAssignments: buildDailyVocabForPath('LOW', today),
    learnedVocabIds: ['v_compass', 'v_forest'],
    storyCompletions: [],
    questionnaireAnswers: {},
  };
}

function createBlankUserData(account: RegisteredAccount, today: string): PersistedUserData {
  return {
    profile: {
      id: account.id,
      email: account.email,
      displayName: account.displayName,
      initialPath: 'LOW',
      currentPath: 'LOW',
      dailyTarget: LEARNING_PATH_META.LOW.targetWordsPerDay,
      currentStreak: 1,
      bestStreak: 1,
      xp: 0,
      activeCompanionId: 'fox',
      lastStudyDate: today,
      onboardingCompleted: false,
      diagnosticScore: 0,
    },
    levelsByPath: buildAllPathLevels(),
    dailyCompletedByDate: { [today]: [] },
    dailyVocabDate: today,
    dailyVocabAssignments: buildDailyVocabForPath('LOW', today),
    learnedVocabIds: [],
    storyCompletions: [],
    questionnaireAnswers: {},
  };
}

const INITIAL_QUIZ_STATE: QuizUiState = {
  stage: 1,
  level: 1,
  path: 'LOW',
  questions: [],
  currentQuestionIndex: 0,
  selectedOption: null,
  isAnswerChecked: false,
  isAnswerCorrect: false,
  firstAttemptScore: 0,
  failedQuestions: [],
  isQuizFinished: false,
  isRemedialMode: false,
  initialRemedialCount: 0,
  remedialTasks: [],
  currentRemedialIndex: 0,
  remedialSelectedOption: null,
  isRemedialChecked: false,
  isRemedialCorrect: false,
  starsAwarded: 0,
  isLevelCompleted: false,
};

export function useExplorerStore() {
  const [root, setRoot] = useState<PersistedRoot>(() => {
    const initial: PersistedRoot = {
      activeUserId: null,
      accounts: {},
      userData: {},
    };
    const loaded = safeReadJson<PersistedRoot>(STORAGE_KEY, initial);
    return {
      activeUserId: loaded.activeUserId ?? null,
      accounts: loaded.accounts ?? {},
      userData: loaded.userData ?? {},
    };
  });

  const [storageWarning, setStorageWarning] = useState<string | null>(null);

  useEffect(() => {
    const ok = safeWriteJson(STORAGE_KEY, root);
    if (!ok) {
      setStorageWarning(
        'Penyimpanan browser penuh atau diblokir mode privat. Unduh cadangan JSON di Pengaturan agar progres tetap aman.'
      );
    }
  }, [root]);

  const activeUserId = root.activeUserId;
  const activeUserData = activeUserId ? root.userData[activeUserId] ?? null : null;
  const profile: UserProfile | null = activeUserData?.profile ?? null;

  // Determine initial screen cleanly with browser URL awareness
  const [notFoundPath, setNotFoundPath] = useState<string | null>(() => {
    if (typeof window === 'undefined') return null;
    const parsed = parseUrlToRoute(window.location.pathname, window.location.search);
    return parsed.kind === 'not_found' ? parsed.path : null;
  });

  const [appScreen, setAppScreen] = useState<AppScreen>(() => {
    if (!activeUserData) return 'auth';
    if (!activeUserData.profile.onboardingCompleted) {
      if (typeof window !== 'undefined') {
        const parsed = parseUrlToRoute(window.location.pathname, window.location.search);
        if (parsed.kind === 'questionnaire' || parsed.kind === 'diagnostic') {
          return parsed.kind;
        }
      }
      return 'onboarding';
    }
    if (typeof window !== 'undefined') {
      const parsed = parseUrlToRoute(window.location.pathname, window.location.search);
      if (parsed.kind === 'quiz') return 'quiz';
    }
    return 'main';
  });

  const [currentTab, setCurrentTab] = useState<NavTab>(() => {
    if (typeof window !== 'undefined') {
      const parsed = parseUrlToRoute(window.location.pathname, window.location.search);
      if (parsed.kind === 'tab') return parsed.tab;
    }
    return 'map';
  });

  // Auth states
  const [authMode, setAuthMode] = useState<AuthScreenMode>(() => {
    if (typeof window !== 'undefined') {
      const parsed = parseUrlToRoute(window.location.pathname, window.location.search);
      if (parsed.kind === 'auth' && parsed.mode !== 'verify') return parsed.mode;
    }
    return 'login';
  });
  const [authEmail, setAuthEmail] = useState('');
  const [authPassword, setAuthPassword] = useState('');
  const [authDisplayName, setAuthDisplayName] = useState('');
  const [authVerificationCode, setAuthVerificationCode] = useState('');
  const [activeVerificationEmail, setActiveVerificationEmail] = useState('');
  const [activeSentCode, setActiveSentCode] = useState<string | null>(null);
  const [resendCooldownSeconds, setResendCooldownSeconds] = useState(0);
  const [authError, setAuthError] = useState<string | null>(null);
  const [authSuccessMessage, setAuthSuccessMessage] = useState<string | null>(null);
  const [isAuthLoading, setIsAuthLoading] = useState(false);
  const [showBackendStatusDialog, setShowBackendStatusDialog] = useState(false);
  const [otpSource, setOtpSource] = useState<'server' | 'local'>('local');
  const [serverHealth, setServerHealth] = useState<{
    reachable: boolean;
    smtpConfigured: boolean;
    googleOAuthConfigured: boolean;
  }>({
    reachable: false,
    smtpConfigured: false,
    googleOAuthConfigured: false,
  });

  useEffect(() => {
    const controller = new AbortController();
    fetch(getApiUrl('/api/health'), { signal: controller.signal })
      .then((res) => (res.ok ? res.json() : null))
      .then((data: { ok?: boolean; smtpConfigured?: boolean; googleOAuthConfigured?: boolean } | null) => {
        if (data?.ok) {
          setServerHealth({
            reachable: true,
            smtpConfigured: Boolean(data.smtpConfigured),
            googleOAuthConfigured: Boolean(data.googleOAuthConfigured),
          });
        }
      })
      .catch(() => {
        // Running static frontend without companion /server; local fallback remains active
      });
    return () => controller.abort();
  }, []);

  const googleWebClientId = useMemo(() => {
    const raw = (import.meta.env.VITE_GOOGLE_WEB_CLIENT_ID ?? '').trim();
    return raw.length > 10 && !raw.includes('YOUR_') ? raw : '';
  }, []);

  const backendStatus: BackendConfigStatus = useMemo(() => {
    const configured = Boolean(googleWebClientId) || serverHealth.googleOAuthConfigured;
    return {
      googleOAuthClientIdConfigured: configured,
      googleOAuthClientIdPreview: googleWebClientId ? googleWebClientId.slice(0, 14) : undefined,
      smtpConfigured: serverHealth.smtpConfigured,
      otpAlgorithm: serverHealth.reachable
        ? 'HMAC-SHA256 Server + TTL 5 menit + maks 5 percobaan salah'
        : 'SHA-256 Digest + 5 menit kadaluarsa + maks 5 percobaan salah (Sandbox Lokal)',
      accountLinkingStatus: 'Penautan kanonikal berdasarkan email aktif (Google + OTP Email)',
      dailyLimitStatus: `Batas ketat ${DAILY_LEVEL_LIMIT} level baru per hari (reset 00:00 waktu lokal)`,
    };
  }, [googleWebClientId, serverHealth]);

  // Cooldown timer for OTP resend
  useEffect(() => {
    if (resendCooldownSeconds <= 0) return undefined;
    const id = window.setInterval(() => {
      setResendCooldownSeconds((prev) => (prev <= 1 ? 0 : prev - 1));
    }, 1000);
    return () => window.clearInterval(id);
  }, [resendCooldownSeconds]);

  // Midnight countdown timer
  const [timeUntilMidnightReset, setTimeUntilMidnightReset] = useState<string>(() =>
    formatTimeUntilMidnight()
  );
  useEffect(() => {
    const id = window.setInterval(() => {
      setTimeUntilMidnightReset(formatTimeUntilMidnight());
    }, 1000);
    return () => window.clearInterval(id);
  }, []);

  // Onboarding / Questionnaire / Diagnostic state
  const [diagnosticIndex, setDiagnosticIndex] = useState(0);
  const [diagnosticScore, setDiagnosticScore] = useState(0);
  const [diagnosticFinished, setDiagnosticFinished] = useState(false);
  const [recommendedPath, setRecommendedPath] = useState<LearningPath>('LOW');

  // Map selection states
  const [selectedLevel, setSelectedLevel] = useState<LevelInfo | null>(null);
  const [selectedRegion, setSelectedRegion] = useState<ExplorationRegion | null>(null);
  const [dailyLimitError, setDailyLimitError] = useState<string | null>(null);

  // Preferences & guide state
  const [isSoundEnabled, setIsSoundEnabled] = useState<boolean>(() => safeGetBool(SOUND_KEY, true));
  const [highContrast, setHighContrast] = useState<boolean>(() =>
    safeGetBool(HIGH_CONTRAST_KEY, false)
  );
  const [reducedMotion, setReducedMotion] = useState<boolean>(() =>
    safeGetBool(REDUCED_MOTION_KEY, false)
  );

  useEffect(() => {
    soundManager.setEnabled(isSoundEnabled);
  }, [isSoundEnabled]);

  const [showFirstTimeGuide, setShowFirstTimeGuide] = useState<boolean>(false);
  const [guideStep, setGuideStep] = useState<number>(0);
  const [isNextStepChipDismissed, setIsNextStepChipDismissed] = useState<boolean>(false);

  const syncGuideFlagsForUser = useCallback((uid: string) => {
    setShowFirstTimeGuide(!safeGetBool(`${GUIDE_KEY_PREFIX}${uid}`, false));
    setGuideStep(0);
    setIsNextStepChipDismissed(safeGetBool(`${CHIP_KEY_PREFIX}${uid}`, false));
  }, []);

  useEffect(() => {
    if (activeUserId) {
      syncGuideFlagsForUser(activeUserId);
    }
  }, [activeUserId, syncGuideFlagsForUser]);

  // Ensure daily vocabulary rotates automatically when the date changes
  useEffect(() => {
    if (!activeUserId || !activeUserData) return;
    const today = getTodayIsoDate();
    if (activeUserData.dailyVocabDate !== today) {
      setRoot((prev) => {
        const current = prev.userData[activeUserId];
        if (!current) return prev;
        return {
          ...prev,
          userData: {
            ...prev.userData,
            [activeUserId]: {
              ...current,
              dailyVocabDate: today,
              dailyVocabAssignments: buildDailyVocabForPath(current.profile.currentPath, today),
            },
          },
        };
      });
    }
  }, [activeUserId, activeUserData]);

  // Quiz & Interactive Question State
  const [quizState, setQuizState] = useState<QuizUiState>(INITIAL_QUIZ_STATE);
  const [matchingSelectedEnglish, setMatchingSelectedEnglish] = useState<string | null>(null);
  const [matchingAnswers, setMatchingAnswers] = useState<Record<string, string>>({});
  const [typedAnswer, setTypedAnswer] = useState('');
  const [clueStep, setClueStep] = useState(0);
  const [scrambledSlots, setScrambledSlots] = useState<string[]>([]);
  const [availableLetters, setAvailableLetters] = useState<Array<{ id: number; char: string }>>([]);
  const [sentenceBuilderSlots, setSentenceBuilderSlots] = useState<string[]>([]);
  const [availableSentenceChips, setAvailableSentenceChips] = useState<
    Array<{ id: number; token: string }>
  >([]);
  const [categoryBucketAssignments, setCategoryBucketAssignments] = useState<
    Record<string, string>
  >({});
  const [selectedCategoryWord, setSelectedCategoryWord] = useState<string | null>(null);
  const [memoryFlippedIndices, setMemoryFlippedIndices] = useState<number[]>([]);
  const [memoryMatchedCards, setMemoryMatchedCards] = useState<string[]>([]);
  const [selectedTrueFalse, setSelectedTrueFalse] = useState<boolean | null>(null);
  const [selectedGridCoords, setSelectedGridCoords] = useState<Array<[number, number]>>([]);

  // Lexicon & Lore local UI states
  const [vocabSearchQuery, setVocabSearchQuery] = useState('');
  const [vocabCategoryFilter, setVocabCategoryFilter] = useState('All');
  const [activeStory, setActiveStory] = useState<AdventureStory | null>(null);

  const selectedPath: LearningPath = profile?.currentPath ?? 'LOW';
  const levels: LevelInfo[] = useMemo(() => {
    if (!activeUserData) return buildInitialLevels('LOW');
    return activeUserData.levelsByPath[selectedPath] ?? buildInitialLevels(selectedPath);
  }, [activeUserData, selectedPath]);

  const today = getTodayIsoDate();
  const dailyCompletedCount: number = activeUserData
    ? (activeUserData.dailyCompletedByDate[today] ?? []).length
    : 0;
  const dailyVocabAssignments: DailyVocabAssignment[] =
    activeUserData?.dailyVocabAssignments ?? [];

  const vocabularyList: VocabularyItem[] = useMemo(() => {
    const learnedSet = new Set(activeUserData?.learnedVocabIds ?? []);
    return INITIAL_VOCABULARY.map((item) => ({
      ...item,
      isLearned: learnedSet.has(item.id),
    }));
  }, [activeUserData?.learnedVocabIds]);

  const filteredVocabulary: VocabularyItem[] = useMemo(() => {
    const q = vocabSearchQuery.trim().toLowerCase();
    return vocabularyList.filter((item) => {
      const catMatch =
        vocabCategoryFilter === 'All' ||
        item.category.toLowerCase() === vocabCategoryFilter.toLowerCase();
      if (!catMatch) return false;
      if (!q) return true;
      return (
        item.word.toLowerCase().includes(q) ||
        item.indonesian.toLowerCase().includes(q) ||
        item.englishDef.toLowerCase().includes(q) ||
        item.category.toLowerCase().includes(q)
      );
    });
  }, [vocabularyList, vocabCategoryFilter, vocabSearchQuery]);

  const companionsList: Array<CompanionAnimal & { isUnlocked: boolean }> = useMemo(() => {
    const streak = profile?.currentStreak ?? 0;
    return INITIAL_COMPANIONS.map((c) => ({
      ...c,
      isUnlocked: streak >= c.requiredStreak,
    }));
  }, [profile?.currentStreak]);

  const storyCompletions = useMemo(
    () => new Set(activeUserData?.storyCompletions ?? []),
    [activeUserData?.storyCompletions]
  );

  // --- Routing Helpers & Auth Transitions ---
  const navigateToTab = useCallback((tab: NavTab) => {
    setNotFoundPath(null);
    if (tab !== 'lore') {
      setActiveStory(null);
    }
    setCurrentTab(tab);
    setAppScreen('main');
    navigateBrowserUrl(getTabHref(tab), false);
  }, []);

  const selectAuthMode = useCallback((nextMode: AuthScreenMode, replace = false) => {
    setNotFoundPath(null);
    setAuthMode(nextMode);
    navigateBrowserUrl(buildUrlForRoute({ kind: 'auth', mode: nextMode }), replace);
  }, []);

  const openStory = useCallback((story: AdventureStory | null) => {
    setActiveStory(story);
    navigateBrowserUrl(
      buildUrlForRoute({ kind: 'tab', tab: 'lore', storyId: story?.id }),
      false
    );
  }, []);

  const enterAuthenticatedUser = useCallback(
    (uid: string, onboardingCompleted: boolean) => {
      syncGuideFlagsForUser(uid);
      setNotFoundPath(null);
      setCurrentTab('map');
      setSelectedLevel(null);
      setSelectedRegion(null);
      setAppScreen(onboardingCompleted ? 'main' : 'onboarding');
      navigateBrowserUrl(onboardingCompleted ? '/map' : '/onboarding', false);
    },
    [syncGuideFlagsForUser]
  );

  const quickDemoLogin = useCallback(() => {
    soundManager.playClick();
    const todayIso = getTodayIsoDate();
    setRoot((prev) => {
      const existing = prev.userData['demo_arka_explorer'];
      return {
        ...prev,
        activeUserId: 'demo_arka_explorer',
        userData: {
          ...prev.userData,
          demo_arka_explorer: existing ?? createDemoUserData(todayIso),
        },
      };
    });
    setAuthError(null);
    setAuthSuccessMessage(null);
    enterAuthenticatedUser('demo_arka_explorer', true);
  }, [enterAuthenticatedUser]);

  const dispatchOtpChallenge = useCallback(
    async (
      email: string,
      purpose: 'register' | 'login' | 'resend'
    ): Promise<
      | { ok: true; source: 'server'; delivery: 'smtp' | 'sandbox'; sandboxCode: string | null }
      | { ok: true; source: 'local'; otp: string; otpHash: string; expiresAt: number }
      | { ok: false; error: string }
    > => {
      try {
        const response = await fetch(getApiUrl('/api/auth/otp/send'), {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, purpose }),
        });
        const payload = (await response.json().catch(() => null)) as {
          ok?: boolean;
          delivery?: 'smtp' | 'sandbox';
          sandboxCode?: string;
          error?: string;
        } | null;

        if (!response.ok || !payload?.ok) {
          return {
            ok: false,
            error: payload?.error ?? 'Gagal mengirim kode OTP dari server.',
          };
        }
        return {
          ok: true,
          source: 'server',
          delivery: payload.delivery ?? 'smtp',
          sandboxCode: payload.delivery === 'sandbox' ? payload.sandboxCode ?? null : null,
        };
      } catch {
        // Fallback when backend /server is not running alongside static frontend
        const otp = String(Math.floor(100000 + Math.random() * 900000));
        const otpHash = await sha256Hex(`${email}::${otp}`);
        const expiresAt = Date.now() + 5 * 60 * 1000;
        return { ok: true, source: 'local', otp, otpHash, expiresAt };
      }
    },
    []
  );

  const login = useCallback(async () => {
    const email = authEmail.trim().toLowerCase();
    const password = authPassword;
    if (!email || !password) {
      setAuthError('Harap masukkan email dan kata sandi.');
      return;
    }
    setIsAuthLoading(true);
    setAuthError(null);
    setAuthSuccessMessage(null);

    const account = root.accounts[email];
    if (!account) {
      setIsAuthLoading(false);
      setAuthError('Akun tidak ditemukan. Silakan daftar akun baru atau gunakan Demo.');
      return;
    }

    const passHash = await sha256Hex(`${email}::${password}`);
    if (account.passwordHash !== passHash) {
      setIsAuthLoading(false);
      setAuthError('Kata sandi tidak sesuai.');
      return;
    }

    if (!account.isEmailVerified) {
      const challenge = await dispatchOtpChallenge(email, 'login');
      if (!challenge.ok) {
        setIsAuthLoading(false);
        setAuthError(challenge.error);
        return;
      }
      if (challenge.source === 'server') {
        setOtpSource('server');
        setActiveSentCode(challenge.sandboxCode);
      } else {
        setOtpSource('local');
        setRoot((prev) => ({
          ...prev,
          accounts: {
            ...prev.accounts,
            [email]: {
              ...account,
              otpHash: challenge.otpHash,
              otpExpiresAt: challenge.expiresAt,
              otpFailedAttempts: 0,
            },
          },
        }));
        setActiveSentCode(challenge.otp);
      }
      setActiveVerificationEmail(email);
      setResendCooldownSeconds(60);
      setAuthMode('verify');
      navigateBrowserUrl('/verify', false);
      setAuthError('Akun belum diverifikasi. Kode OTP baru telah dikirimkan.');
      setIsAuthLoading(false);
      return;
    }

    const userData = root.userData[account.id];
    setRoot((prev) => ({ ...prev, activeUserId: account.id }));
    setIsAuthLoading(false);
    enterAuthenticatedUser(account.id, userData?.profile.onboardingCompleted ?? false);
  }, [authEmail, authPassword, dispatchOtpChallenge, enterAuthenticatedUser, root.accounts, root.userData]);

  const register = useCallback(async () => {
    const email = authEmail.trim().toLowerCase();
    const password = authPassword;
    const name = authDisplayName.trim();
    if (!email || !password || !name) {
      setAuthError('Harap lengkapi semua kolom pendaftaran.');
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setAuthError('Format email tidak valid. Masukkan alamat email aktif.');
      return;
    }
    if (password.length < 6) {
      setAuthError('Kata sandi minimal harus 6 karakter.');
      return;
    }
    if (root.accounts[email]) {
      setAuthError('Email sudah terdaftar. Silakan masuk atau gunakan email lain.');
      return;
    }

    setIsAuthLoading(true);
    setAuthError(null);
    const id = `usr_${Date.now().toString(36)}_${Math.random().toString(36).slice(2, 6)}`;
    const passwordHash = await sha256Hex(`${email}::${password}`);
    const challenge = await dispatchOtpChallenge(email, 'register');
    if (!challenge.ok) {
      setIsAuthLoading(false);
      setAuthError(challenge.error);
      return;
    }

    const newAccount: RegisteredAccount = {
      id,
      email,
      displayName: name,
      passwordHash,
      isEmailVerified: false,
      otpHash: challenge.source === 'local' ? challenge.otpHash : undefined,
      otpExpiresAt: challenge.source === 'local' ? challenge.expiresAt : undefined,
      otpFailedAttempts: 0,
    };

    const todayIso = getTodayIsoDate();
    setRoot((prev) => ({
      ...prev,
      accounts: { ...prev.accounts, [email]: newAccount },
      userData: { ...prev.userData, [id]: createBlankUserData(newAccount, todayIso) },
    }));

    setOtpSource(challenge.source);
    setActiveVerificationEmail(email);
    setActiveSentCode(challenge.source === 'server' ? challenge.sandboxCode : challenge.otp);
    setAuthVerificationCode('');
    setResendCooldownSeconds(60);
    setAuthSuccessMessage(
      challenge.source === 'server' && challenge.delivery === 'smtp'
        ? `Kode OTP 6-digit telah dikirim ke email ${email} (berlaku 5 menit).`
        : `Kode OTP 6-digit berhasil dibuat untuk ${email} (berlaku 5 menit).`
    );
    setAuthMode('verify');
    navigateBrowserUrl('/verify', false);
    setIsAuthLoading(false);
  }, [authDisplayName, authEmail, authPassword, dispatchOtpChallenge, root.accounts]);

  const verifyEmailCode = useCallback(async () => {
    const email = activeVerificationEmail.trim().toLowerCase();
    const code = authVerificationCode.trim();
    if (!email || code.length !== 6) {
      setAuthError('Harap masukkan kode OTP 6-digit.');
      return;
    }
    const account = root.accounts[email];
    if (!account) {
      setAuthError('Sesi verifikasi tidak ditemukan.');
      return;
    }

    setIsAuthLoading(true);
    if (otpSource === 'server') {
      try {
        const res = await fetch(getApiUrl('/api/auth/otp/verify'), {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, code }),
        });
        const data = (await res.json().catch(() => null)) as { ok?: boolean; error?: string } | null;
        if (!res.ok || !data?.ok) {
          setAuthError(data?.error ?? 'Kode OTP tidak cocok.');
          setIsAuthLoading(false);
          return;
        }
      } catch {
        setAuthError('Gagal terhubung ke server verifikasi OTP.');
        setIsAuthLoading(false);
        return;
      }
    } else {
      if ((account.otpFailedAttempts ?? 0) >= 5) {
        setAuthError('Terlalu banyak percobaan gagal (maks 5x). Kirim ulang kode OTP baru.');
        setIsAuthLoading(false);
        return;
      }
      if (!account.otpExpiresAt || Date.now() > account.otpExpiresAt) {
        setAuthError('Kode OTP telah kadaluarsa (> 5 menit). Silakan kirim ulang kode.');
        setIsAuthLoading(false);
        return;
      }
      const inputHash = await sha256Hex(`${email}::${code}`);
      if (inputHash !== account.otpHash) {
        const nextAttempts = (account.otpFailedAttempts ?? 0) + 1;
        setRoot((prev) => ({
          ...prev,
          accounts: {
            ...prev.accounts,
            [email]: { ...account, otpFailedAttempts: nextAttempts },
          },
        }));
        setAuthError(`Kode OTP tidak cocok (${nextAttempts}/5 percobaan gagal).`);
        setIsAuthLoading(false);
        return;
      }
    }

    const verifiedAccount: RegisteredAccount = {
      ...account,
      isEmailVerified: true,
      otpHash: undefined,
      otpExpiresAt: undefined,
      otpFailedAttempts: 0,
    };
    const onboardingCompleted = root.userData[account.id]?.profile.onboardingCompleted ?? false;
    setRoot((prev) => ({
      ...prev,
      activeUserId: account.id,
      accounts: { ...prev.accounts, [email]: verifiedAccount },
    }));
    setAuthSuccessMessage(`Akun berhasil diverifikasi! Selamat datang, ${account.displayName}.`);
    setIsAuthLoading(false);
    enterAuthenticatedUser(account.id, onboardingCompleted);
  }, [activeVerificationEmail, authVerificationCode, enterAuthenticatedUser, otpSource, root.accounts, root.userData]);

  const resendVerificationCode = useCallback(async () => {
    const email = activeVerificationEmail.trim().toLowerCase();
    if (!email || resendCooldownSeconds > 0) return;
    const account = root.accounts[email];
    if (!account) return;
    setIsAuthLoading(true);
    const challenge = await dispatchOtpChallenge(email, 'resend');
    if (!challenge.ok) {
      setIsAuthLoading(false);
      setAuthError(challenge.error);
      return;
    }
    if (challenge.source === 'server') {
      setOtpSource('server');
      setActiveSentCode(challenge.sandboxCode);
    } else {
      setOtpSource('local');
      setRoot((prev) => ({
        ...prev,
        accounts: {
          ...prev.accounts,
          [email]: {
            ...account,
            otpHash: challenge.otpHash,
            otpExpiresAt: challenge.expiresAt,
            otpFailedAttempts: 0,
          },
        },
      }));
      setActiveSentCode(challenge.otp);
    }
    setResendCooldownSeconds(60);
    setAuthError(null);
    setIsAuthLoading(false);
    setAuthSuccessMessage(`Kode OTP baru (berlaku 5 menit) telah disiapkan untuk ${email}.`);
  }, [activeVerificationEmail, dispatchOtpChallenge, resendCooldownSeconds, root.accounts]);

  const resetPassword = useCallback(async () => {
    const email = authEmail.trim().toLowerCase();
    const newPass = authPassword;
    if (!email || newPass.length < 6) {
      setAuthError('Masukkan email dan kata sandi baru (minimal 6 karakter).');
      return;
    }
    const account = root.accounts[email];
    if (!account) {
      setAuthError('Akun dengan email tersebut tidak ditemukan.');
      return;
    }
    const passwordHash = await sha256Hex(`${email}::${newPass}`);
    setRoot((prev) => ({
      ...prev,
      accounts: { ...prev.accounts, [email]: { ...account, passwordHash } },
    }));
    setAuthError(null);
    setAuthSuccessMessage('Kata sandi berhasil diperbarui! Silakan masuk kembali.');
    setAuthMode('login');
    navigateBrowserUrl('/login', false);
  }, [authEmail, authPassword, root.accounts]);

  const verifyGoogleCredential = useCallback(
    async (idToken: string) => {
      setIsAuthLoading(true);
      setAuthError(null);
      try {
        const res = await fetch(getApiUrl('/api/auth/google'), {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ idToken }),
        });
        const data = (await res.json().catch(() => null)) as {
          ok?: boolean;
          error?: string;
          user?: { sub: string; email: string; displayName: string };
        } | null;

        if (!res.ok || !data?.ok || !data.user) {
          setIsAuthLoading(false);
          setAuthError(data?.error ?? 'Verifikasi Google Sign-In gagal.');
          return;
        }

        const canonicalEmail = data.user.email.trim().toLowerCase();
        const existingAccount = root.accounts[canonicalEmail];
        const accountId = existingAccount?.id ?? `usr_g_${data.user.sub.slice(0, 12)}`;
        const todayIso = getTodayIsoDate();
        const randomSeedHash =
          existingAccount?.passwordHash ?? (await sha256Hex(`${canonicalEmail}::google::${data.user.sub}`));

        const linkedAccount: RegisteredAccount = {
          id: accountId,
          email: canonicalEmail,
          displayName: existingAccount?.displayName ?? data.user.displayName,
          passwordHash: randomSeedHash,
          isEmailVerified: true,
        };

        const existingUserData = root.userData[accountId];
        const onboardingCompleted = existingUserData?.profile.onboardingCompleted ?? false;
        setRoot((prev) => ({
          ...prev,
          activeUserId: accountId,
          accounts: { ...prev.accounts, [canonicalEmail]: linkedAccount },
          userData: {
            ...prev.userData,
            [accountId]: existingUserData ?? createBlankUserData(linkedAccount, todayIso),
          },
        }));
        setIsAuthLoading(false);
        enterAuthenticatedUser(accountId, onboardingCompleted);
      } catch {
        setIsAuthLoading(false);
        setAuthError('Server verifikasi Google belum aktif atau tidak dapat dijangkau.');
      }
    },
    [enterAuthenticatedUser, root.accounts, root.userData]
  );

  const signInWithGoogle = useCallback(() => {
    soundManager.playClick();
    if (!googleWebClientId) {
      setAuthError(
        'Google Sign-In membutuhkan VITE_GOOGLE_WEB_CLIENT_ID & endpoint backend (/api/auth/google).'
      );
      setShowBackendStatusDialog(true);
      return;
    }
    const gis = window.google?.accounts?.id;
    if (!gis) {
      setAuthError('SDK Google Identity Services masih dimuat. Coba klik tombol masuk Google di bawah atau ulangi sesaat lagi.');
      return;
    }
    gis.initialize({
      client_id: googleWebClientId,
      callback: (response) => {
        if (response.credential) {
          void verifyGoogleCredential(response.credential);
        }
      },
    });
    gis.prompt((notification) => {
      if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
        setAuthError('Jendela cepat Google diblokir peramban; silakan gunakan tombol resmi Google di bawah.');
      }
    });
  }, [googleWebClientId, verifyGoogleCredential]);

  const logout = useCallback(() => {
    soundManager.playClick();
    if (activeUserId) {
      safeRemoveKey(`${QUIZ_SESSION_KEY_PREFIX}${activeUserId}`);
    }
    setRoot((prev) => ({ ...prev, activeUserId: null }));
    setSelectedLevel(null);
    setSelectedRegion(null);
    setActiveStory(null);
    setNotFoundPath(null);
    setQuizState(INITIAL_QUIZ_STATE);
    setAuthPassword('');
    setAuthVerificationCode('');
    setActiveSentCode(null);
    setAuthError(null);
    setAuthSuccessMessage(null);
    setAuthMode('login');
    setAppScreen('auth');
    navigateBrowserUrl('/login', false);
  }, [activeUserId]);

  // --- Onboarding & Diagnostic ---
  const completeOnboardingIntro = useCallback(() => {
    soundManager.playClick();
    setAppScreen('questionnaire');
    navigateBrowserUrl('/questionnaire', false);
  }, []);

  const submitQuestionnaireAnswer = useCallback(
    (questionIndex: number, answer: string) => {
      if (!activeUserId) return;
      soundManager.playClick();
      setRoot((prev) => {
        const current = prev.userData[activeUserId];
        if (!current) return prev;
        return {
          ...prev,
          userData: {
            ...prev.userData,
            [activeUserId]: {
              ...current,
              questionnaireAnswers: {
                ...current.questionnaireAnswers,
                [questionIndex]: answer,
              },
            },
          },
        };
      });
    },
    [activeUserId]
  );

  const completeQuestionnaire = useCallback(() => {
    soundManager.playClick();
    setDiagnosticIndex(0);
    setDiagnosticScore(0);
    setDiagnosticFinished(false);
    setAppScreen('diagnostic');
    navigateBrowserUrl('/diagnostic', false);
  }, []);

  const answerDiagnosticQuestion = useCallback(
    (selectedOptionIndex: number) => {
      if (diagnosticIndex >= DIAGNOSTIC_QUESTIONS.length) return;
      const q = DIAGNOSTIC_QUESTIONS[diagnosticIndex];
      const isCorrect = selectedOptionIndex === q.correctIndex;
      const nextScore = diagnosticScore + (isCorrect ? 1 : 0);
      setDiagnosticScore(nextScore);
      if (isCorrect) soundManager.playCorrect();
      else soundManager.playIncorrect();

      if (diagnosticIndex < DIAGNOSTIC_QUESTIONS.length - 1) {
        setDiagnosticIndex((prev) => prev + 1);
      } else {
        const assigned: LearningPath =
          nextScore <= 4 ? 'LOW' : nextScore <= 7 ? 'MIDDLE' : 'HIGH';
        setRecommendedPath(assigned);
        setDiagnosticFinished(true);
      }
    },
    [diagnosticIndex, diagnosticScore]
  );

  const enterAdventureAfterDiagnostic = useCallback(() => {
    if (!activeUserId) return;
    soundManager.playVictoryFanfare();
    const todayIso = getTodayIsoDate();
    setRoot((prev) => {
      const current = prev.userData[activeUserId];
      if (!current) return prev;
      return {
        ...prev,
        userData: {
          ...prev.userData,
          [activeUserId]: {
            ...current,
            profile: {
              ...current.profile,
              initialPath: recommendedPath,
              currentPath: recommendedPath,
              dailyTarget: LEARNING_PATH_META[recommendedPath].targetWordsPerDay,
              diagnosticScore,
              onboardingCompleted: true,
            },
            dailyVocabDate: todayIso,
            dailyVocabAssignments: buildDailyVocabForPath(recommendedPath, todayIso),
          },
        },
      };
    });
    syncGuideFlagsForUser(activeUserId);
    setCurrentTab('map');
    setAppScreen('main');
    navigateBrowserUrl('/map', true);
  }, [activeUserId, diagnosticScore, recommendedPath, syncGuideFlagsForUser]);

  // --- Map, Path & Guide ---
  const switchPath = useCallback(
    (path: LearningPath) => {
      if (!activeUserId || !profile) return;
      if (!canAccessPath(profile.initialPath, path)) return;
      soundManager.playClick();
      const todayIso = getTodayIsoDate();
      setRoot((prev) => {
        const current = prev.userData[activeUserId];
        if (!current) return prev;
        return {
          ...prev,
          userData: {
            ...prev.userData,
            [activeUserId]: {
              ...current,
              profile: {
                ...current.profile,
                currentPath: path,
                dailyTarget: LEARNING_PATH_META[path].targetWordsPerDay,
              },
              dailyVocabDate: todayIso,
              dailyVocabAssignments: buildDailyVocabForPath(path, todayIso),
            },
          },
        };
      });
      setSelectedLevel(null);
    },
    [activeUserId, profile]
  );

  const checkRegionUnlocked = useCallback(
    (stageNumber: number) => isRegionUnlocked(levels, stageNumber),
    [levels]
  );

  const advanceGuide = useCallback(() => {
    soundManager.playClick();
    setGuideStep((prev) => Math.min(prev + 1, 2));
  }, []);

  const completeGuide = useCallback(
    (openFirstLevel: boolean) => {
      if (activeUserId) safeSetBool(`${GUIDE_KEY_PREFIX}${activeUserId}`, true);
      soundManager.playClick();
      setShowFirstTimeGuide(false);
      setGuideStep(0);
      if (openFirstLevel) {
        const available = levels.filter((lvl) => lvl.status === 'AVAILABLE');
        const target = available[available.length - 1] ?? levels[0] ?? null;
        if (target) setSelectedLevel(target);
      }
    },
    [activeUserId, levels]
  );

  const replayGuide = useCallback(() => {
    soundManager.playClick();
    setSelectedLevel(null);
    setSelectedRegion(null);
    setGuideStep(0);
    setShowFirstTimeGuide(true);
    setCurrentTab('map');
    setAppScreen('main');
    navigateBrowserUrl('/map', false);
  }, []);

  const dismissNextStepChip = useCallback(() => {
    if (activeUserId) safeSetBool(`${CHIP_KEY_PREFIX}${activeUserId}`, true);
    setIsNextStepChipDismissed(true);
  }, [activeUserId]);

  // --- Interactive Question State Initialization ---
  const setupInteractiveQuestion = useCallback(
    (q: Question) => {
      setMatchingSelectedEnglish(null);
      setMatchingAnswers({});
      setTypedAnswer('');
      setClueStep(0);
      setScrambledSlots([]);
      setAvailableLetters([]);
      setSentenceBuilderSlots([]);
      setAvailableSentenceChips([]);
      setCategoryBucketAssignments({});
      setSelectedCategoryWord(null);
      setMemoryFlippedIndices([]);
      setMemoryMatchedCards([]);
      setSelectedTrueFalse(null);
      setSelectedGridCoords([]);

      if (q.type === 'WORD_UNSCRAMBLE' || q.type === 'SCRAMBLED_LETTERS') {
        const letters = q.scrambledLetters ?? shuffleWithRng(q.correctAnswer.toUpperCase().split(''));
        setAvailableLetters(letters.map((char, id) => ({ id, char })));
      } else if (q.type === 'SENTENCE_BUILDER') {
        const tokens = q.sentenceTokens ?? shuffleWithRng(q.correctAnswer.split(' '));
        setAvailableSentenceChips(tokens.map((token, id) => ({ id, token })));
      } else if (q.type === 'LISTEN_AND_TYPE') {
        if (isSoundEnabled) {
          speakWord(q.audioPromptWord ?? q.targetWord);
        }
      }
    },
    [isSoundEnabled]
  );

  const launchFreshLevelQuiz = useCallback(
    (lvl: LevelInfo, pushUrl: boolean) => {
      setDailyLimitError(null);
      setSelectedLevel(null);
      setNotFoundPath(null);

      const questions = generateTenQuestionsForLevel(lvl.path, lvl.stageNumber, lvl.levelNumber);
      setQuizState({
        stage: lvl.stageNumber,
        level: lvl.levelNumber,
        path: lvl.path,
        questions,
        currentQuestionIndex: 0,
        selectedOption: null,
        isAnswerChecked: false,
        isAnswerCorrect: false,
        firstAttemptScore: 0,
        failedQuestions: [],
        isQuizFinished: false,
        isRemedialMode: false,
        initialRemedialCount: 0,
        remedialTasks: [],
        currentRemedialIndex: 0,
        remedialSelectedOption: null,
        isRemedialChecked: false,
        isRemedialCorrect: false,
        starsAwarded: 0,
        isLevelCompleted: false,
      });
      if (questions[0]) {
        setupInteractiveQuestion(questions[0]);
      }
      setAppScreen('quiz');
      if (pushUrl) {
        navigateBrowserUrl(`/quiz/${lvl.stageNumber}-${lvl.levelNumber}`, false);
      }
    },
    [setupInteractiveQuestion]
  );

  const startQuizForLevel = useCallback(
    (lvl: LevelInfo) => {
      if (lvl.status === 'LOCKED') return;
      if (lvl.status !== 'COMPLETED' && dailyCompletedCount >= DAILY_LEVEL_LIMIT) {
        setDailyLimitError(
          'Kamu sudah menyelesaikan batas 3 level baru hari ini! Kembalilah besok untuk melanjutkan petualangan.'
        );
        return;
      }

      soundManager.playClick();
      if (activeUserId) {
        safeRemoveKey(`${QUIZ_SESSION_KEY_PREFIX}${activeUserId}`);
      }
      launchFreshLevelQuiz(lvl, true);
    },
    [activeUserId, dailyCompletedCount, launchFreshLevelQuiz]
  );

  // Interactive Question Controls
  const selectMatchingEnglish = useCallback(
    (eng: string) => {
      setMatchingSelectedEnglish(eng);
      speakWord(eng);
      soundManager.playTileTap();
    },
    []
  );

  const pairMatchingIndonesian = useCallback(
    (indo: string) => {
      if (!matchingSelectedEnglish) return;
      const eng = matchingSelectedEnglish;
      setMatchingAnswers((prev) => ({ ...prev, [eng]: indo }));
      setMatchingSelectedEnglish(null);
      soundManager.playMatchPair();
    },
    [matchingSelectedEnglish]
  );

  const assignMatchingDirect = useCallback((eng: string, indo: string) => {
    setMatchingAnswers((prev) => ({ ...prev, [eng]: indo }));
    setMatchingSelectedEnglish(null);
    soundManager.playMatchPair();
  }, []);

  const unpairMatching = useCallback((eng: string) => {
    setMatchingAnswers((prev) => {
      const next = { ...prev };
      delete next[eng];
      return next;
    });
    soundManager.playTileTap();
  }, []);

  const advanceClueStep = useCallback(() => {
    soundManager.playClick();
    setClueStep((prev) => (prev === 0 ? 1 : prev === 1 ? 2 : 0));
  }, []);

  const pickScrambledLetter = useCallback((id: number, char: string) => {
    setAvailableLetters((prev) => prev.filter((item) => item.id !== id));
    setScrambledSlots((prev) => [...prev, char]);
    soundManager.playTileTap();
  }, []);

  const removeScrambledLetter = useCallback((slotIndex: number) => {
    setScrambledSlots((prev) => {
      const removed = prev[slotIndex];
      if (!removed) return prev;
      setAvailableLetters((avail) => [...avail, { id: Date.now() + Math.floor(Math.random() * 10000), char: removed }]);
      return prev.filter((_, i) => i !== slotIndex);
    });
    soundManager.playTileTap();
  }, []);

  const resetScrambledLetters = useCallback(() => {
    const currentQ = quizState.questions[quizState.currentQuestionIndex];
    if (!currentQ) return;
    const letters = currentQ.scrambledLetters ?? currentQ.correctAnswer.toUpperCase().split('');
    setScrambledSlots([]);
    setAvailableLetters(letters.map((char, id) => ({ id, char })));
    soundManager.playClick();
  }, [quizState.currentQuestionIndex, quizState.questions]);

  const pickSentenceChip = useCallback((id: number, token: string) => {
    setAvailableSentenceChips((prev) => prev.filter((item) => item.id !== id));
    setSentenceBuilderSlots((prev) => [...prev, token]);
    soundManager.playTileTap();
  }, []);

  const removeSentenceChip = useCallback((slotIndex: number) => {
    setSentenceBuilderSlots((prev) => {
      const removed = prev[slotIndex];
      if (!removed) return prev;
      setAvailableSentenceChips((avail) => [
        ...avail,
        { id: Date.now() + Math.floor(Math.random() * 10000), token: removed },
      ]);
      return prev.filter((_, i) => i !== slotIndex);
    });
    soundManager.playTileTap();
  }, []);

  const resetSentenceBuilder = useCallback(() => {
    const currentQ = quizState.questions[quizState.currentQuestionIndex];
    if (!currentQ) return;
    const tokens = currentQ.sentenceTokens ?? currentQ.correctAnswer.split(' ');
    setSentenceBuilderSlots([]);
    setAvailableSentenceChips(tokens.map((token, id) => ({ id, token })));
    soundManager.playClick();
  }, [quizState.currentQuestionIndex, quizState.questions]);

  const selectCategoryWord = useCallback((word: string) => {
    setSelectedCategoryWord((prev) => (prev === word ? null : word));
    soundManager.playTileTap();
  }, []);

  const assignCategoryWord = useCallback((word: string, bucket: string) => {
    setCategoryBucketAssignments((prev) => ({ ...prev, [word]: bucket }));
    setSelectedCategoryWord(null);
    soundManager.playMatchPair();
  }, []);

  const unassignCategoryWord = useCallback((word: string) => {
    setCategoryBucketAssignments((prev) => {
      const next = { ...prev };
      delete next[word];
      return next;
    });
    soundManager.playTileTap();
  }, []);

  const flipMemoryCard = useCallback(
    (cardIndex: number, cardKey: string, pairs: Record<string, string>) => {
      if (memoryFlippedIndices.includes(cardIndex) || memoryMatchedCards.includes(cardKey)) return;
      soundManager.playTileTap();

      const baseFlipped = memoryFlippedIndices.length === 2 ? [] : [...memoryFlippedIndices];
      const nextFlipped = [...baseFlipped, cardIndex];
      setMemoryFlippedIndices(nextFlipped);

      if (nextFlipped.length === 2) {
        const allCards = Object.entries(pairs)
          .flatMap(([k, val]) => [k, val])
          .sort();
        const card1 = allCards[nextFlipped[0]] ?? '';
        const card2 = allCards[nextFlipped[1]] ?? '';
        const isMatch = pairs[card1] === card2 || pairs[card2] === card1;
        if (isMatch) {
          setMemoryMatchedCards((prev) => [...prev, card1, card2]);
          soundManager.playMatchPair();
        }
      }
    },
    [memoryFlippedIndices, memoryMatchedCards]
  );

  const selectTrueFalse = useCallback(
    (value: boolean) => {
      if (!quizState.isAnswerChecked) {
        setSelectedTrueFalse(value);
        soundManager.playClick();
      }
    },
    [quizState.isAnswerChecked]
  );

  const toggleGridCoord = useCallback(
    (row: number, col: number) => {
      if (quizState.isAnswerChecked) return;
      setSelectedGridCoords((prev) => {
        const exists = prev.some(([r, c]) => r === row && c === col);
        return exists ? prev.filter(([r, c]) => !(r === row && c === col)) : [...prev, [row, col]];
      });
      soundManager.playTileTap();
    },
    [quizState.isAnswerChecked]
  );

  const clearGridCoords = useCallback(() => {
    setSelectedGridCoords([]);
    soundManager.playClick();
  }, []);

  const selectQuizOption = useCallback(
    (option: string) => {
      if (quizState.isAnswerChecked) return;
      soundManager.playClick();
      setQuizState((prev) => ({ ...prev, selectedOption: option }));
    },
    [quizState.isAnswerChecked]
  );

  // Persist Daily Vocab Mastery Status
  const updateDailyVocabWordStatus = useCallback(
    (wordLower: string, status: DailyVocabAssignment['status']) => {
      if (!activeUserId) return;
      setRoot((prev) => {
        const current = prev.userData[activeUserId];
        if (!current) return prev;
        return {
          ...prev,
          userData: {
            ...prev.userData,
            [activeUserId]: {
              ...current,
              dailyVocabAssignments: current.dailyVocabAssignments.map((item) =>
                item.word === wordLower ? { ...item, status } : item
              ),
            },
          },
        };
      });
    },
    [activeUserId]
  );

  const persistLevelCompletion = useCallback(
    (path: LearningPath, stage: number, levelNum: number, score: number, stars: number) => {
      if (!activeUserId) return;
      const levelId = `${stage}-${levelNum}`;
      const nextNum = levelNum < 10 ? levelNum + 1 : 1;
      const nextStage = levelNum < 10 ? stage : stage + 1;
      const nextId = nextStage <= 5 ? `${nextStage}-${nextNum}` : null;
      const todayIso = getTodayIsoDate();

      setRoot((prev) => {
        const current = prev.userData[activeUserId];
        if (!current) return prev;

        const pathLevels = current.levelsByPath[path] ?? buildInitialLevels(path);
        const targetExisting = pathLevels.find((l) => l.id === levelId);
        const wasAlreadyCompleted = targetExisting?.status === 'COMPLETED';

        const updatedLevels = pathLevels.map((l) => {
          if (l.id === levelId) {
            return {
              ...l,
              status: 'COMPLETED' as const,
              stars: Math.max(stars, l.stars),
              bestScore: Math.max(score, l.bestScore),
            };
          }
          if (nextId && l.id === nextId && l.status === 'LOCKED') {
            return { ...l, status: 'AVAILABLE' as const };
          }
          return l;
        });

        // Daily Limit counter increments ONLY on newly completed levels
        const todayList = current.dailyCompletedByDate[todayIso] ?? [];
        const levelKey = `${path}:${levelId}`;
        const nextDailyList =
          !wasAlreadyCompleted && !todayList.includes(levelKey)
            ? [...todayList, levelKey]
            : todayList;

        // Streak & XP update
        let nextStreak = current.profile.currentStreak;
        if (current.profile.lastStudyDate !== todayIso) {
          const lastMs = Date.parse(current.profile.lastStudyDate);
          const todayMs = Date.parse(todayIso);
          const diffDays = Number.isFinite(lastMs) ? Math.round((todayMs - lastMs) / 86400000) : 2;
          nextStreak = diffDays === 1 ? current.profile.currentStreak + 1 : 1;
        } else if (nextStreak === 0) {
          nextStreak = 1;
        }
        const earnedXp = score * 10 + stars * 15;

        return {
          ...prev,
          userData: {
            ...prev.userData,
            [activeUserId]: {
              ...current,
              profile: {
                ...current.profile,
                currentStreak: nextStreak,
                bestStreak: Math.max(current.profile.bestStreak, nextStreak),
                xp: current.profile.xp + earnedXp,
                lastStudyDate: todayIso,
              },
              levelsByPath: {
                ...current.levelsByPath,
                [path]: updatedLevels,
              },
              dailyCompletedByDate: {
                ...current.dailyCompletedByDate,
                [todayIso]: nextDailyList,
              },
            },
          },
        };
      });
    },
    [activeUserId]
  );

  const checkQuizAnswer = useCallback(() => {
    if (quizState.isAnswerChecked) return;
    const currentQ = quizState.questions[quizState.currentQuestionIndex];
    if (!currentQ) return;

    let isCorrect = false;
    switch (currentQ.type) {
      case 'DRAG_AND_MATCH': {
        const pairs = currentQ.matchingPairs ?? {};
        const entries = Object.entries(pairs);
        isCorrect =
          entries.length > 0 &&
          entries.every(
            ([eng, indo]) =>
              (matchingAnswers[eng] ?? '').toLowerCase() === indo.toLowerCase()
          );
        break;
      }
      case 'FILL_BLANK':
      case 'LISTEN_AND_TYPE': {
        isCorrect =
          typedAnswer.trim().toLowerCase() === currentQ.correctAnswer.trim().toLowerCase();
        break;
      }
      case 'WORD_UNSCRAMBLE':
      case 'SCRAMBLED_LETTERS': {
        isCorrect =
          scrambledSlots.join('').trim().toLowerCase() ===
          currentQ.correctAnswer.trim().toLowerCase();
        break;
      }
      case 'SENTENCE_BUILDER': {
        const built = sentenceBuilderSlots.join(' ').trim().toLowerCase();
        const expected = (currentQ.orderedSentence ?? currentQ.correctAnswer).trim().toLowerCase();
        isCorrect = built === expected;
        break;
      }
      case 'CATEGORY_SORT': {
        const buckets = currentQ.categoryBuckets ?? {};
        const entries = Object.entries(buckets);
        isCorrect =
          entries.length > 0 &&
          entries.every(([bucketName, expectedWords]) =>
            expectedWords.every((word) => categoryBucketAssignments[word] === bucketName)
          );
        break;
      }
      case 'MEMORY_FLIP': {
        const pairs = currentQ.matchingPairs ?? {};
        const count = Object.keys(pairs).length;
        isCorrect = count > 0 && memoryMatchedCards.length >= count * 2;
        break;
      }
      case 'TRUE_OR_FALSE': {
        isCorrect = selectedTrueFalse === currentQ.isStatementTrue;
        break;
      }
      case 'WORD_HUNT': {
        const grid = currentQ.gridLetters ?? [];
        const tappedChars = selectedGridCoords
          .map(([r, c]) => grid[r]?.[c] ?? '')
          .join('');
        const spellsTarget =
          tappedChars.toUpperCase() === currentQ.correctAnswer.toUpperCase();
        let isContiguousLine = false;
        if (selectedGridCoords.length === 1) {
          isContiguousLine = true;
        } else if (selectedGridCoords.length > 1) {
          const dr = selectedGridCoords[1][0] - selectedGridCoords[0][0];
          const dc = selectedGridCoords[1][1] - selectedGridCoords[0][1];
          const validStep =
            Math.abs(dr) <= 1 && Math.abs(dc) <= 1 && (dr !== 0 || dc !== 0);
          isContiguousLine =
            validStep &&
            selectedGridCoords.every((coord, idx) =>
              idx === 0
                ? true
                : coord[0] - selectedGridCoords[idx - 1][0] === dr &&
                  coord[1] - selectedGridCoords[idx - 1][1] === dc
            );
        }
        isCorrect = spellsTarget && isContiguousLine;
        break;
      }
      default: {
        isCorrect = quizState.selectedOption === currentQ.correctAnswer;
      }
    }

    if (isCorrect) {
      soundManager.playCorrect();
      updateDailyVocabWordStatus(currentQ.targetWord.toLowerCase(), 'MASTERED');
    } else {
      soundManager.playIncorrect();
      updateDailyVocabWordStatus(currentQ.targetWord.toLowerCase(), 'NEEDS_REVIEW');
    }

    let updatedQ = currentQ;
    if (!isCorrect && currentQ.type === 'CATEGORY_SORT') {
      const buckets = currentQ.categoryBuckets ?? {};
      const misplaced: string[] = [];
      Object.entries(buckets).forEach(([bucketName, expectedWords]) => {
        expectedWords.forEach((word) => {
          const assigned = categoryBucketAssignments[word];
          if (assigned !== bucketName) {
            misplaced.push(
              assigned
                ? `'${word}' seharusnya di '${bucketName}', bukan '${assigned}'`
                : `'${word}' belum ditempatkan ke '${bucketName}'`
            );
          }
        });
      });
      if (misplaced.length > 0) {
        updatedQ = {
          ...currentQ,
          failureFeedback: `Pengelompokan belum tepat: ${misplaced.join('; ')}.`,
        };
      }
    }

    setQuizState((prev) => {
      const updatedQuestions =
        updatedQ !== currentQ
          ? prev.questions.map((item, idx) =>
              idx === prev.currentQuestionIndex ? updatedQ : item
            )
          : prev.questions;
      return {
        ...prev,
        questions: updatedQuestions,
        isAnswerChecked: true,
        isAnswerCorrect: isCorrect,
        firstAttemptScore: isCorrect ? prev.firstAttemptScore + 1 : prev.firstAttemptScore,
        failedQuestions: isCorrect ? prev.failedQuestions : [...prev.failedQuestions, updatedQ],
      };
    });
  }, [
    categoryBucketAssignments,
    matchingAnswers,
    memoryMatchedCards.length,
    quizState.currentQuestionIndex,
    quizState.isAnswerChecked,
    quizState.questions,
    quizState.selectedOption,
    scrambledSlots,
    selectedGridCoords,
    selectedTrueFalse,
    sentenceBuilderSlots,
    typedAnswer,
    updateDailyVocabWordStatus,
  ]);

  const nextQuizQuestion = useCallback(() => {
    if (quizState.currentQuestionIndex < quizState.questions.length - 1) {
      const nextIdx = quizState.currentQuestionIndex + 1;
      setQuizState((prev) => ({
        ...prev,
        currentQuestionIndex: nextIdx,
        selectedOption: null,
        isAnswerChecked: false,
        isAnswerCorrect: false,
      }));
      const nextQ = quizState.questions[nextIdx];
      if (nextQ) setupInteractiveQuestion(nextQ);
    } else {
      // Finalize 10-question attempt
      const failed = quizState.failedQuestions;
      const stars = computeStarsForScore(quizState.firstAttemptScore);
      if (failed.length === 0) {
        soundManager.playVictoryFanfare();
        persistLevelCompletion(
          quizState.path,
          quizState.stage,
          quizState.level,
          quizState.firstAttemptScore,
          3
        );
        setQuizState((prev) => ({
          ...prev,
          isQuizFinished: true,
          isRemedialMode: false,
          starsAwarded: 3,
          isLevelCompleted: true,
        }));
      } else {
        const remedialTasks: RemedialTask[] = failed.map((fq) => ({
          originalQuestionId: fq.id,
          targetWord: fq.targetWord,
          question: generateRemedialQuestion(fq),
          isResolved: false,
        }));
        setQuizState((prev) => ({
          ...prev,
          isQuizFinished: true,
          isRemedialMode: true,
          initialRemedialCount: remedialTasks.length,
          remedialTasks,
          currentRemedialIndex: 0,
          remedialSelectedOption: null,
          isRemedialChecked: false,
          isRemedialCorrect: false,
          starsAwarded: stars,
          isLevelCompleted: false,
        }));
      }
    }
  }, [
    persistLevelCompletion,
    quizState.currentQuestionIndex,
    quizState.failedQuestions,
    quizState.firstAttemptScore,
    quizState.level,
    quizState.path,
    quizState.questions,
    quizState.stage,
    setupInteractiveQuestion,
  ]);

  const selectRemedialOption = useCallback((option: string) => {
    setQuizState((prev) =>
      prev.isRemedialChecked ? prev : { ...prev, remedialSelectedOption: option }
    );
    soundManager.playClick();
  }, []);

  const checkRemedialAnswer = useCallback(() => {
    const task = quizState.remedialTasks[quizState.currentRemedialIndex];
    if (!task || quizState.isRemedialChecked) return;
    const isCorrect = quizState.remedialSelectedOption === task.question.correctAnswer;
    if (isCorrect) {
      soundManager.playCorrect();
      const updatedTasks = quizState.remedialTasks.map((t, i) =>
        i === quizState.currentRemedialIndex ? { ...t, isResolved: true } : t
      );
      const allResolved = updatedTasks.every((t) => t.isResolved);
      if (allResolved) {
        soundManager.playVictoryFanfare();
        persistLevelCompletion(
          quizState.path,
          quizState.stage,
          quizState.level,
          quizState.firstAttemptScore,
          quizState.starsAwarded
        );
      }
      setQuizState((prev) => ({
        ...prev,
        remedialTasks: updatedTasks,
        isRemedialChecked: true,
        isRemedialCorrect: true,
        isLevelCompleted: allResolved,
      }));
    } else {
      soundManager.playIncorrect();
      setQuizState((prev) => ({
        ...prev,
        isRemedialChecked: true,
        isRemedialCorrect: false,
      }));
    }
  }, [
    persistLevelCompletion,
    quizState.currentRemedialIndex,
    quizState.firstAttemptScore,
    quizState.isRemedialChecked,
    quizState.level,
    quizState.path,
    quizState.remedialSelectedOption,
    quizState.remedialTasks,
    quizState.stage,
    quizState.starsAwarded,
  ]);

  const retryRemedialTask = useCallback(() => {
    soundManager.playClick();
    setQuizState((prev) => ({
      ...prev,
      remedialSelectedOption: null,
      isRemedialChecked: false,
      isRemedialCorrect: false,
    }));
  }, []);

  const nextRemedialTask = useCallback(() => {
    soundManager.playClick();
    setQuizState((prev) => {
      if (prev.currentRemedialIndex < prev.remedialTasks.length - 1) {
        return {
          ...prev,
          currentRemedialIndex: prev.currentRemedialIndex + 1,
          remedialSelectedOption: null,
          isRemedialChecked: false,
          isRemedialCorrect: false,
        };
      }
      return { ...prev, isLevelCompleted: true };
    });
  }, []);

  const exitQuizToMap = useCallback(() => {
    soundManager.playClick();
    if (activeUserId) {
      safeRemoveKey(`${QUIZ_SESSION_KEY_PREFIX}${activeUserId}`);
    }
    setNotFoundPath(null);
    setCurrentTab('map');
    setAppScreen('main');
    navigateBrowserUrl('/map', false);
  }, [activeUserId]);

  // --- Lexicon, Lore, Beasts, Settings ---
  const toggleLearnedWord = useCallback(
    (vocabId: string) => {
      if (!activeUserId) return;
      soundManager.playTileTap();
      setRoot((prev) => {
        const current = prev.userData[activeUserId];
        if (!current) return prev;
        const exists = current.learnedVocabIds.includes(vocabId);
        const nextLearned = exists
          ? current.learnedVocabIds.filter((id) => id !== vocabId)
          : [...current.learnedVocabIds, vocabId];
        return {
          ...prev,
          userData: {
            ...prev.userData,
            [activeUserId]: {
              ...current,
              learnedVocabIds: nextLearned,
            },
          },
        };
      });
    },
    [activeUserId]
  );

  const completeStory = useCallback(
    (storyId: string) => {
      if (!activeUserId) return;
      soundManager.playCorrect();
      setRoot((prev) => {
        const current = prev.userData[activeUserId];
        if (!current) return prev;
        const nextCompletions = current.storyCompletions.includes(storyId)
          ? current.storyCompletions
          : [...current.storyCompletions, storyId];
        const xpGain = current.storyCompletions.includes(storyId) ? 0 : 25;
        return {
          ...prev,
          userData: {
            ...prev.userData,
            [activeUserId]: {
              ...current,
              profile: {
                ...current.profile,
                xp: current.profile.xp + xpGain,
              },
              storyCompletions: nextCompletions,
            },
          },
        };
      });
      setActiveStory(null);
      navigateBrowserUrl('/lore', true);
    },
    [activeUserId]
  );

  const equipCompanion = useCallback(
    (companionId: string) => {
      if (!activeUserId) return;
      soundManager.playMatchPair();
      setRoot((prev) => {
        const current = prev.userData[activeUserId];
        if (!current) return prev;
        return {
          ...prev,
          userData: {
            ...prev.userData,
            [activeUserId]: {
              ...current,
              profile: {
                ...current.profile,
                activeCompanionId: companionId,
              },
            },
          },
        };
      });
    },
    [activeUserId]
  );

  // --- Persist Active Mid-Quiz Session (Survives Refresh / Deep Link) ---
  useEffect(() => {
    if (!activeUserId) return;
    if (appScreen === 'quiz' && quizState.questions.length > 0) {
      const snapshot: PersistedQuizSession = {
        quizState,
        matchingSelectedEnglish,
        matchingAnswers,
        typedAnswer,
        clueStep,
        scrambledSlots,
        availableLetters,
        sentenceBuilderSlots,
        availableSentenceChips,
        categoryBucketAssignments,
        selectedCategoryWord,
        memoryFlippedIndices,
        memoryMatchedCards,
        selectedTrueFalse,
        selectedGridCoords,
      };
      safeWriteJson(`${QUIZ_SESSION_KEY_PREFIX}${activeUserId}`, snapshot);
    }
  }, [
    activeUserId,
    appScreen,
    quizState,
    matchingSelectedEnglish,
    matchingAnswers,
    typedAnswer,
    clueStep,
    scrambledSlots,
    availableLetters,
    sentenceBuilderSlots,
    availableSentenceChips,
    categoryBucketAssignments,
    selectedCategoryWord,
    memoryFlippedIndices,
    memoryMatchedCards,
    selectedTrueFalse,
    selectedGridCoords,
  ]);

  const applySavedQuizSnapshot = useCallback((snapshot: PersistedQuizSession) => {
    setQuizState(snapshot.quizState);
    setMatchingSelectedEnglish(snapshot.matchingSelectedEnglish ?? null);
    setMatchingAnswers(snapshot.matchingAnswers ?? {});
    setTypedAnswer(snapshot.typedAnswer ?? '');
    setClueStep(snapshot.clueStep ?? 0);
    setScrambledSlots(snapshot.scrambledSlots ?? []);
    setAvailableLetters(snapshot.availableLetters ?? []);
    setSentenceBuilderSlots(snapshot.sentenceBuilderSlots ?? []);
    setAvailableSentenceChips(snapshot.availableSentenceChips ?? []);
    setCategoryBucketAssignments(snapshot.categoryBucketAssignments ?? {});
    setSelectedCategoryWord(snapshot.selectedCategoryWord ?? null);
    setMemoryFlippedIndices(snapshot.memoryFlippedIndices ?? []);
    setMemoryMatchedCards(snapshot.memoryMatchedCards ?? []);
    setSelectedTrueFalse(snapshot.selectedTrueFalse ?? null);
    setSelectedGridCoords(snapshot.selectedGridCoords ?? []);
  }, []);

  const syncWithBrowserLocation = useCallback(
    (allowReplaceRedirect: boolean) => {
      if (typeof window === 'undefined') return;
      const parsed = parseUrlToRoute(window.location.pathname, window.location.search);

      if (parsed.kind === 'not_found') {
        setNotFoundPath(parsed.path);
        return;
      }
      setNotFoundPath(null);

      // Guard 1: Unauthenticated user
      if (!activeUserId || !activeUserData) {
        setAppScreen('auth');
        if (parsed.kind === 'auth') {
          if (parsed.mode === 'verify' && !activeVerificationEmail) {
            setAuthMode('login');
            if (allowReplaceRedirect) navigateBrowserUrl('/login', true);
          } else {
            setAuthMode(parsed.mode);
          }
        } else if (allowReplaceRedirect) {
          navigateBrowserUrl('/login', true);
        }
        return;
      }

      // Guard 2: Authenticated user who has not completed onboarding/diagnostic
      if (!activeUserData.profile.onboardingCompleted) {
        if (
          parsed.kind === 'onboarding' ||
          parsed.kind === 'questionnaire' ||
          parsed.kind === 'diagnostic'
        ) {
          setAppScreen(parsed.kind);
        } else {
          setAppScreen('onboarding');
          if (allowReplaceRedirect) navigateBrowserUrl('/onboarding', true);
        }
        return;
      }

      // Guard 3: Fully onboarded user
      if (parsed.kind === 'tab') {
        setCurrentTab(parsed.tab);
        setAppScreen('main');
        if (parsed.tab === 'lore') {
          if (parsed.storyId) {
            const matchedStory = ADVENTURE_STORIES.find((s) => s.id === parsed.storyId) ?? null;
            setActiveStory(matchedStory);
            if (!matchedStory && allowReplaceRedirect) {
              navigateBrowserUrl('/lore', true);
            }
          } else {
            setActiveStory(null);
          }
        } else {
          setActiveStory(null);
        }
        return;
      }

      if (parsed.kind === 'quiz') {
        const sessionKey = `${QUIZ_SESSION_KEY_PREFIX}${activeUserId}`;
        const saved = safeReadJson<PersistedQuizSession | null>(sessionKey, null);
        if (
          saved &&
          saved.quizState.questions.length > 0 &&
          saved.quizState.path === activeUserData.profile.currentPath &&
          `${saved.quizState.stage}-${saved.quizState.level}` === parsed.levelId
        ) {
          applySavedQuizSnapshot(saved);
          setAppScreen('quiz');
          return;
        }

        const pathLevels = activeUserData.levelsByPath[activeUserData.profile.currentPath] ?? [];
        const targetLevel = pathLevels.find((l) => l.id === parsed.levelId);
        if (!targetLevel || targetLevel.status === 'LOCKED') {
          setCurrentTab('map');
          setAppScreen('main');
          setDailyLimitError(
            `Pos Level ${parsed.levelId} masih terkunci. Selesaikan pos sebelumnya terlebih dahulu.`
          );
          navigateBrowserUrl('/map', true);
          return;
        }

        const todayList = activeUserData.dailyCompletedByDate[getTodayIsoDate()] ?? [];
        if (targetLevel.status !== 'COMPLETED' && todayList.length >= DAILY_LEVEL_LIMIT) {
          setCurrentTab('map');
          setAppScreen('main');
          setDailyLimitError(
            'Kamu sudah menyelesaikan batas 3 level baru hari ini! Kembalilah besok untuk melanjutkan petualangan.'
          );
          navigateBrowserUrl('/map', true);
          return;
        }

        launchFreshLevelQuiz(targetLevel, false);
        return;
      }

      // Default landing for root/auth/onboarding URLs when already onboarded
      setCurrentTab('map');
      setAppScreen('main');
      setActiveStory(null);
      if (allowReplaceRedirect) {
        navigateBrowserUrl('/map', true);
      }
    },
    [
      activeUserData,
      activeUserId,
      activeVerificationEmail,
      applySavedQuizSnapshot,
      launchFreshLevelQuiz,
    ]
  );

  // Sync initial URL on load (including mid-quiz deep link recovery)
  useEffect(() => {
    syncWithBrowserLocation(true);
    // Intentionally runs on mount and whenever active account changes
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeUserId]);

  // Listen to browser Back / Forward buttons
  useEffect(() => {
    const handlePopState = () => {
      syncWithBrowserLocation(false);
    };
    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, [syncWithBrowserLocation]);

  // Keep browser document.title synced with current route
  useEffect(() => {
    let route: AppRoute;
    if (notFoundPath) {
      route = { kind: 'not_found', path: notFoundPath };
    } else if (appScreen === 'auth' || !profile) {
      route = { kind: 'auth', mode: authMode };
    } else if (appScreen === 'onboarding') {
      route = { kind: 'onboarding' };
    } else if (appScreen === 'questionnaire') {
      route = { kind: 'questionnaire' };
    } else if (appScreen === 'diagnostic') {
      route = { kind: 'diagnostic' };
    } else if (appScreen === 'quiz') {
      route = { kind: 'quiz', levelId: `${quizState.stage}-${quizState.level}` };
    } else {
      route = {
        kind: 'tab',
        tab: currentTab,
        storyId: currentTab === 'lore' && activeStory ? activeStory.id : undefined,
      };
    }
    document.title = getDocumentTitleForRoute(route);
  }, [
    activeStory,
    appScreen,
    authMode,
    currentTab,
    notFoundPath,
    profile,
    quizState.level,
    quizState.stage,
  ]);

  const recoverFromNotFound = useCallback(() => {
    soundManager.playClick();
    setNotFoundPath(null);
    if (!activeUserId || !activeUserData) {
      setAuthMode('login');
      setAppScreen('auth');
      navigateBrowserUrl('/login', false);
    } else if (!activeUserData.profile.onboardingCompleted) {
      setAppScreen('onboarding');
      navigateBrowserUrl('/onboarding', false);
    } else {
      setCurrentTab('map');
      setAppScreen('main');
      navigateBrowserUrl('/map', false);
    }
  }, [activeUserData, activeUserId]);

  const toggleSound = useCallback(() => {
    setIsSoundEnabled((prev) => {
      const next = !prev;
      safeSetBool(SOUND_KEY, next);
      soundManager.setEnabled(next);
      return next;
    });
  }, []);

  const toggleHighContrast = useCallback(() => {
    setHighContrast((prev) => {
      const next = !prev;
      safeSetBool(HIGH_CONTRAST_KEY, next);
      return next;
    });
  }, []);

  const toggleReducedMotion = useCallback(() => {
    setReducedMotion((prev) => {
      const next = !prev;
      safeSetBool(REDUCED_MOTION_KEY, next);
      return next;
    });
  }, []);

  const dismissStorageWarning = useCallback(() => {
    setStorageWarning(null);
  }, []);

  // Multi-tab localStorage synchronization
  useEffect(() => {
    if (typeof window === 'undefined') return undefined;
    const handleStorageChange = (event: StorageEvent) => {
      if (event.storageArea !== window.localStorage) return;
      if (event.key === STORAGE_KEY && event.newValue) {
        try {
          const parsed = JSON.parse(event.newValue) as PersistedRoot;
          setRoot({
            activeUserId: parsed.activeUserId ?? null,
            accounts: parsed.accounts ?? {},
            userData: parsed.userData ?? {},
          });
        } catch {
          // Ignore malformed external tab write
        }
      } else if (event.key === SOUND_KEY && event.newValue !== null) {
        const next = event.newValue === 'true';
        setIsSoundEnabled(next);
        soundManager.setEnabled(next);
      } else if (event.key === HIGH_CONTRAST_KEY && event.newValue !== null) {
        setHighContrast(event.newValue === 'true');
      } else if (event.key === REDUCED_MOTION_KEY && event.newValue !== null) {
        setReducedMotion(event.newValue === 'true');
      }
    };
    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  const exportProgressBackup = useCallback((): string | null => {
    if (!activeUserId || !activeUserData) return null;
    return JSON.stringify(
      {
        schema: 'word-explorer-backup-v1',
        exportedAt: new Date().toISOString(),
        userData: activeUserData,
      },
      null,
      2
    );
  }, [activeUserData, activeUserId]);

  const importProgressBackup = useCallback(
    (rawJson: string): { ok: boolean; message: string } => {
      if (!activeUserId || !activeUserData) {
        return { ok: false, message: 'Silakan masuk ke akun terlebih dahulu sebelum memulihkan cadangan.' };
      }
      let parsed: unknown;
      try {
        parsed = JSON.parse(rawJson);
      } catch {
        return { ok: false, message: 'File tidak valid: format JSON rusak atau tidak dapat dibaca.' };
      }
      if (!parsed || typeof parsed !== 'object') {
        return { ok: false, message: 'Struktur cadangan tidak sesuai.' };
      }
      const record = parsed as Record<string, unknown>;
      if (record.schema !== 'word-explorer-backup-v1' || !record.userData || typeof record.userData !== 'object') {
        return { ok: false, message: 'File bukan cadangan resmi Word Explorer (v1).' };
      }
      const incoming = record.userData as Partial<PersistedUserData>;
      if (
        !incoming.profile ||
        !incoming.levelsByPath ||
        !Array.isArray(incoming.learnedVocabIds) ||
        !Array.isArray(incoming.storyCompletions)
      ) {
        return { ok: false, message: 'Data progres di dalam file tidak lengkap.' };
      }
      const incomingLevels = incoming.levelsByPath as Partial<Record<LearningPath, LevelInfo[]>>;
      if (
        !Array.isArray(incomingLevels.LOW) ||
        !Array.isArray(incomingLevels.MIDDLE) ||
        !Array.isArray(incomingLevels.HIGH)
      ) {
        return { ok: false, message: 'Data jalur level (LOW/MIDDLE/HIGH) pada file cadangan tidak valid.' };
      }

      setRoot((prev) => {
        const current = prev.userData[activeUserId];
        if (!current) return prev;
        const mergedProfile: UserProfile = {
          ...current.profile,
          xp: typeof incoming.profile?.xp === 'number' ? incoming.profile.xp : current.profile.xp,
          currentStreak:
            typeof incoming.profile?.currentStreak === 'number'
              ? incoming.profile.currentStreak
              : current.profile.currentStreak,
          bestStreak:
            typeof incoming.profile?.bestStreak === 'number'
              ? incoming.profile.bestStreak
              : current.profile.bestStreak,
          lastStudyDate:
            typeof incoming.profile?.lastStudyDate === 'string'
              ? incoming.profile.lastStudyDate
              : current.profile.lastStudyDate,
          currentPath: incoming.profile?.currentPath ?? current.profile.currentPath,
          initialPath: incoming.profile?.initialPath ?? current.profile.initialPath,
          onboardingCompleted:
            typeof incoming.profile?.onboardingCompleted === 'boolean'
              ? incoming.profile.onboardingCompleted
              : current.profile.onboardingCompleted,
          activeCompanionId:
            typeof incoming.profile?.activeCompanionId === 'string'
              ? incoming.profile.activeCompanionId
              : current.profile.activeCompanionId,
        };
        return {
          ...prev,
          userData: {
            ...prev.userData,
            [activeUserId]: {
              profile: mergedProfile,
              levelsByPath: {
                LOW: incomingLevels.LOW ?? current.levelsByPath.LOW,
                MIDDLE: incomingLevels.MIDDLE ?? current.levelsByPath.MIDDLE,
                HIGH: incomingLevels.HIGH ?? current.levelsByPath.HIGH,
              },
              dailyCompletedByDate: incoming.dailyCompletedByDate ?? current.dailyCompletedByDate,
              dailyVocabDate: incoming.dailyVocabDate ?? current.dailyVocabDate,
              dailyVocabAssignments: incoming.dailyVocabAssignments ?? current.dailyVocabAssignments,
              learnedVocabIds: incoming.learnedVocabIds ?? current.learnedVocabIds,
              storyCompletions: incoming.storyCompletions ?? current.storyCompletions,
              questionnaireAnswers: incoming.questionnaireAnswers ?? current.questionnaireAnswers,
            },
          },
        };
      });
      return { ok: true, message: 'Progres belajar berhasil dipulihkan dari file cadangan.' };
    },
    [activeUserData, activeUserId]
  );

  return {
    appScreen,
    setAppScreen,
    notFoundPath,
    recoverFromNotFound,
    profile,
    currentTab,
    setCurrentTab: navigateToTab,
    selectedPath,
    switchPath,
    levels,
    dailyCompletedCount,
    dailyLimitError,
    setDailyLimitError,
    dailyVocabAssignments,
    timeUntilMidnightReset,
    selectedLevel,
    setSelectedLevel,
    selectedRegion,
    setSelectedRegion,
    isSoundEnabled,
    toggleSound,
    highContrast,
    toggleHighContrast,
    reducedMotion,
    toggleReducedMotion,
    showFirstTimeGuide,
    guideStep,
    advanceGuide,
    completeGuide,
    replayGuide,
    isNextStepChipDismissed,
    dismissNextStepChip,
    checkRegionUnlocked,
    // Auth
    authMode,
    setAuthMode: selectAuthMode,
    authEmail,
    setAuthEmail,
    authPassword,
    setAuthPassword,
    authDisplayName,
    setAuthDisplayName,
    authVerificationCode,
    setAuthVerificationCode,
    activeVerificationEmail,
    activeSentCode,
    resendCooldownSeconds,
    authError,
    setAuthError,
    authSuccessMessage,
    setAuthSuccessMessage,
    isAuthLoading,
    backendStatus,
    googleWebClientId,
    verifyGoogleCredential,
    showBackendStatusDialog,
    setShowBackendStatusDialog,
    login,
    register,
    verifyEmailCode,
    resendVerificationCode,
    resetPassword,
    signInWithGoogle,
    quickDemoLogin,
    logout,
    // Onboarding & Diagnostic
    questionnaireAnswers: activeUserData?.questionnaireAnswers ?? {},
    completeOnboardingIntro,
    submitQuestionnaireAnswer,
    completeQuestionnaire,
    diagnosticIndex,
    diagnosticScore,
    diagnosticFinished,
    recommendedPath,
    answerDiagnosticQuestion,
    enterAdventureAfterDiagnostic,
    // Quiz & Remedial
    quizState,
    startQuizForLevel,
    selectQuizOption,
    checkQuizAnswer,
    nextQuizQuestion,
    selectRemedialOption,
    checkRemedialAnswer,
    retryRemedialTask,
    nextRemedialTask,
    exitQuizToMap,
    matchingSelectedEnglish,
    matchingAnswers,
    selectMatchingEnglish,
    pairMatchingIndonesian,
    assignMatchingDirect,
    unpairMatching,
    typedAnswer,
    setTypedAnswer,
    clueStep,
    advanceClueStep,
    scrambledSlots,
    availableLetters,
    pickScrambledLetter,
    removeScrambledLetter,
    resetScrambledLetters,
    sentenceBuilderSlots,
    availableSentenceChips,
    pickSentenceChip,
    removeSentenceChip,
    resetSentenceBuilder,
    categoryBucketAssignments,
    selectedCategoryWord,
    selectCategoryWord,
    assignCategoryWord,
    unassignCategoryWord,
    memoryFlippedIndices,
    memoryMatchedCards,
    flipMemoryCard,
    selectedTrueFalse,
    selectTrueFalse,
    selectedGridCoords,
    toggleGridCoord,
    clearGridCoords,
    // Lexicon / Lore / Beasts
    vocabularyList,
    filteredVocabulary,
    vocabSearchQuery,
    setVocabSearchQuery,
    vocabCategoryFilter,
    setVocabCategoryFilter,
    toggleLearnedWord,
    storyCompletions,
    activeStory,
    setActiveStory: openStory,
    completeStory,
    companionsList,
    equipCompanion,
    speakWord,
    // Web Storage & Backup
    storageWarning,
    dismissStorageWarning,
    exportProgressBackup,
    importProgressBackup,
  };
}
