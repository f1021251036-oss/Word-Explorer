import { getTopicForLevel, type Question } from './vocabCatalog';

export type LearningPath = 'LOW' | 'MIDDLE' | 'HIGH';

export interface LearningPathMeta {
  code: LearningPath;
  title: string;
  description: string;
  targetWordsPerDay: number;
  dailyWordPoolSize: number;
  cefrLabel: string;
  uiLanguage: string;
}

export const LEARNING_PATH_META: Record<LearningPath, LearningPathMeta> = {
  LOW: {
    code: 'LOW',
    title: 'Beginner Explorer',
    description: 'Materi dasar, kosakata sehari-hari dengan instruksi bahasa Indonesia',
    targetWordsPerDay: 10,
    dailyWordPoolSize: 25,
    cefrLabel: 'A1–A2',
    uiLanguage: 'Bahasa Indonesia',
  },
  MIDDLE: {
    code: 'MIDDLE',
    title: 'Skilled Explorer',
    description: 'Materi lebih beragam, frasa kontekstual dengan bahasa Inggris sederhana',
    targetWordsPerDay: 18,
    dailyWordPoolSize: 35,
    cefrLabel: 'A2–B1',
    uiLanguage: 'Simple English',
  },
  HIGH: {
    code: 'HIGH',
    title: 'Master Explorer',
    description: 'Kosakata kompleks, kolokasi, bacaan dan pemahaman menyeluruh',
    targetWordsPerDay: 28,
    dailyWordPoolSize: 40,
    cefrLabel: 'B1–B2',
    uiLanguage: 'English',
  },
};

export function canAccessPath(userInitialPath: LearningPath, targetPath: LearningPath): boolean {
  if (userInitialPath === 'LOW') return targetPath === 'LOW';
  if (userInitialPath === 'MIDDLE') return targetPath === 'LOW' || targetPath === 'MIDDLE';
  return true;
}

export type LevelStatus = 'LOCKED' | 'AVAILABLE' | 'COMPLETED';

export type QuestionType =
  | 'MULTIPLE_CHOICE'
  | 'DRAG_AND_MATCH'
  | 'FILL_BLANK'
  | 'WORD_UNSCRAMBLE'
  | 'IMAGE_VOCABULARY'
  | 'SENTENCE_COMPLETION'
  | 'SCRAMBLED_LETTERS'
  | 'SENTENCE_BUILDER'
  | 'LISTEN_AND_TYPE'
  | 'PICTURE_TAP'
  | 'CATEGORY_SORT'
  | 'MEMORY_FLIP'
  | 'TRUE_OR_FALSE'
  | 'WORD_HUNT';

export interface LevelInfo {
  id: string;
  stageNumber: number;
  levelNumber: number;
  title: string;
  path: LearningPath;
  posX: number;
  posY: number;
  targetWords: string[];
  status: LevelStatus;
  stars: number;
  bestScore: number;
}

export interface ExplorationRegion {
  stageNumber: number;
  name: string;
  shortName: string;
  subtitle: string;
  description: string;
  pixelX: number;
  pixelY: number;
  bannerX: number;
  bannerY: number;
  iconEmoji: string;
}

export interface CompanionAnimal {
  id: string;
  name: string;
  species: string;
  trait: string;
  description: string;
  requiredStreak: number;
  isMythical: boolean;
  emoji: string;
}

export interface UserProfile {
  id: string;
  email: string;
  displayName: string;
  initialPath: LearningPath;
  currentPath: LearningPath;
  dailyTarget: number;
  currentStreak: number;
  bestStreak: number;
  xp: number;
  activeCompanionId: string;
  lastStudyDate: string;
  onboardingCompleted: boolean;
  diagnosticScore: number;
}

export interface RegisteredAccount {
  id: string;
  email: string;
  displayName: string;
  passwordHash: string;
  isEmailVerified: boolean;
  otpHash?: string;
  otpExpiresAt?: number;
  otpFailedAttempts?: number;
}

export interface BackendConfigStatus {
  googleOAuthClientIdConfigured: boolean;
  googleOAuthClientIdPreview?: string;
  smtpConfigured: boolean;
  smtpHost?: string;
  smtpPort?: number;
  smtpSenderEmail?: string;
  otpAlgorithm: string;
  accountLinkingStatus: string;
  dailyLimitStatus: string;
}

export interface DailyVocabAssignment {
  word: string;
  status: 'NEW' | 'LEARNING' | 'MASTERED' | 'NEEDS_REVIEW';
  date: string;
}

