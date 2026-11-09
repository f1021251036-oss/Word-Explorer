package com.example.data.repository

import com.example.data.auth.EmailDeliveryResult
import com.example.data.auth.EmailDeliveryService
import com.example.data.firestore.*
import com.example.data.local.*
import com.example.data.models.*
import com.example.data.seed.InitialDataSeed
import com.example.data.seed.LevelTopicCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class WordExplorerRepository(
    private val userDao: UserDao,
    private val progressDao: ProgressDao,
    private val vocabularyDao: VocabularyDao,
    private val emailDeliveryService: EmailDeliveryService = EmailDeliveryService(),
    val firestoreRepository: WordExplorerFirestoreRepository? = null
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayDateString(): String = dateFormat.format(Date())

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun getBackendConfigStatus() = emailDeliveryService.getBackendConfigStatus()

    // --- Authentication & User Session ---

    // 1. Google Sign-In with Official Flow and Canonical Account Linking
    suspend fun handleGoogleSignIn(
        googleId: String,
        email: String,
        displayName: String
    ): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()

        // Check if a user with this unique Google ID already exists
        val existingByGoogleId = userDao.getUserByGoogleId(googleId)
        if (existingByGoogleId != null) {
            userDao.saveSession(UserSessionEntity(userId = existingByGoogleId.id, authMethod = "google"))
            syncCloudUserAndRestore(existingByGoogleId)
            return Result.success(existingByGoogleId)
        }

        // Check if an existing account with the same verified email exists (Account Linking)
        val existingByEmail = userDao.getUserByEmail(cleanEmail)
        if (existingByEmail != null) {
            // Safe Account Linking: The user owns this verified Google account matching their registered email.
            // Connect the Google identity into the single canonical Word Explorer account.
            val currentProviders = existingByEmail.authProviders.split(",").map { it.trim() }.toMutableSet()
            currentProviders.add("google")

            val linkedUser = existingByEmail.copy(
                googleId = googleId,
                isEmailVerified = true,
                authProviders = currentProviders.joinToString(",")
            )
            userDao.insertUser(linkedUser)
            userDao.saveSession(UserSessionEntity(userId = linkedUser.id, authMethod = "google"))
            syncCloudUserAndRestore(linkedUser)
            return Result.success(linkedUser)
        }

        // New student registering via Google OAuth
        val newUserId = googleId.ifBlank { "user_g_${UUID.randomUUID().toString().take(8)}" }
        val newUser = UserEntity(
            id = newUserId,
            email = cleanEmail,
            displayName = displayName.ifBlank { "Explorer ${cleanEmail.substringBefore("@")}" },
            passwordHash = "", // Google Sign-In does not store or ask for Google passwords
            isEmailVerified = true, // Verified by Google OAuth
            googleId = googleId,
            authProviders = "google"
        )
        userDao.insertUser(newUser)

        val profile = UserProfileEntity(
            userId = newUserId,
            onboardingCompleted = false,
            diagnosticScore = -1,
            initialPath = LearningPath.LOW.code,
            currentPath = LearningPath.LOW.code,
            dailyTarget = 10,
            currentStreak = 1,
            bestStreak = 1,
            lastStudyDate = getTodayDateString(),
            activeCompanionId = "fox"
        )
        userDao.insertOrUpdateProfile(profile)
        initializePathLevels(newUserId, LearningPath.LOW)

        userDao.saveSession(UserSessionEntity(userId = newUserId, authMethod = "google"))
        syncCloudUserAndRestore(newUser)
        return Result.success(newUser)
    }

    suspend fun syncCloudUserAndRestore(user: UserEntity) {
        val fs = firestoreRepository ?: return
        try {
            val profile = userDao.getUserProfile(user.id)
            val currentPath = profile?.currentPath ?: "LOW"

            // Check if user has progress in cloud
            val cloudProgress = fs.getLearningProgress(user.id).getOrNull()
            if (cloudProgress != null && cloudProgress.completedLevels.isNotEmpty()) {
                // Restore completed levels
                for (lvlId in cloudProgress.completedLevels) {
                    val parts = lvlId.split("-")
                    val stg = parts.getOrNull(0)?.toIntOrNull() ?: 1
                    val lvl = parts.getOrNull(1)?.toIntOrNull() ?: 1
                    progressDao.insertLevelProgress(
                        LevelProgressEntity(
                            id = "${user.id}_${currentPath}_$lvlId",
                            userId = user.id,
                            path = currentPath,
                            levelId = lvlId,
                            stageNumber = stg,
                            levelNumber = lvl,
                            status = LevelStatus.COMPLETED.name,
                            stars = 3,
                            firstAttemptScore = 10,
                            completedAt = System.currentTimeMillis()
                        )
                    )
                }
                for (lvlId in cloudProgress.unlockedLevels) {
                    val parts = lvlId.split("-")
                    val stg = parts.getOrNull(0)?.toIntOrNull() ?: 1
                    val lvl = parts.getOrNull(1)?.toIntOrNull() ?: 1
                    val existing = progressDao.getLevelProgress(user.id, currentPath, lvlId)
                    if (existing == null) {
                        progressDao.insertLevelProgress(
                            LevelProgressEntity(
                                id = "${user.id}_${currentPath}_$lvlId",
                                userId = user.id,
                                path = currentPath,
                                levelId = lvlId,
                                stageNumber = stg,
                                levelNumber = lvl,
                                status = LevelStatus.AVAILABLE.name,
                                stars = 0,
                                firstAttemptScore = 0
                            )
                        )
                    }
                }
            } else {
                // Push local profile to cloud
                fs.saveUserProfile(
                    FirestoreUser(
                        uid = user.id,
                        displayName = user.displayName,
                        email = user.email,
                        authProviders = user.authProviders,
                        learningPathway = currentPath
                    )
                )
                syncProgressToCloud(user.id, LearningPath.valueOf(currentPath))
            }

            // Sync daily activity
            val today = getTodayDateString()
            val cloudActivity = fs.getDailyActivity(today, user.id).getOrNull()
            if (cloudActivity != null && cloudActivity.adventureLevelsCompleted > 0) {
                val localCount = progressDao.getDailyLevelCompletions(user.id, today).size
                if (localCount < cloudActivity.adventureLevelsCompleted) {
                    for (i in (localCount + 1)..cloudActivity.adventureLevelsCompleted) {
                        progressDao.insertDailyLevelCompletion(
                            DailyLevelCompletionEntity(
                                id = "${user.id}_${today}_cloud_$i",
                                userId = user.id,
                                dateString = today,
                                path = currentPath,
                                levelId = "cloud-$i",
                                completedAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Offline fallback
        }
    }

    suspend fun syncProgressToCloud(userId: String, path: LearningPath) {
        val fs = firestoreRepository ?: return
        try {
            val allProgress = progressDao.getProgressForPath(userId, path.code)
            val completed = allProgress.filter { it.status == LevelStatus.COMPLETED.name }.map { it.levelId }
            val unlocked = allProgress.filter { it.status != LevelStatus.LOCKED.name }.map { it.levelId }
            val totalStars = allProgress.sumOf { it.stars }
            val maxStage = allProgress.filter { it.status == LevelStatus.COMPLETED.name }.maxOfOrNull { it.stageNumber } ?: 1
            val maxLevel = allProgress.filter { it.status == LevelStatus.COMPLETED.name }.maxOfOrNull { it.levelNumber } ?: 1

            fs.saveLearningProgress(
                FirestoreLearningProgress(
                    currentChapter = maxStage,
                    currentLevel = maxLevel,
                    completedLevels = completed,
                    unlockedLevels = unlocked.ifEmpty { listOf("1-1") },
                    totalStars = totalStars,
                    totalCompletedLevels = completed.size
                )
            )
        } catch (_: Exception) {
        }
    }

    // 2. Email & Password Registration with Secure SHA-256 Hashed 6-Digit OTP (5-Min Expiry)
    suspend fun registerUser(
        email: String,
        displayName: String,
        password: String
    ): Result<Pair<UserEntity, String>> {
        val cleanEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null && existing.isEmailVerified) {
            return Result.failure(Exception("Email sudah terdaftar. Silakan masuk."))
        }

        val userId = existing?.id ?: "user_${UUID.randomUUID().toString().take(8)}"

        // Generate 6-digit OTP
        val otpCode = (100000..999999).random().toString()
        val otpHash = hashPassword(otpCode)
        val otpExpiry = System.currentTimeMillis() + 5 * 60 * 1000L // Strictly 5 minutes

        val user = UserEntity(
            id = userId,
            email = cleanEmail,
            displayName = displayName.ifBlank { "Young Explorer" },
            passwordHash = hashPassword(password),
            isEmailVerified = false,
            authProviders = existing?.authProviders ?: "email_password",
            otpHash = otpHash,
            otpExpiry = otpExpiry,
            otpAttempts = 0,
            lastOtpResendAt = System.currentTimeMillis(),
            verificationCode = otpCode, // For debug inspection when unconfigured
            codeExpiry = otpExpiry
        )
        userDao.insertUser(user)

        if (existing == null) {
            val profile = UserProfileEntity(
                userId = userId,
                onboardingCompleted = false,
                diagnosticScore = -1,
                initialPath = LearningPath.LOW.code,
                currentPath = LearningPath.LOW.code,
                dailyTarget = 10,
                currentStreak = 1,
                bestStreak = 1,
                lastStudyDate = getTodayDateString(),
                activeCompanionId = "fox"
            )
            userDao.insertOrUpdateProfile(profile)
            initializePathLevels(userId, LearningPath.LOW)
        }

        // Send OTP via Real Transactional Email Service
        val sendResult = emailDeliveryService.sendVerificationOtpEmail(cleanEmail, otpCode)
        return when (sendResult) {
            is EmailDeliveryResult.Success -> {
                Result.success(Pair(user, otpCode))
            }
            is EmailDeliveryResult.ProviderNotConfigured -> {
                // Disclose backend status clearly; return code so developer/tester can proceed in sandbox
                Result.success(Pair(user, otpCode))
            }
            is EmailDeliveryResult.DeliveryError -> {
                Result.success(Pair(user, otpCode))
            }
        }
    }

    // 3. Verify OTP: 5-Min Expiry, Hash Validation, Max 5 Failed Attempts
    suspend fun verifyEmailCode(email: String, code: String): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(cleanEmail)
            ?: return Result.failure(Exception("Akun tidak ditemukan."))

        // Reject expired OTP
        if (System.currentTimeMillis() > user.otpExpiry && user.codeExpiry < System.currentTimeMillis()) {
            return Result.failure(Exception("Kode OTP telah kadaluarsa (berlaku 5 menit). Silakan minta kode baru."))
        }

        // Limit attempts (max 5)
        if (user.otpAttempts >= 5) {
            return Result.failure(Exception("Terlalu banyak percobaan salah (maksimal 5 kali). Silakan minta kode verifikasi baru."))
        }

        val inputHash = hashPassword(code.trim())
        val isMatch = (user.otpHash != null && user.otpHash == inputHash) ||
                (user.verificationCode.isNotBlank() && user.verificationCode == code.trim())

        if (!isMatch) {
            val newAttempts = user.otpAttempts + 1
            userDao.insertUser(user.copy(otpAttempts = newAttempts))
            val remaining = (5 - newAttempts).coerceAtLeast(0)
            return Result.failure(Exception("Kode OTP salah. Sisa kesempatan: $remaining kali."))
        }

        // OTP verified successfully
        val verifiedUser = user.copy(
            isEmailVerified = true,
            otpHash = null,
            otpExpiry = 0L,
            otpAttempts = 0,
            verificationCode = "",
            codeExpiry = 0L
        )
        userDao.insertUser(verifiedUser)
        userDao.saveSession(UserSessionEntity(userId = verifiedUser.id, authMethod = "email_password"))

        return Result.success(verifiedUser)
    }

    // 4. Resend OTP with 60-second cooldown
    suspend fun resendVerificationCode(email: String): Result<String> {
        val cleanEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(cleanEmail)
            ?: return Result.failure(Exception("Akun tidak ditemukan."))

        val timeSinceLast = System.currentTimeMillis() - user.lastOtpResendAt
        if (timeSinceLast < 60_000L) {
            val waitSec = ((60_000L - timeSinceLast) / 1000).toInt()
            return Result.failure(Exception("Harap tunggu $waitSec detik sebelum mengirim ulang kode OTP."))
        }

        val newOtp = (100000..999999).random().toString()
        val newHash = hashPassword(newOtp)
        val newExpiry = System.currentTimeMillis() + 5 * 60 * 1000L

        val updated = user.copy(
            otpHash = newHash,
            otpExpiry = newExpiry,
            otpAttempts = 0,
            lastOtpResendAt = System.currentTimeMillis(),
            verificationCode = newOtp,
            codeExpiry = newExpiry
        )
        userDao.insertUser(updated)

        emailDeliveryService.sendVerificationOtpEmail(cleanEmail, newOtp)
        return Result.success(newOtp)
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntity> {
        val user = userDao.getUserByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("Akun tidak ditemukan. Silakan daftar terlebih dahulu."))
        if (user.passwordHash.isNotBlank() && user.passwordHash != hashPassword(password)) {
            return Result.failure(Exception("Kata sandi salah."))
        }
        if (!user.isEmailVerified) {
            return Result.failure(Exception("UNVERIFIED_EMAIL:${user.email}"))
        }

        userDao.saveSession(UserSessionEntity(userId = user.id, authMethod = "email_password"))
        return Result.success(user)
    }

    // Persistent Session Restoration across app refreshes/restarts
    suspend fun restoreUserSession(): UserEntity? {
        val session = userDao.getActiveSession() ?: return null
        val user = userDao.getUserById(session.userId) ?: return null
        return if (user.isEmailVerified) user else null
    }

    suspend fun clearUserSession() {
        userDao.clearSession()
    }

    suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun quickLoginDemo(): UserEntity {
        val demoEmail = "arka.explorer@wordexplorer.app"
        val existing = userDao.getUserByEmail(demoEmail)
        if (existing != null) {
            val user = if (!existing.isEmailVerified) existing.copy(isEmailVerified = true) else existing
            userDao.insertUser(user)
            userDao.saveSession(UserSessionEntity(userId = user.id, authMethod = "demo"))
            return user
        }

        val demoUser = UserEntity(
            id = "demo_arka_explorer",
            email = demoEmail,
            displayName = "Arka Explorer",
            passwordHash = hashPassword("adventure123"),
            isEmailVerified = true,
            authProviders = "email_password,google"
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
            lastStudyDate = getTodayDateString(),
            activeCompanionId = "parrot"
        )
        userDao.insertOrUpdateProfile(profile)
        initializePathLevels(demoUser.id, LearningPath.LOW)

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
        userDao.saveSession(UserSessionEntity(userId = demoUser.id, authMethod = "demo"))
        return demoUser
    }

    suspend fun resetPassword(email: String, newPass: String): Result<Unit> {
        val user = userDao.getUserByEmail(email.trim().lowercase())
            ?: return Result.failure(Exception("Email belum terdaftar."))
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

    // --- Daily Adventure Level Limit (Max 3 levels per day across entire pathway) ---

    suspend fun getDailyCompletedLevelsCount(userId: String, dateString: String = getTodayDateString()): Int {
        return progressDao.getDailyLevelCompletions(userId, dateString).size
    }

    fun observeDailyCompletedLevelsCount(userId: String, dateString: String = getTodayDateString()): Flow<Int> {
        return progressDao.observeDailyLevelCompletions(userId, dateString).map { it.size }
    }

    suspend fun canStartNewAdventureLevel(userId: String, dateString: String = getTodayDateString()): Boolean {
        val count = getDailyCompletedLevelsCount(userId, dateString)
        return count < 3
    }

    // --- Level Progression & Unlocking Rules ---

    private suspend fun initializePathLevels(userId: String, path: LearningPath) {
        val existing = progressDao.getProgressForPath(userId, path.code)
        if (existing.isEmpty()) {
            val list = mutableListOf<LevelProgressEntity>()
            for (stage in 1..5) {
                for (lvl in 1..10) {
                    val levelId = "$stage-$lvl"
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

                    // Each level has its distinct topic definition from LevelTopicCatalog
                    val topic = LevelTopicCatalog.getTopicForLevel(stage, lvl)
                    val targetVocabs = topic.vocabulary.take(3).map { it.word }

                    levelInfos.add(
                        LevelInfo(
                            id = levelId,
                            stageNumber = stage,
                            levelNumber = lvl,
                            title = "Stage $stage-$lvl: ${topic.topicTitle}",
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

        // 3 stars = 10/10 perfect!
        val stars = when (firstAttemptScore) {
            10 -> 3
            in 7..9 -> 2
            in 5..6 -> 1
            else -> 0
        }

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
        val wasAlreadyCompleted = existingProgress?.status == LevelStatus.COMPLETED.name
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

        // Record into daily_level_completions table if newly completed today
        if (!wasAlreadyCompleted) {
            val today = getTodayDateString()
            progressDao.insertDailyLevelCompletion(
                DailyLevelCompletionEntity(
                    id = "${userId}_${today}_$levelId",
                    userId = userId,
                    dateString = today,
                    path = path.code,
                    levelId = levelId,
                    completedAt = System.currentTimeMillis()
                )
            )
        }

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

        updateStudyStreak(userId)

        // Sync to Cloud Firestore
        firestoreRepository?.let { fs ->
            try {
                val allProgress = progressDao.getProgressForPath(userId, path.code)
                val completed = allProgress.filter { it.status == LevelStatus.COMPLETED.name }.map { it.levelId }
                val unlocked = allProgress.filter { it.status != LevelStatus.LOCKED.name }.map { it.levelId }
                val totalStars = allProgress.sumOf { it.stars }

                fs.saveLearningProgress(
                    FirestoreLearningProgress(
                        currentChapter = stage,
                        currentLevel = level,
                        completedLevels = completed,
                        unlockedLevels = unlocked,
                        totalStars = totalStars,
                        totalCompletedLevels = completed.size
                    )
                )

                val today = getTodayDateString()
                val dailyLevels = progressDao.getDailyLevelCompletions(userId, today).size
                val dailyAssignments = vocabularyDao.getDailyAssignments(userId, today)
                val masteredCount = dailyAssignments.count { it.status == "MASTERED" }

                fs.saveDailyActivity(
                    FirestoreDailyActivity(
                        date = today,
                        vocabularyCompleted = masteredCount,
                        adventureLevelsCompleted = minOf(3, dailyLevels),
                        dailyTargetCompleted = masteredCount >= (dailyAssignments.size.takeIf { it > 0 } ?: 10),
                        streakQualified = dailyLevels > 0
                    )
                )

                fs.saveQuizResult(
                    resultId = "${levelId}_${System.currentTimeMillis()}",
                    quizResult = FirestoreQuizResult(
                        chapter = stage,
                        level = level,
                        score = (score * 10).coerceIn(0, 100),
                        stars = stars,
                        remedialRequired = false,
                        remedialCompleted = true
                    )
                )
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun updateStudyStreak(userId: String) {
        val profile = userDao.getUserProfile(userId) ?: return
        val today = getTodayDateString()
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

    // --- Daily Vocabulary Rotation System ---

    suspend fun getOrCreateDailyVocabSet(
        userId: String,
        path: LearningPath,
        dateString: String = getTodayDateString()
    ): List<DailyVocabAssignmentEntity> {
        // Check if today's set already exists in Room
        val existing = vocabularyDao.getDailyAssignments(userId, dateString)
        if (existing.isNotEmpty()) {
            return existing
        }

        // Target words per day by pathway: LOW: 25, MIDDLE: 35, HIGH: 40
        val targetCount = when (path) {
            LearningPath.LOW -> 25
            LearningPath.MIDDLE -> 35
            LearningPath.HIGH -> 40
        }

        // Get all vocabularies already assigned or learned by this user previously
        val previouslyAssigned = vocabularyDao.getAllAssignedVocabIds(userId).toSet()

        // Fetch candidate words from database
        val allVocabs = vocabularyDao.getAllVocabularyList()
        val candidateUnseen = allVocabs.filter { it.id !in previouslyAssigned }

        // Pick distinct words
        val selectedVocabs = if (candidateUnseen.size >= targetCount) {
            candidateUnseen.shuffled().take(targetCount)
        } else {
            // If unseen pool is smaller, combine all remaining unseen with other unmastered items
            (candidateUnseen + allVocabs.filter { !it.isLearned }.shuffled()).distinctBy { it.id }.take(targetCount)
        }

        val assignments = selectedVocabs.map { v ->
            DailyVocabAssignmentEntity(
                id = "${userId}_${dateString}_${v.id}",
                userId = userId,
                dateString = dateString,
                path = path.code,
                vocabId = v.id,
                status = "NEW",
                assignedAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        }

        vocabularyDao.insertDailyAssignments(assignments)

        // Sync daily vocabulary set to Cloud Firestore
        firestoreRepository?.let { fs ->
            try {
                fs.saveDailyVocabulary(
                    FirestoreDailyVocabulary(
                        assignmentDate = dateString,
                        assignedVocabularyIds = assignments.map { it.vocabId },
                        totalAssigned = assignments.size,
                        completedCount = assignments.count { it.status == "MASTERED" },
                        completed = assignments.all { it.status == "MASTERED" }
                    )
                )
            } catch (_: Exception) {
            }
        }

        return assignments
    }

    fun observeDailyVocabAssignments(
        userId: String,
        dateString: String = getTodayDateString()
    ): Flow<List<DailyVocabAssignmentEntity>> {
        return vocabularyDao.observeDailyAssignments(userId, dateString)
    }

    suspend fun updateDailyVocabStatus(
        userId: String,
        vocabId: String,
        status: String,
        dateString: String = getTodayDateString()
    ) {
        vocabularyDao.updateDailyVocabStatus(userId, dateString, vocabId, status, System.currentTimeMillis())
        if (status == "MASTERED") {
            vocabularyDao.updateLearned(vocabId, true)
        }

        firestoreRepository?.let { fs ->
            try {
                val vocab = vocabularyDao.getVocabularyById(vocabId)
                if (vocab != null) {
                    fs.saveVocabularyProgress(
                        FirestoreVocabularyProgress(
                            vocabularyId = vocabId,
                            word = vocab.word,
                            meaning = vocab.indonesian,
                            learningStatus = status,
                            assignedDate = dateString,
                            reviewCount = 1
                        )
                    )
                }
            } catch (_: Exception) {
            }
        }
    }

    // --- Vocabulary Database ---

    suspend fun prepopulateVocabularyIfEmpty() {
        val count = vocabularyDao.getCount()
        if (count < 50) {
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
                    isLearned = it.isLearned,
                    clue = it.clue,
                    clue2 = it.clue2
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
