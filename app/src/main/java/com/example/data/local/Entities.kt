package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val passwordHash: String = "",
    val isEmailVerified: Boolean = false,
    val googleId: String? = null,
    val authProviders: String = "email_password", // "email_password", "google", or "email_password,google"
    val otpHash: String? = null,                  // SHA-256 hash of 6-digit OTP
    val otpExpiry: Long = 0L,                     // 5-minute expiry timestamp
    val otpAttempts: Int = 0,                     // Failed attempt counter (max 5)
    val lastOtpResendAt: Long = 0L,               // Cooldown tracker (60 seconds)
    val verificationCode: String = "",            // Retained for backward compatibility
    val codeExpiry: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_sessions")
data class UserSessionEntity(
    @PrimaryKey val id: String = "active_session",
    val userId: String,
    val authMethod: String, // "google" or "email_password"
    val loggedInAt: Long = System.currentTimeMillis()
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

@Entity(tableName = "daily_level_completions")
data class DailyLevelCompletionEntity(
    @PrimaryKey val id: String, // "${userId}_${dateString}_${levelId}"
    val userId: String,
    val dateString: String,      // "yyyy-MM-dd"
    val path: String,
    val levelId: String,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_vocab_assignments")
data class DailyVocabAssignmentEntity(
    @PrimaryKey val id: String, // "${userId}_${dateString}_${vocabId}"
    val userId: String,
    val dateString: String,      // "yyyy-MM-dd"
    val path: String,
    val vocabId: String,
    val status: String,          // "NEW", "LEARNING", "MASTERED", "NEEDS_REVIEW"
    val assignedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
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
