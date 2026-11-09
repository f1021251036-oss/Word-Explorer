package com.example.data.firestore

import android.content.Context
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class WordExplorerFirestoreRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    // Convenience constructor: dynamically resolves custom database ID
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        FirebaseAuth.getInstance()
    )

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun requireUserId(): String {
        return auth.currentUser?.uid ?: error("Operation requires authenticated user")
    }

    // --- User Profile ---

    suspend fun saveUserProfile(profile: FirestoreUser): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid"
        return try {
            val userMap = hashMapOf(
                "uid" to uid,
                "displayName" to profile.displayName,
                "email" to profile.email,
                "photoURL" to profile.photoURL,
                "authProviders" to profile.authProviders,
                "preferredLanguage" to profile.preferredLanguage,
                "learningPathway" to profile.learningPathway,
                "accountStatus" to profile.accountStatus,
                "createdAt" to FieldValue.serverTimestamp(),
                "lastLoginAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(uid).set(userMap, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String = requireUserId()): Result<FirestoreUser?> {
        val path = "users/$userId"
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            if (doc.exists()) {
                val user = FirestoreUser(
                    uid = doc.getString("uid") ?: userId,
                    displayName = doc.getString("displayName") ?: "",
                    email = doc.getString("email") ?: "",
                    photoURL = doc.getString("photoURL") ?: "",
                    authProviders = doc.getString("authProviders") ?: "google",
                    preferredLanguage = doc.getString("preferredLanguage") ?: "id",
                    learningPathway = doc.getString("learningPathway") ?: "LOW",
                    accountStatus = doc.getString("accountStatus") ?: "ACTIVE"
                )
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    // --- Main Learning Progress ---

    suspend fun saveLearningProgress(progress: FirestoreLearningProgress): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/learningProgress/main"
        return try {
            val progressMap = hashMapOf(
                "currentChapter" to progress.currentChapter,
                "currentLevel" to progress.levelNumberClamped(),
                "completedLevels" to progress.completedLevels,
                "unlockedLevels" to progress.unlockedLevels,
                "totalStars" to progress.totalStars,
                "totalCompletedLevels" to progress.totalCompletedLevels,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(uid)
                .collection("learningProgress").document("main")
                .set(progressMap, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun getLearningProgress(userId: String = requireUserId()): Result<FirestoreLearningProgress?> {
        val path = "users/$userId/learningProgress/main"
        return try {
            val doc = firestore.collection("users").document(userId)
                .collection("learningProgress").document("main").get().await()
            if (doc.exists()) {
                val data = FirestoreLearningProgress(
                    currentChapter = doc.getLong("currentChapter")?.toInt() ?: 1,
                    currentLevel = doc.getLong("currentLevel")?.toInt() ?: 1,
                    completedLevels = (doc.get("completedLevels") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                    unlockedLevels = (doc.get("unlockedLevels") as? List<*>)?.mapNotNull { it?.toString() } ?: listOf("1-1"),
                    totalStars = doc.getLong("totalStars")?.toInt() ?: 0,
                    totalCompletedLevels = doc.getLong("totalCompletedLevels")?.toInt() ?: 0
                )
                Result.success(data)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    // --- Daily Activity (Enforcing 3 Levels/Day Adventure Limit in Cloud) ---

    suspend fun saveDailyActivity(activity: FirestoreDailyActivity): Result<Unit> {
        val uid = requireUserId()
        val date = activity.date
        val path = "users/$uid/dailyActivity/$date"
        return try {
            // Anti-exploit: clamp levels completed to max 3
            val clampedLevels = activity.adventureLevelsCompleted.coerceIn(0, 3)
            val activityMap = hashMapOf(
                "date" to date,
                "vocabularyCompleted" to activity.vocabularyCompleted,
                "adventureLevelsCompleted" to clampedLevels,
                "dailyTargetCompleted" to activity.dailyTargetCompleted,
                "streakQualified" to activity.streakQualified,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(uid)
                .collection("dailyActivity").document(date)
                .set(activityMap, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun getDailyActivity(date: String, userId: String = requireUserId()): Result<FirestoreDailyActivity?> {
        val path = "users/$userId/dailyActivity/$date"
        return try {
            val doc = firestore.collection("users").document(userId)
                .collection("dailyActivity").document(date).get().await()
            if (doc.exists()) {
                val activity = FirestoreDailyActivity(
                    date = doc.getString("date") ?: date,
                    vocabularyCompleted = doc.getLong("vocabularyCompleted")?.toInt() ?: 0,
                    adventureLevelsCompleted = doc.getLong("adventureLevelsCompleted")?.toInt() ?: 0,
                    dailyTargetCompleted = doc.getBoolean("dailyTargetCompleted") ?: false,
                    streakQualified = doc.getBoolean("streakQualified") ?: false
                )
                Result.success(activity)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    // --- Daily Vocabulary Rotation ---

    suspend fun saveDailyVocabulary(dailyVocab: FirestoreDailyVocabulary): Result<Unit> {
        val uid = requireUserId()
        val date = dailyVocab.assignmentDate
        val path = "users/$uid/dailyVocabulary/$date"
        return try {
            val vocabMap = hashMapOf(
                "assignmentDate" to date,
                "assignedVocabularyIds" to dailyVocab.assignedVocabularyIds,
                "totalAssigned" to dailyVocab.totalAssigned,
                "completedCount" to dailyVocab.completedCount,
                "completed" to dailyVocab.completed
            )
            firestore.collection("users").document(uid)
                .collection("dailyVocabulary").document(date)
                .set(vocabMap, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun getDailyVocabulary(date: String, userId: String = requireUserId()): Result<FirestoreDailyVocabulary?> {
        val path = "users/$userId/dailyVocabulary/$date"
        return try {
            val doc = firestore.collection("users").document(userId)
                .collection("dailyVocabulary").document(date).get().await()
            if (doc.exists()) {
                val vocab = FirestoreDailyVocabulary(
                    assignmentDate = doc.getString("assignmentDate") ?: date,
                    assignedVocabularyIds = (doc.get("assignedVocabularyIds") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                    totalAssigned = doc.getLong("totalAssigned")?.toInt() ?: 0,
                    completedCount = doc.getLong("completedCount")?.toInt() ?: 0,
                    completed = doc.getBoolean("completed") ?: false
                )
                Result.success(vocab)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    // --- Quiz Result ---

    suspend fun saveQuizResult(resultId: String, quizResult: FirestoreQuizResult): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/quizResults/$resultId"
        return try {
            val quizMap = hashMapOf(
                "chapter" to quizResult.chapter,
                "level" to quizResult.level,
                "score" to quizResult.score,
                "stars" to quizResult.stars,
                "remedialRequired" to quizResult.remedialRequired,
                "remedialCompleted" to quizResult.remedialCompleted,
                "completedAt" to FieldValue.serverTimestamp()
            )
            firestore.collection("users").document(uid)
                .collection("quizResults").document(resultId)
                .set(quizMap, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- Vocabulary Progress ---

    suspend fun saveVocabularyProgress(vocab: FirestoreVocabularyProgress): Result<Unit> {
        val uid = requireUserId()
        val vocabId = vocab.vocabularyId
        val path = "users/$uid/vocabulary/$vocabId"
        return try {
            val map = hashMapOf(
                "vocabularyId" to vocabId,
                "word" to vocab.word,
                "meaning" to vocab.meaning,
                "learningStatus" to vocab.learningStatus,
                "assignedDate" to vocab.assignedDate,
                "reviewCount" to vocab.reviewCount
            )
            firestore.collection("users").document(uid)
                .collection("vocabulary").document(vocabId)
                .set(map, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    private fun FirestoreLearningProgress.levelNumberClamped(): Int = currentLevel.coerceIn(1, 10)
}