export interface RemedialTask {
  originalQuestionId: string;
  targetWord: string;
  question: Question;
  isResolved: boolean;
}

export interface QuizUiState {
  stage: number;
  level: number;
  path: LearningPath;
  questions: Question[];
  currentQuestionIndex: number;
  selectedOption: string | null;
  isAnswerChecked: boolean;
  isAnswerCorrect: boolean;
  firstAttemptScore: number;
  failedQuestions: Question[];
  isQuizFinished: boolean;
  isRemedialMode: boolean;
  initialRemedialCount: number;
  remedialTasks: RemedialTask[];
  currentRemedialIndex: number;
  remedialSelectedOption: string | null;
  isRemedialChecked: boolean;
  isRemedialCorrect: boolean;
  starsAwarded: number;
  isLevelCompleted: boolean;
}

export type AuthScreenMode = 'login' | 'register' | 'forgot' | 'verify';
export type AppScreen =
  | 'auth'
  | 'onboarding'
  | 'questionnaire'
  | 'diagnostic'
  | 'main'
  | 'quiz';

export type NavTab = 'map' | 'camp' | 'lexicon' | 'lore' | 'beasts' | 'settings';

export const NATIVE_MAP_WIDTH = 899;
export const NATIVE_MAP_HEIGHT = 1600;
export const DAILY_LEVEL_LIMIT = 3;

export const EXPLORATION_REGIONS: ExplorationRegion[] = [
  {
    stageNumber: 1,
    name: 'Forest',
    shortName: 'Forest',
    subtitle: 'Lush Green Woodland',
    description:
      'Starting exploration area in a lush tropical forest, dense trees, and woodland paths.',
    pixelX: 280,
    pixelY: 1360,
    bannerX: 240,
    bannerY: 1285,
    iconEmoji: '🌲',
  },
  {
    stageNumber: 2,
    name: 'Island',
    shortName: 'Island',
    subtitle: 'Tropical Shore & Lagoon',
    description:
      'A tropical island surrounded by blue sea, sandy beaches, coconut palm trees, and coastal rocks.',
    pixelX: 570,
    pixelY: 890,
    bannerX: 265,
    bannerY: 940,
    iconEmoji: '🏝️',
  },
  {
    stageNumber: 3,
    name: 'Ancient Rome',
    shortName: 'Rome',
    subtitle: 'Colosseum & Classical Ruins',
    description:
      'An ancient Roman city with historical ruins, monumental Colosseum, classical pillars, and stone arches.',
    pixelX: 510,
    pixelY: 640,
    bannerX: 440,
    bannerY: 490,
    iconEmoji: '🏛️',
  },
  {
    stageNumber: 4,
    name: 'Mountain',
    shortName: 'Mountain',
    subtitle: 'Rocky & Snowy Peaks',
    description:
      'A towering snowy mountain region above Ancient Rome with rocky switchback trails, snowy peaks, and high cliffs.',
    pixelX: 440,
    pixelY: 280,
    bannerX: 365,
    bannerY: 105,
    iconEmoji: '🏔️',
  },
  {
    stageNumber: 5,
    name: 'Cave',
    shortName: 'Cave',
    subtitle: 'Glowing Blue Crystal Cavern',
    description:
      'The final destination featuring a mysterious subterranean cave entrance with glowing blue crystals.',
    pixelX: 770,
    pixelY: 160,
    bannerX: 805,
    bannerY: 115,
    iconEmoji: '💎',
  },
];

