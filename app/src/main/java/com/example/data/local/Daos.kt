package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE googleId = :googleId LIMIT 1")
    suspend fun getUserByGoogleId(googleId: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun observeUserProfile(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getUserProfile(userId: String): UserProfileEntity?

    // Session Management for Persistent Login
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: UserSessionEntity)

    @Query("SELECT * FROM user_sessions WHERE id = 'active_session' LIMIT 1")
    suspend fun getActiveSession(): UserSessionEntity?

    @Query("DELETE FROM user_sessions WHERE id = 'active_session'")
    suspend fun clearSession()
}

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelProgress(progress: LevelProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelProgressList(progressList: List<LevelProgressEntity>)

    @Query("SELECT * FROM level_progress WHERE userId = :userId AND path = :path")
    fun observeProgressForPath(userId: String, path: String): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE userId = :userId AND path = :path")
    suspend fun getProgressForPath(userId: String, path: String): List<LevelProgressEntity>

    @Query("SELECT * FROM level_progress WHERE userId = :userId AND path = :path AND levelId = :levelId LIMIT 1")
    suspend fun getLevelProgress(userId: String, path: String, levelId: String): LevelProgressEntity?

    // Daily Level Completions (Anti-Exploit 3 Levels/Day Limit)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLevelCompletion(completion: DailyLevelCompletionEntity)

    @Query("SELECT * FROM daily_level_completions WHERE userId = :userId AND dateString = :dateString")
    suspend fun getDailyLevelCompletions(userId: String, dateString: String): List<DailyLevelCompletionEntity>

    @Query("SELECT * FROM daily_level_completions WHERE userId = :userId AND dateString = :dateString")
    fun observeDailyLevelCompletions(userId: String, dateString: String): Flow<List<DailyLevelCompletionEntity>>

    // Remedials
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemedials(remedials: List<RemedialEntity>)

    @Query("SELECT * FROM remedials WHERE userId = :userId AND path = :path AND levelId = :levelId")
    suspend fun getRemedialsForLevel(userId: String, path: String, levelId: String): List<RemedialEntity>

    @Query("UPDATE remedials SET isResolved = 1 WHERE userId = :userId AND path = :path AND levelId = :levelId AND questionId = :questionId")
    suspend fun resolveRemedial(userId: String, path: String, levelId: String, questionId: String)

    @Query("DELETE FROM remedials WHERE userId = :userId AND path = :path AND levelId = :levelId")
    suspend fun clearRemedialsForLevel(userId: String, path: String, levelId: String)

    // Stories
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setStoryProgress(storyProgress: StoryProgressEntity)

    @Query("SELECT * FROM story_progress WHERE userId = :userId")
    fun observeStoryProgress(userId: String): Flow<List<StoryProgressEntity>>
}

@Dao
interface VocabularyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vocabList: List<VocabularyEntity>)

    @Query("SELECT * FROM vocabulary ORDER BY word ASC")
    fun observeAllVocabulary(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary ORDER BY word ASC")
    suspend fun getAllVocabularyList(): List<VocabularyEntity>

    @Query("SELECT * FROM vocabulary WHERE word LIKE '%' || :query || '%' OR indonesian LIKE '%' || :query || '%' ORDER BY word ASC")
    fun searchVocabulary(query: String): Flow<List<VocabularyEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary")
    suspend fun getCount(): Int

    @Query("UPDATE vocabulary SET isLearned = :learned WHERE id = :id")
    suspend fun updateLearned(id: String, learned: Boolean)

    @Query("SELECT * FROM vocabulary WHERE id = :id LIMIT 1")
    suspend fun getVocabularyById(id: String): VocabularyEntity?

    // Daily Vocabulary Rotation Tracking
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyAssignments(assignments: List<DailyVocabAssignmentEntity>)

    @Query("SELECT * FROM daily_vocab_assignments WHERE userId = :userId AND dateString = :dateString")
    fun observeDailyAssignments(userId: String, dateString: String): Flow<List<DailyVocabAssignmentEntity>>

    @Query("SELECT * FROM daily_vocab_assignments WHERE userId = :userId AND dateString = :dateString")
    suspend fun getDailyAssignments(userId: String, dateString: String): List<DailyVocabAssignmentEntity>

    @Query("SELECT DISTINCT vocabId FROM daily_vocab_assignments WHERE userId = :userId")
    suspend fun getAllAssignedVocabIds(userId: String): List<String>

    @Query("UPDATE daily_vocab_assignments SET status = :status, updatedAt = :updatedAt WHERE userId = :userId AND dateString = :dateString AND vocabId = :vocabId")
    suspend fun updateDailyVocabStatus(userId: String, dateString: String, vocabId: String, status: String, updatedAt: Long)
}
