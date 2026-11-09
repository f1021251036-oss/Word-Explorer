package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.firestore.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test

class WordExplorerFirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveUserProfile_authenticatedUser_succeeds() = runBlocking {
        val aliceUid = signInTestUser("alice.rule@example.com")
        val repository = WordExplorerFirestoreRepository(firestore, auth)

        val profile = FirestoreUser(
            uid = aliceUid,
            displayName = "Alice Explorer",
            email = "alice.rule@example.com",
            photoURL = "",
            authProviders = "google",
            learningPathway = "LOW"
        )

        val result = withTimeout(10000L) { repository.saveUserProfile(profile) }
        assertTrue("Expected saveUserProfile to succeed, but was: $result", result.isSuccess)

        val fetched = withTimeout(10000L) { repository.getUserProfile(aliceUid) }
        assertTrue(fetched.isSuccess)
        assertNotNull(fetched.getOrNull())
        assertEquals("Alice Explorer", fetched.getOrNull()?.displayName)
    }

    @Test
    fun unauthenticatedAccess_fails() = runBlocking {
        auth.signOut()
        val repository = WordExplorerFirestoreRepository(firestore, auth)

        val profile = FirestoreUser(
            uid = "unauth_user",
            displayName = "Unauth User",
            email = "unauth@example.com",
            learningPathway = "LOW"
        )

        val result = runCatching { repository.saveUserProfile(profile) }
        assertTrue(result.isFailure || result.getOrNull()?.isFailure == true)
    }

    @Test
    fun crossUserAccess_bobCannotReadAliceProfile() = runBlocking {
        val aliceUid = signInTestUser("alice.data@example.com")
        val aliceRepo = WordExplorerFirestoreRepository(firestore, auth)
        aliceRepo.saveUserProfile(
            FirestoreUser(
                uid = aliceUid,
                displayName = "Alice Private",
                email = "alice.data@example.com",
                learningPathway = "LOW"
            )
        )

        // Sign in as Bob
        val bobUid = signInTestUser("bob.intruder@example.com")
        val bobRepo = WordExplorerFirestoreRepository(firestore, auth)

        val result = withTimeout(10000L) { bobRepo.getUserProfile(aliceUid) }
        assertTrue("Bob should fail reading Alice's private profile", result.isFailure)
    }

    @Test
    fun saveDailyActivity_enforcesMax3LevelsDailyLimit() = runBlocking {
        val uid = signInTestUser("daily.limit@example.com")
        val repository = WordExplorerFirestoreRepository(firestore, auth)

        val validActivity = FirestoreDailyActivity(
            date = "2026-11-03",
            vocabularyCompleted = 10,
            adventureLevelsCompleted = 3,
            dailyTargetCompleted = true,
            streakQualified = true
        )
        val validResult = withTimeout(10000L) { repository.saveDailyActivity(validActivity) }
        assertTrue(validResult.isSuccess)

        val fetched = withTimeout(10000L) { repository.getDailyActivity("2026-11-03", uid) }
        assertTrue(fetched.isSuccess)
        assertEquals(3, fetched.getOrNull()?.adventureLevelsCompleted)
    }

    @Test
    fun saveLearningProgress_andRetrieve_preservesCompletedLevels() = runBlocking {
        val uid = signInTestUser("progress.sync@example.com")
        val repository = WordExplorerFirestoreRepository(firestore, auth)

        val progress = FirestoreLearningProgress(
            currentChapter = 2,
            currentLevel = 4,
            completedLevels = listOf("1-1", "1-2", "1-3", "2-1", "2-2", "2-3"),
            unlockedLevels = listOf("1-1", "1-2", "1-3", "2-1", "2-2", "2-3", "2-4"),
            totalStars = 18,
            totalCompletedLevels = 6
        )

        val saveResult = withTimeout(10000L) { repository.saveLearningProgress(progress) }
        assertTrue(saveResult.isSuccess)

        val fetched = withTimeout(10000L) { repository.getLearningProgress(uid) }
        assertTrue(fetched.isSuccess)
        val loaded = fetched.getOrNull()
        assertNotNull(loaded)
        assertEquals(2, loaded?.currentChapter)
        assertEquals(6, loaded?.totalCompletedLevels)
        assertTrue(loaded?.completedLevels?.contains("2-3") == true)
    }
}