export const INITIAL_COMPANIONS: CompanionAnimal[] = [
  {
    id: 'fox',
    name: 'Rusty the Forest Fox',
    species: 'Red Fox',
    trait: 'Quick Reflexes',
    description: 'Guides you through tangled woods and alerts you to hidden treasure.',
    requiredStreak: 0,
    isMythical: false,
    emoji: '🦊',
  },
  {
    id: 'parrot',
    name: 'Echo the Jungle Parrot',
    species: 'Macaw',
    trait: 'Vocabulary Mimic',
    description: 'Repeats pronunciations and sings cheering tunes when you learn new words.',
    requiredStreak: 7,
    isMythical: false,
    emoji: '🦜',
  },
  {
    id: 'otter',
    name: 'Splash the Island Otter',
    species: 'River Otter',
    trait: 'Curious Fisher',
    description: 'Dives into clear currents and retrieves ancient forgotten coins.',
    requiredStreak: 14,
    isMythical: false,
    emoji: '🦦',
  },
  {
    id: 'fennec',
    name: 'Zephyr the Desert Fennec',
    species: 'Fennec Fox',
    trait: 'Acuity Listener',
    description: 'Large ears hear faint desert whispers and helps unscramble tricky words.',
    requiredStreak: 21,
    isMythical: false,
    emoji: '🐾',
  },
  {
    id: 'hawk',
    name: 'Sol the Mountain Hawk',
    species: 'Golden Hawk',
    trait: 'Apex Scout',
    description: 'Soars above misty mountain peaks to map out the next expedition.',
    requiredStreak: 28,
    isMythical: false,
    emoji: '🦅',
  },
  {
    id: 'griffin',
    name: 'Aurelius the Celestial Griffin',
    species: 'Mythical Griffin',
    trait: 'Ancient Wisdom',
    description: 'Protector of classical ruins and master of Greek & Latin word roots.',
    requiredStreak: 30,
    isMythical: true,
    emoji: '🦁',
  },
  {
    id: 'phoenix',
    name: 'Ignis the Solar Phoenix',
    species: 'Flame Phoenix',
    trait: 'Endless Resilience',
    description: 'Reborn from study flames, empowering uninterrupted streaks.',
    requiredStreak: 60,
    isMythical: true,
    emoji: '🔥',
  },
  {
    id: 'dragon',
    name: 'Azure the Crystal Dragon',
    species: 'Crystal Drake',
    trait: 'Omni Lexicon',
    description: 'Ancient keeper of the deepest subterranean vocabulary vault.',
    requiredStreak: 90,
    isMythical: true,
    emoji: '🐉',
  },
];

export function getLevelCoordinates(): Array<[number, number]> {
  return [
    // Chapter 1 (1-1 to 1-10)
    [324, 1460],
    [340, 1400],
    [366, 1345],
    [390, 1295],
    [416, 1242],
    [440, 1205],
    [461, 1170],
    [469, 1106],
    [500, 1080],
    [545, 1055],
    // Chapter 2 (2-1 to 2-10)
    [585, 1025],
    [615, 980],
    [575, 940],
    [530, 915],
    [505, 875],
    [530, 835],
    [580, 820],
    [635, 815],
    [685, 805],
    [730, 775],
    // Chapter 3 (3-1 to 3-10)
    [675, 755],
    [624, 750],
    [565, 740],
    [505, 730],
    [465, 705],
    [425, 665],
    [402, 620],
    [404, 565],
    [430, 520],
    [480, 485],
    // Chapter 4 (4-1 to 4-10)
    [530, 445],
    [500, 405],
    [445, 385],
    [385, 375],
    [359, 345],
    [390, 315],
    [445, 295],
    [492, 280],
    [487, 226],
    [421, 158],
    // Chapter 5 (5-1 to 5-10)
    [465, 140],
    [515, 130],
    [570, 125],
    [625, 120],
    [675, 120],
    [725, 125],
    [765, 135],
    [795, 155],
    [815, 185],
    [830, 225],
  ];
}

export function buildInitialLevels(path: LearningPath): LevelInfo[] {
  const coords = getLevelCoordinates();
  return coords.map(([posX, posY], idx) => {
    const stageNumber = Math.floor(idx / 10) + 1;
    const levelNumber = (idx % 10) + 1;
    const id = `${stageNumber}-${levelNumber}`;
    const topic = getTopicForLevel(stageNumber, levelNumber);
    return {
      id,
      stageNumber,
      levelNumber,
      title: `Stage ${id}: ${topic.topicTitle}`,
      path,
      posX,
      posY,
      targetWords: topic.vocabulary.slice(0, 3).map((item) => item.word),
      status: idx === 0 ? 'AVAILABLE' : 'LOCKED',
      stars: 0,
      bestScore: 0,
    };
  });
}

export function isRegionUnlocked(levels: LevelInfo[], stageNumber: number): boolean {
  if (stageNumber <= 1) return true;
  const prevStageLevels = levels.filter((lvl) => lvl.stageNumber === stageNumber - 1);
  return prevStageLevels.length > 0 && prevStageLevels.every((lvl) => lvl.status === 'COMPLETED');
}

export function computeStarsForScore(score: number): number {
  if (score >= 10) return 3;
  if (score >= 7) return 2;
  if (score >= 5) return 1;
  return 0;
}
