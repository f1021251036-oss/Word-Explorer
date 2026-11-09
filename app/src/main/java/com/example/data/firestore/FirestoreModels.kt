package com.example.data.firestore

data class FirestoreUser(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoURL: String = "",
    val authProviders: String = "google",
    val preferredLanguage: String = "id",
    val learningPathway: String = "LOW",
    val accountStatus: String = "ACTIVE"
)

data class FirestoreLearningProgress(
    val currentChapter: Int = 1,
    val currentLevel: Int = 1,
    val completedLevels: List<String> = emptyList(),
    val unlockedLevels: List<String> = listOf("1-1"),
    val totalStars: Int = 0,
    val totalCompletedLevels: Int = 0
)

data class FirestoreDailyActivity(
    val date: String = "",
    val vocabularyCompleted: Int = 0,
    val adventureLevelsCompleted: Int = 0,
    val dailyTargetCompleted: Boolean = false,
    val streakQualified: Boolean = false
)

data class FirestoreDailyVocabulary(
    val assignmentDate: String = "",
    val assignedVocabularyIds: List<String> = emptyList(),
    val totalAssigned: Int = 0,
    val completedCount: Int = 0,
    val completed: Boolean = false
)

data class FirestoreQuizResult(
    val chapter: Int = 1,
    val level: Int = 1,
    val score: Int = 0,
    val stars: Int = 0,
    val remedialRequired: Boolean = false,
    val remedialCompleted: Boolean = false
)

data class FirestoreVocabularyProgress(
    val vocabularyId: String = "",
    val word: String = "",
    val meaning: String = "",
    val learningStatus: String = "NEW",
    val assignedDate: String = "",
    val reviewCount: Int = 0
)
