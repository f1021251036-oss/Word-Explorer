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
    WORD_MATCHING,
    FILL_BLANK,
    WORD_UNSCRAMBLE,
    CONTEXT_CHOICE
}

data class Question(
    val id: String,
    val type: QuestionType,
    val prompt: String,
    val subText: String? = null,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String,
    val targetWord: String
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
    val isLearned: Boolean = false
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
