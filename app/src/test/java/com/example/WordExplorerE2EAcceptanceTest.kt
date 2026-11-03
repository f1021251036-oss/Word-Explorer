package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.models.LearningPath
import com.example.data.models.LevelStatus
import com.example.data.repository.WordExplorerRepository
import com.example.data.seed.InitialDataSeed
import com.example.data.seed.LevelTopicCatalog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WordExplorerE2EAcceptanceTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: WordExplorerRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = WordExplorerRepository(
            userDao = database.userDao(),
            progressDao = database.progressDao(),
            vocabularyDao = database.vocabularyDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ==================== PART 1 ACCEPTANCE TESTS ====================

    @Test
    fun `test Scenario 1 to 4 - Email registration with OTP and session verification`() = runBlocking {
        repository.prepopulateVocabularyIfEmpty()
        val email = "student.explorer@gmail.com"
        val password = "securePassword123"

        // 1. Register with Gmail
        val regResult = repository.registerUser(email, "Budi Explorer", password)
        assertTrue(regResult.isSuccess)
        val (user, otpCode) = regResult.getOrThrow()
        assertEquals(email, user.email)
        assertFalse(user.isEmailVerified)
        assertEquals(6, otpCode.length)

        // 2. Incorrect OTP must fail and increment attempts
        val wrongResult = repository.verifyEmailCode(email, "000000")
        assertTrue(wrongResult.isFailure)
        val userAfterWrong = database.userDao().getUserByEmail(email)
        assertEquals(1, userAfterWrong?.otpAttempts)

        // 3. Correct OTP verifies the account
        val verifyResult = repository.verifyEmailCode(email, otpCode)
        assertTrue(verifyResult.isSuccess)
        val verifiedUser = verifyResult.getOrThrow()
        assertTrue(verifiedUser.isEmailVerified)

        // 4. Session is automatically restored
        val activeSessionUser = repository.restoreUserSession()
        assertNotNull(activeSessionUser)
        assertEquals(verifiedUser.id, activeSessionUser?.id)

        // 5. Logout clears active session while keeping progress intact
        repository.clearUserSession()
        val clearedSession = repository.restoreUserSession()
        assertNull(clearedSession)
        val persistedUser = database.userDao().getUserByEmail(email)
        assertNotNull(persistedUser)
        assertTrue(persistedUser!!.isEmailVerified)
    }

    @Test
    fun `test Scenario 5 to 8 - Canonical Account Linking with Google Sign-In preserves progress`() = runBlocking {
        repository.prepopulateVocabularyIfEmpty()
        val email = "siti.adventurer@gmail.com"
        val password = "password456"

        // Step A: Register via Email/Password & verify
        val regResult = repository.registerUser(email, "Siti Explorer", password)
        val (_, otp) = regResult.getOrThrow()
        repository.verifyEmailCode(email, otp)
        val initialUser = database.userDao().getUserByEmail(email)!!
        val canonicalUserId = initialUser.id

        // Complete level 1-1 with 3 stars
        val failedQuestions = emptyList<com.example.data.models.Question>()
        repository.recordFirstAttemptAndCheckRemedials(
            userId = canonicalUserId,
            path = LearningPath.LOW,
            stage = 1,
            level = 1,
            firstAttemptScore = 10,
            failedQuestions = failedQuestions
        )

        val levelsBefore = repository.observeLevels(canonicalUserId, LearningPath.LOW).first()
        val level11Before = levelsBefore.find { it.id == "1-1" }
        assertEquals(LevelStatus.COMPLETED, level11Before?.status)
        assertEquals(3, level11Before?.stars)

        // Step B: Sign in with Google using the same verified Gmail address
        val googleId = "google_sub_1092837465241"
        val googleResult = repository.handleGoogleSignIn(
            googleId = googleId,
            email = email,
            displayName = "Siti Google Verified"
        )
        assertTrue(googleResult.isSuccess)
        val linkedUser = googleResult.getOrThrow()

        // Verify: ONE Canonical Account, no duplicate created!
        assertEquals(canonicalUserId, linkedUser.id)
        assertEquals(googleId, linkedUser.googleId)
        assertTrue(linkedUser.authProviders.contains("google"))
        assertTrue(linkedUser.authProviders.contains("email_password"))

        // Verify: All previous level progress, stars, and records are preserved intact!
        val levelsAfter = repository.observeLevels(canonicalUserId, LearningPath.LOW).first()
        val level11After = levelsAfter.find { it.id == "1-1" }
        assertEquals(LevelStatus.COMPLETED, level11After?.status)
        assertEquals(3, level11After?.stars)
    }

    // ==================== PART 2 ACCEPTANCE TESTS ====================

    @Test
    fun `test Daily Adventure Level Limit - Maximum 3 levels per day and 17 days minimum`() = runBlocking {
        repository.prepopulateVocabularyIfEmpty()
        val regResult = repository.registerUser("daily.limit@gmail.com", "Test Student", "pass123")
        val (user, otp) = regResult.getOrThrow()
        repository.verifyEmailCode(user.email, otp)
        val today = repository.getTodayDateString()

        assertEquals(true, repository.canStartNewAdventureLevel(user.id, today))

        // Complete level 1 (Day's 1st completion)
        repository.recordFirstAttemptAndCheckRemedials(user.id, LearningPath.LOW, 1, 1, 10, emptyList())
        // Complete level 2 (Day's 2nd completion)
        repository.recordFirstAttemptAndCheckRemedials(user.id, LearningPath.LOW, 1, 2, 10, emptyList())
        // Complete level 3 (Day's 3rd completion)
        repository.recordFirstAttemptAndCheckRemedials(user.id, LearningPath.LOW, 1, 3, 10, emptyList())

        val countToday = repository.getDailyCompletedLevelsCount(user.id, today)
        assertEquals(3, countToday)

        // Can student start a 4th new level today? STRICTLY FALSE!
        val canStartFourth = repository.canStartNewAdventureLevel(user.id, today)
        assertFalse(canStartFourth)

        // Calculation: 50 total levels / 3 max per day = 16 days (48 levels) + 1 day (2 levels) = 17 days minimum!
        val totalLevels = 50
        val maxPerDay = 3
        val minDays = Math.ceil(totalLevels.toDouble() / maxPerDay).toInt()
        assertEquals(17, minDays)
    }

    @Test
    fun `test Daily Vocabulary Rotation - Words from yesterday do not repeat as new today`() = runBlocking {
        repository.prepopulateVocabularyIfEmpty()
        val user = repository.quickLoginDemo()

        val day1 = "2026-11-03"
        val day2 = "2026-11-04"

        // Day 1 Daily Set
        val day1Vocab = repository.getOrCreateDailyVocabSet(user.id, LearningPath.LOW, day1)
        assertEquals(25, day1Vocab.size)
        val day1Ids = day1Vocab.map { it.vocabId }.toSet()

        // Mark all Day 1 vocabularies as MASTERED / LEARNED
        day1Vocab.forEach {
            repository.updateDailyVocabStatus(user.id, it.vocabId, "MASTERED", day1)
        }

        // Retain same set when returning on same day (Day 1)
        val day1Return = repository.getOrCreateDailyVocabSet(user.id, LearningPath.LOW, day1)
        assertEquals(day1Ids, day1Return.map { it.vocabId }.toSet())

        // Day 2 Daily Set
        val day2Vocab = repository.getOrCreateDailyVocabSet(user.id, LearningPath.LOW, day2)
        assertEquals(25, day2Vocab.size)
        val day2Ids = day2Vocab.map { it.vocabId }.toSet()

        // Rule: Words learned on previous day must NOT be repeated as new on next day!
        val overlap = day1Ids.intersect(day2Ids)
        assertTrue("Day 2 new vocabulary must not overlap with Day 1 learned words!", overlap.isEmpty())
    }

    @Test
    fun `test 50 Distinct Level Topics and 10 Questions Variety`() {
        // Exactly 50 topics defined in LevelTopicCatalog
        assertEquals(50, LevelTopicCatalog.TOPICS.size)

        // Every single level (1-1 to 5-10) has a unique topic title
        val topicTitles = LevelTopicCatalog.TOPICS.map { it.topicTitle }
        assertEquals(50, topicTitles.distinct().size)

        // Check that each level produces 10 questions utilizing varied formats
        for (stage in 1..5) {
            for (lvl in 1..10) {
                val questions = LevelTopicCatalog.generateTenQuestionsForLevel(LearningPath.MIDDLE, stage, lvl)
                assertEquals(10, questions.size)

                // Verify questions test different words (not repeating the same word 10 times)
                val targetWords = questions.map { it.targetWord }
                assertTrue(targetWords.distinct().size >= 5)

                // Verify multiple exercise types are present in every level
                val types = questions.map { it.type }.distinct()
                assertTrue(types.size >= 4)
            }
        }
    }
}
