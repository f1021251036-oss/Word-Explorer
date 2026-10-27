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

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun observeUserProfile(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getUserProfile(userId: String): UserProfileEntity?
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

    @Query("SELECT * FROM vocabulary WHERE word LIKE '%' || :query || '%' OR indonesian LIKE '%' || :query || '%' ORDER BY word ASC")
    fun searchVocabulary(query: String): Flow<List<VocabularyEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary")
    suspend fun getCount(): Int

    @Query("UPDATE vocabulary SET isLearned = :learned WHERE id = :id")
    suspend fun updateLearned(id: String, learned: Boolean)
}
