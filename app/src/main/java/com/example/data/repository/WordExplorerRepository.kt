package com.example.data.repository

import com.example.data.local.*
import com.example.data.models.*
import com.example.data.seed.InitialDataSeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class WordExplorerRepository(
    private val userDao: UserDao,
    private val progressDao: ProgressDao,
    private val vocabularyDao: VocabularyDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // --- Authentication & User Session ---

    suspend fun registerUser(email: String, displayName: String, password: String):Result<UserEntity> {
        val existing = userDao.getUserByEmail(email.trim().lowercase())
        if (existing != null) {
            return Result.failure(Exception("Email is already registered. Please login."))
        }
        val userId = "user_${UUID.randomUUID().toString().take(8)}"
        val user = UserEntity(
            id = userId,
            email = email.trim().lowercase(),
            displayName = displayName.ifBlank { "Young Explorer" },
            passwordHash = hashPassword(password)
        )
        userDao.insertUser(user)
        val profile = UserProfileEntity(
            userId = userId,
            onboardingCompleted = false,
            diagnosticScore = -1,
            initialPath = LearningPath.LOW.code,
            currentPath = LearningPath.LOW.code,
            dailyTarget = 10,
            currentStreak = 1,
            bestStreak = 1,
            lastStudyDate = dateFormat.format(Date()),
            activeCompanionId = "fox"
        )
        userDao.insertOrUpdateProfile(profile)
        initializePathLevels(userId, LearningPath.LOW)
        return Result.success(user)
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntity> {
        val user = userDao.getUserByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("Account not found. Please register first."))
        if (user.passwordHash != hashPassword(password)) {
            return Result.failure(Exception("Incorrect password."))
        }
        return Result.success(user)
    }

    suspend fun quickLoginDemo(): UserEntity {
        val demoEmail = "explorer@wordexplorer.app"
        val existing = userDao.getUserByEmail(demoEmail)
        if (existing != null) return existing

        val demoUser = UserEntity(
            id = "demo_explorer_user",
            email = demoEmail,
            displayName = "Arka Explorer",
            passwordHash = hashPassword("adventure123")
        )
        userDao.insertUser(demoUser)
        val profile = UserProfileEntity(
            userId = demoUser.id,
            onboardingCompleted = true,
            diagnosticScore = 6,
            initialPath = LearningPath.LOW.code,
            currentPath = LearningPath.LOW.code,
            dailyTarget = 10,
            currentStreak = 7,
            bestStreak = 7,
            lastStudyDate = dateFormat.format(Date()),
            activeCompanionId = "parrot"
        )
        userDao.insertOrUpdateProfile(profile)
        initializePathLevels(demoUser.id, LearningPath.LOW)

        // Mark 1-1 and 1-2 completed for a lively first experience
        progressDao.insertLevelProgress(
            LevelProgressEntity(
                id = "${demoUser.id}_LOW_1-1",
                userId = demoUser.id,
                path = "LOW",
                levelId = "1-1",
                stageNumber = 1,
                levelNumber = 1,
                status = LevelStatus.COMPLETED.name,
                stars = 3,
                firstAttemptScore = 10,
                completedAt = System.currentTimeMillis()
            )
        )
        progressDao.insertLevelProgress(
            LevelProgressEntity(
                id = "${demoUser.id}_LOW_1-2",
                userId = demoUser.id,
                path = "LOW",
                levelId = "1-2",
                stageNumber = 1,
                levelNumber = 2,
                status = LevelStatus.AVAILABLE.name,
                stars = 0,
                firstAttemptScore = 0
            )
        )
        return demoUser
    }

    suspend fun resetPassword(email: String, newPass: String): Result<Unit> {
        val user = userDao.getUserByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("Email not registered."))
        userDao.insertUser(user.copy(passwordHash = hashPassword(newPass)))
        return Result.success(Unit)
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        return userDao.observeUserProfile(userId).map { entity ->
            entity?.let {
                UserProfile(
                    id = it.userId,
                    email = "",
                    displayName = "",
                    initialPath = LearningPath.valueOf(it.initialPath),
                    currentPath = LearningPath.valueOf(it.currentPath),
                    dailyTarget = it.dailyTarget,
                    currentStreak = it.currentStreak,
                    bestStreak = it.bestStreak,
                    activeCompanionId = it.activeCompanionId,
                    lastStudyDate = it.lastStudyDate,
                    onboardingCompleted = it.onboardingCompleted,
                    diagnosticScore = it.diagnosticScore
                )
            }
        }
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        val entity = userDao.getUserProfile(userId) ?: return null
        val user = userDao.getUserById(userId)
        return UserProfile(
            id = entity.userId,
            email = user?.email ?: "",
            displayName = user?.displayName ?: "Explorer",
            initialPath = LearningPath.valueOf(entity.initialPath),
            currentPath = LearningPath.valueOf(entity.currentPath),
            dailyTarget = entity.dailyTarget,
            currentStreak = entity.currentStreak,
            bestStreak = entity.bestStreak,
            activeCompanionId = entity.activeCompanionId,
            lastStudyDate = entity.lastStudyDate,
            onboardingCompleted = entity.onboardingCompleted,
            diagnosticScore = entity.diagnosticScore
        )
    }

    suspend fun completeOnboardingAndDiagnostic(userId: String, diagnosticScore: Int, assignedPath: LearningPath) {
        val currentProfile = userDao.getUserProfile(userId) ?: UserProfileEntity(userId)
        val updated = currentProfile.copy(
            onboardingCompleted = true,
            diagnosticScore = diagnosticScore,
            initialPath = assignedPath.code,
            currentPath = assignedPath.code,
            dailyTarget = assignedPath.targetWordsPerDay
        )
        userDao.insertOrUpdateProfile(updated)
        initializePathLevels(userId, assignedPath)
    }

    suspend fun switchLearningPath(userId: String, newPath: LearningPath) {
        val currentProfile = userDao.getUserProfile(userId) ?: return
        val updated = currentProfile.copy(
            currentPath = newPath.code,
            dailyTarget = newPath.targetWordsPerDay
        )
        userDao.insertOrUpdateProfile(updated)
        initializePathLevels(userId, newPath)
    }

    suspend fun setActiveCompanion(userId: String, companionId: String) {
        val currentProfile = userDao.getUserProfile(userId) ?: return
        userDao.insertOrUpdateProfile(currentProfile.copy(activeCompanionId = companionId))
    }

    // --- Level Progression & Unlocking Rules ---

    private suspend fun initializePathLevels(userId: String, path: LearningPath) {
        val existing = progressDao.getProgressForPath(userId, path.code)
        if (existing.isEmpty()) {
            val list = mutableListOf<LevelProgressEntity>()
            for (stage in 1..5) {
                for (lvl in 1..10) {
                    val levelId = "$stage-$lvl"
                    // Only 1-1 starts unlocked (AVAILABLE)
                    val status = if (stage == 1 && lvl == 1) LevelStatus.AVAILABLE else LevelStatus.LOCKED
                    list.add(
                        LevelProgressEntity(
                            id = "${userId}_${path.code}_$levelId",
                            userId = userId,
                            path = path.code,
                            levelId = levelId,
                            stageNumber = stage,
                            levelNumber = lvl,
                            status = status.name,
                            stars = 0,
                            firstAttemptScore = 0
                        )
                    )
                }
            }
            progressDao.insertLevelProgressList(list)
        }
    }

    fun observeLevels(userId: String, path: LearningPath): Flow<List<LevelInfo>> {
        val coords = InitialDataSeed.getLevelCoordinates()
        return progressDao.observeProgressForPath(userId, path.code).map { list ->
            val progressMap = list.associateBy { it.levelId }
            val levelInfos = mutableListOf<LevelInfo>()
            var idx = 0
            for (stage in 1..5) {
                for (lvl in 1..10) {
                    val levelId = "$stage-$lvl"
                    val progress = progressMap[levelId]
                    val coord = coords.getOrElse(idx) { Pair(800f, 1300f) }
                    val targetVocabs = InitialDataSeed.INITIAL_VOCABULARY
                        .drop((idx * 2) % 30)
                        .take(3)
                        .map { it.word }

                    levelInfos.add(
                        LevelInfo(
                            id = levelId,
                            stageNumber = stage,
                            levelNumber = lvl,
                            title = "Stage $stage-$lvl: ${InitialDataSeed.STAGES[stage - 1].name}",
                            path = path,
                            posX = coord.first,
                            posY = coord.second,
                            targetWords = targetVocabs,
                            status = progress?.let { LevelStatus.valueOf(it.status) }
                                ?: if (stage == 1 && lvl == 1) LevelStatus.AVAILABLE else LevelStatus.LOCKED,
                            stars = progress?.stars ?: 0,
                            bestScore = progress?.firstAttemptScore ?: 0
                        )
                    )
                    idx++
                }
            }
            levelInfos
        }
    }

    suspend fun getRemedials(userId: String, path: LearningPath, levelId: String): List<RemedialEntity> {
        return progressDao.getRemedialsForLevel(userId, path.code, levelId)
    }

    suspend fun recordFirstAttemptAndCheckRemedials(
        userId: String,
        path: LearningPath,
        stage: Int,
        level: Int,
        firstAttemptScore: Int,
        failedQuestions: List<Question>
    ): Pair<Int, List<RemedialTask>> {
        val levelId = "$stage-$level"

        // Calculate stars strictly based on the first attempt!
        // 3 stars = 10/10 perfect!
        // 2 stars = 7..9
        // 1 star = 5..6
        // 0 stars = <5
        val stars = when (firstAttemptScore) {
            10 -> 3
            in 7..9 -> 2
            in 5..6 -> 1
            else -> 0
        }

        // Generate remedial obligations = exact number of wrong answers from first attempt
        val remedialTasks = failedQuestions.map { failedQ ->
            RemedialTask(
                id = "${userId}_${path.code}_${levelId}_${failedQ.id}",
                originalQuestionId = failedQ.id,
                failedWord = failedQ.targetWord,
                question = InitialDataSeed.generateRemedialQuestion(failedQ, path),
                isResolved = false
            )
        }

        if (remedialTasks.isNotEmpty()) {
            val entities = remedialTasks.map { task ->
                RemedialEntity(
                    id = task.id,
                    userId = userId,
                    path = path.code,
                    levelId = levelId,
                    questionId = task.originalQuestionId,
                    targetWord = task.failedWord,
                    isResolved = false
                )
            }
            progressDao.insertRemedials(entities)
        } else {
            // Perfect 10/10! Complete level immediately and unlock next level
            markLevelCompletedAndUnlockNext(userId, path, stage, level, stars, firstAttemptScore)
        }

        return Pair(stars, remedialTasks)
    }

    suspend fun resolveRemedialItem(
        userId: String,
        path: LearningPath,
        stage: Int,
        level: Int,
        questionId: String,
        firstAttemptStars: Int,
        firstAttemptScore: Int
    ): Boolean {
        val levelId = "$stage-$level"
        progressDao.resolveRemedial(userId, path.code, levelId, questionId)
        val allRemedials = progressDao.getRemedialsForLevel(userId, path.code, levelId)
        val allResolved = allRemedials.all { it.isResolved }
        if (allResolved) {
            // All remedial obligations cleared! Complete level now.
            markLevelCompletedAndUnlockNext(userId, path, stage, level, firstAttemptStars, firstAttemptScore)
            return true
        }
        return false
    }

    private suspend fun markLevelCompletedAndUnlockNext(
        userId: String,
        path: LearningPath,
        stage: Int,
        level: Int,
        stars: Int,
        score: Int
    ) {
        val levelId = "$stage-$level"
        val existingProgress = progressDao.getLevelProgress(userId, path.code, levelId)
        val bestStars = maxOf(stars, existingProgress?.stars ?: 0)
        val bestScore = maxOf(score, existingProgress?.firstAttemptScore ?: 0)

        // Mark current level COMPLETED
        progressDao.insertLevelProgress(
            LevelProgressEntity(
                id = "${userId}_${path.code}_$levelId",
                userId = userId,
                path = path.code,
                levelId = levelId,
                stageNumber = stage,
                levelNumber = level,
                status = LevelStatus.COMPLETED.name,
                stars = bestStars,
                firstAttemptScore = bestScore,
                completedAt = System.currentTimeMillis()
            )
        )

        // Unlock next level (or next stage level 1)
        if (level < 10) {
            val nextLevelId = "$stage-${level + 1}"
            val nextProgress = progressDao.getLevelProgress(userId, path.code, nextLevelId)
            if (nextProgress == null || nextProgress.status == LevelStatus.LOCKED.name) {
                progressDao.insertLevelProgress(
                    LevelProgressEntity(
                        id = "${userId}_${path.code}_$nextLevelId",
                        userId = userId,
                        path = path.code,
                        levelId = nextLevelId,
                        stageNumber = stage,
                        levelNumber = level + 1,
                        status = LevelStatus.AVAILABLE.name,
                        stars = 0,
                        firstAttemptScore = 0
                    )
                )
            }
        } else if (stage < 5) {
            // Stage completed! Unlock next stage level 1 (e.g. 2-1)
            val nextStageLevel1 = "${stage + 1}-1"
            val nextProgress = progressDao.getLevelProgress(userId, path.code, nextStageLevel1)
            if (nextProgress == null || nextProgress.status == LevelStatus.LOCKED.name) {
                progressDao.insertLevelProgress(
                    LevelProgressEntity(
                        id = "${userId}_${path.code}_$nextStageLevel1",
                        userId = userId,
                        path = path.code,
                        levelId = nextStageLevel1,
                        stageNumber = stage + 1,
                        levelNumber = 1,
                        status = LevelStatus.AVAILABLE.name,
                        stars = 0,
                        firstAttemptScore = 0
                    )
                )
            }
        }

        // Advance daily streak
        updateStudyStreak(userId)
    }

    private suspend fun updateStudyStreak(userId: String) {
        val profile = userDao.getUserProfile(userId) ?: return
        val today = dateFormat.format(Date())
        if (profile.lastStudyDate != today) {
            val newStreak = profile.currentStreak + 1
            val newBest = maxOf(newStreak, profile.bestStreak)
            userDao.insertOrUpdateProfile(
                profile.copy(
                    currentStreak = newStreak,
                    bestStreak = newBest,
                    lastStudyDate = today
                )
            )
        }
    }

    // --- Vocabulary Database ---

    suspend fun prepopulateVocabularyIfEmpty() {
        val count = vocabularyDao.getCount()
        if (count == 0) {
            vocabularyDao.insertAll(InitialDataSeed.INITIAL_VOCABULARY)
        }
    }

    fun observeVocabulary(searchQuery: String): Flow<List<VocabularyItem>> {
        val flow = if (searchQuery.isBlank()) {
            vocabularyDao.observeAllVocabulary()
        } else {
            vocabularyDao.searchVocabulary(searchQuery.trim())
        }
        return flow.map { list ->
            list.map {
                VocabularyItem(
                    id = it.id,
                    word = it.word,
                    indonesian = it.indonesian,
                    englishDef = it.englishDef,
                    partOfSpeech = it.partOfSpeech,
                    pronunciation = it.pronunciation,
                    exampleSentence = it.exampleSentence,
                    exampleTranslation = it.exampleTranslation,
                    cefr = it.cefr,
                    category = it.category,
                    isLearned = it.isLearned
                )
            }
        }
    }

    suspend fun getVocabularyCount(): Int = vocabularyDao.getCount()

    suspend fun toggleVocabularyLearned(vocabId: String, currentLearned: Boolean) {
        vocabularyDao.updateLearned(vocabId, !currentLearned)
    }

    // --- Adventure Stories ---

    fun getStories(): List<AdventureStory> = InitialDataSeed.ADVENTURE_STORIES

    fun observeStoryCompletions(userId: String): Flow<Set<String>> {
        return progressDao.observeStoryProgress(userId).map { list ->
            list.filter { it.isCompleted }.map { it.storyId }.toSet()
        }
    }

    suspend fun completeStory(userId: String, storyId: String) {
        progressDao.setStoryProgress(
            StoryProgressEntity(
                id = "${userId}_$storyId",
                userId = userId,
                storyId = storyId,
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            )
        )
        updateStudyStreak(userId)
    }

    // --- Animal Companions ---

    fun getCompanions(currentStreak: Int): List<CompanionAnimal> {
        return InitialDataSeed.COMPANIONS.map {
            it.copy(isUnlocked = currentStreak >= it.requiredStreak)
        }
    }
}
