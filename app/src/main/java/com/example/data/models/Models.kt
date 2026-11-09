package com.example.data.models

enum class LearningPath(
    val code: String,
    val title: String,
    val description: String,
    val targetWordsPerDay: Int,
    val cefrLabel: String,
    val uiLanguage: String
) {
    LOW(
        code = "LOW",
        title = "Beginner Explorer",
        description = "Materi dasar, kosakata sehari-hari dengan instruksi bahasa Indonesia",
        targetWordsPerDay = 10,
        cefrLabel = "A1–A2",
        uiLanguage = "Bahasa Indonesia"
    ),
    MIDDLE(
        code = "MIDDLE",
        title = "Skilled Explorer",
        description = "Materi lebih beragam, frasa kontekstual dengan bahasa Inggris sederhana",
        targetWordsPerDay = 18,
        cefrLabel = "A2–B1",
        uiLanguage = "Simple English"
    ),
    HIGH(
        code = "HIGH",
        title = "Master Explorer",
        description = "Kosakata kompleks, kolokasi, bacaan dan pemahaman menyeluruh",
        targetWordsPerDay = 28,
        cefrLabel = "B1–B2",
        uiLanguage = "English"
    );

    fun canAccess(targetPath: LearningPath): Boolean {
        return when (this) {
            LOW -> targetPath == LOW
            MIDDLE -> targetPath == LOW || targetPath == MIDDLE
            HIGH -> true
        }
    }
}

enum class LevelStatus {
    LOCKED,
    AVAILABLE,
    COMPLETED
}

enum class QuestionType {
    MULTIPLE_CHOICE,
    DRAG_AND_MATCH,
    FILL_BLANK,
    WORD_UNSCRAMBLE,
    IMAGE_VOCABULARY,
    SENTENCE_COMPLETION,
    // New Interactive Adventure Question Types
    SCRAMBLED_LETTERS,
    SENTENCE_BUILDER,
    LISTEN_AND_TYPE,
    PICTURE_TAP,
    CATEGORY_SORT,
    MEMORY_FLIP,
    TRUE_OR_FALSE,
    WORD_HUNT
}

data class PictureOption(
    val id: String,
    val vectorIconKey: String, // "compass" | "map" | "tree" | "tent" | "lantern" | "boots" | "backpack" | "branch"
    val label: String
)

data class Question(
    val id: String,
    val type: QuestionType,
    val prompt: String,
    val subText: String? = null,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanation: String,
    val targetWord: String,
    val matchingPairs: Map<String, String>? = null, // For Drag and Match & Memory Flip: English -> Indonesian
    val iconEmoji: String? = null,                 // For Image-based vocabulary
    val scrambledLetters: List<String>? = null,     // For Word Arrangement / Scrambled Letters
    val sentenceWithBlank: String? = null,          // Contextual sentence
    val hint: String? = null,                       // Hint text for typing questions
    val sentenceTokens: List<String>? = null,       // For Sentence Builder: Shuffled word chips
    val orderedSentence: String? = null,           // For Sentence Builder: Expected sentence
    val audioPromptWord: String? = null,           // For Listen and Type: Word spoken by TTS
    val pictureOptions: List<PictureOption>? = null,// For Picture Tap: Vector illustrations
    val isStatementTrue: Boolean? = null,          // For True or False: Expected boolean
    val gridLetters: List<List<String>>? = null,   // For Word Hunt: 5x5 character grid
    val clue: String? = null,                      // Task 7: Indirect function/characteristic clue in Indonesian
    val clue2: String? = null,                     // Task 7: Deeper contextual hint + letter count
    val hintsEnabled: Boolean = true,              // Task 7: Set to false on highest level
    val failureFeedback: String? = null            // Task 2: Specific error feedback for sorting or wrong answer
)

data class LevelInfo(
    val id: String, // e.g. "1-1"
    val stageNumber: Int, // 1 to 5
    val levelNumber: Int, // 1 to 10
    val title: String,
    val path: LearningPath,
    val posX: Float, // Normalized isometric map coordinate (0f..1000f)
    val posY: Float, // Normalized isometric map coordinate (0f..1000f)
    val targetWords: List<String>,
    val status: LevelStatus = LevelStatus.LOCKED,
    val stars: Int = 0,
    val bestScore: Int = 0
)

data class StageInfo(
    val number: Int,
    val name: String,
    val regionTheme: String,
    val description: String,
    val colorHex: String
)

data class ExplorationRegion(
    val stageNumber: Int,
    val name: String,
    val subtitle: String,
    val description: String,
    val pixelX: Float,
    val pixelY: Float,
    val bannerX: Float,
    val bannerY: Float,
    val iconEmoji: String,
    val colorHex: String
)

data class VocabularyItem(
    val id: String,
    val word: String,
    val indonesian: String,
    val englishDef: String,
    val partOfSpeech: String,
    val pronunciation: String,
    val exampleSentence: String,
    val exampleTranslation: String,
    val cefr: String,
    val category: String,
    val isLearned: Boolean = false,
    val clue: String = "",
    val clue2: String = ""
)

data class CompanionAnimal(
    val id: String,
    val name: String,
    val species: String,
    val trait: String,
    val description: String,
    val requiredStreak: Int,
    val isMythical: Boolean,
    val emoji: String,
    val isUnlocked: Boolean = false
)

data class AdventureStory(
    val id: String,
    val title: String,
    val region: String,
    val cefr: String,
    val targetWords: List<String>,
    val synopsis: String,
    val paragraphs: List<String>,
    val comprehensionQuestions: List<Question>,
    val isCompleted: Boolean = false
)

data class DiagnosticQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val cefrLevel: String,
    val explanation: String
)

data class RemedialTask(
    val id: String,
    val originalQuestionId: String,
    val failedWord: String,
    val question: Question,
    val isResolved: Boolean = false
)

data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String,
    val initialPath: LearningPath,
    val currentPath: LearningPath,
    val dailyTarget: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val activeCompanionId: String,
    val lastStudyDate: String,
    val onboardingCompleted: Boolean,
    val diagnosticScore: Int
)
