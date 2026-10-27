package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.LearningPath
import com.example.data.seed.InitialDataSeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Word Explorer", appName)
    }

    @Test
    fun `verify stage and level structure`() {
        // Exactly 5 stages
        assertEquals(5, InitialDataSeed.STAGES.size)

        // Exactly 50 coordinate nodes for 50 levels
        val coords = InitialDataSeed.getLevelCoordinates()
        assertEquals(50, coords.size)

        // 10 main questions generated per level
        val questions = InitialDataSeed.generateQuestionsForLevel(LearningPath.LOW, stage = 1, level = 1)
        assertEquals(10, questions.size)
    }

    @Test
    fun `verify learning paths access rules`() {
        assertTrue(LearningPath.LOW.canAccess(LearningPath.LOW))
        assertTrue(LearningPath.MIDDLE.canAccess(LearningPath.LOW))
        assertTrue(LearningPath.MIDDLE.canAccess(LearningPath.MIDDLE))
        assertTrue(LearningPath.HIGH.canAccess(LearningPath.LOW))
        assertTrue(LearningPath.HIGH.canAccess(LearningPath.MIDDLE))
        assertTrue(LearningPath.HIGH.canAccess(LearningPath.HIGH))
    }
}
