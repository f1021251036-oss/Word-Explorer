package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val onboardingCompleted: Boolean = false,
    val diagnosticScore: Int = -1,
    val initialPath: String = "LOW",
    val currentPath: String = "LOW",
    val dailyTarget: Int = 10,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastStudyDate: String = "",
    val activeCompanionId: String = "fox"
)

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val id: String, // "${userId}_${path}_${levelId}"
    val userId: String,
    val path: String,
    val levelId: String,
    val stageNumber: Int,
    val levelNumber: Int,
    val status: String, // LOCKED, AVAILABLE, COMPLETED
    val stars: Int,
    val firstAttemptScore: Int,
    val completedAt: Long = 0L
)

@Entity(tableName = "remedials")
data class RemedialEntity(
    @PrimaryKey val id: String, // "${userId}_${path}_${levelId}_${questionId}"
    val userId: String,
    val path: String,
    val levelId: String,
    val questionId: String,
    val targetWord: String,
    val isResolved: Boolean = false
)

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey val id: String,
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

@Entity(tableName = "story_progress")
data class StoryProgressEntity(
    @PrimaryKey val id: String, // "${userId}_${storyId}"
    val userId: String,
    val storyId: String,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L
)
