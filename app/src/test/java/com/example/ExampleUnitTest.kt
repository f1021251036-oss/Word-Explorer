package com.example

import com.example.data.seed.InitialDataSeed
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `verify exploration regions progression order`() {
        val regions = InitialDataSeed.EXPLORATION_REGIONS
        assertEquals(5, regions.size)

        // Forest -> Island -> Ancient Rome -> Mountain -> Cave
        assertEquals("Forest", regions[0].name)
        assertEquals("Island", regions[1].name)
        assertEquals("Ancient Rome", regions[2].name)
        assertEquals("Mountain", regions[3].name)
        assertEquals("Cave", regions[4].name)

        // Stage numbers 1 to 5
        for (i in 0 until 5) {
            assertEquals(i + 1, regions[i].stageNumber)
        }
    }

    @Test
    fun `verify adventurer starting point coordinates`() {
        val levelCoords = InitialDataSeed.getLevelCoordinates()
        assertEquals(50, levelCoords.size)

        // Starting point at Forest start stone (324, 1460) on portrait map
        assertEquals(324f, levelCoords[0].first, 0.01f)
        assertEquals(1460f, levelCoords[0].second, 0.01f)
    }

    @Test
    fun `verify audio raw resources exist`() {
        assertTrue(R.raw.sound_correct != 0)
        assertTrue(R.raw.sound_incorrect != 0)
    }

    @Test
    fun `verify indirect clues contain no English target words across all 50 levels`() {
        val path = com.example.data.models.LearningPath.LOW
        val allQuestions = mutableListOf<com.example.data.models.Question>()
        for (stage in 1..5) {
            for (level in 1..10) {
                allQuestions.addAll(com.example.data.seed.LevelTopicCatalog.generateTenQuestionsForLevel(path, stage, level))
            }
        }
        assertEquals(500, allQuestions.size)
        val violations = com.example.data.seed.LevelTopicCatalog.validateClues(allQuestions)
        assertTrue("Found clue violations: $violations", violations.isEmpty())
    }

    @Test
    fun `verify category sort buckets and failure feedback across all levels`() {
        val path = com.example.data.models.LearningPath.LOW
        for (stage in 1..5) {
            for (level in 1..10) {
                val questions = com.example.data.seed.LevelTopicCatalog.generateTenQuestionsForLevel(path, stage, level)
                val catQ = questions.find { it.type == com.example.data.models.QuestionType.CATEGORY_SORT }
                assertNotNull("Level $stage-$level must have a CATEGORY_SORT question", catQ)
                val buckets = catQ!!.categoryBuckets
                assertNotNull(buckets)
                assertEquals(2, buckets!!.size)
                buckets.forEach { (name, words) ->
                    assertTrue("Bucket '$name' in $stage-$level must have >= 2 words", words.size >= 2)
                }
                assertNotNull("Category Sort must have specific failureFeedback", catQ.failureFeedback)
            }
        }
    }

    @Test
    fun `verify word hunt prompt does not spell raw target word`() {
        val path = com.example.data.models.LearningPath.LOW
        for (stage in 1..5) {
            for (level in 1..10) {
                val questions = com.example.data.seed.LevelTopicCatalog.generateTenQuestionsForLevel(path, stage, level)
                val huntQ = questions.find { it.type == com.example.data.models.QuestionType.WORD_HUNT }
                assertNotNull("Level $stage-$level must have a WORD_HUNT question", huntQ)
                val grid = huntQ!!.gridLetters
                assertNotNull(grid)
                assertTrue("Grid size must be 6 or 8", grid!!.size == 6 || grid.size == 8)
                assertFalse("Prompt must not give away raw target word", huntQ.prompt.contains("'${huntQ.targetWord}'", ignoreCase = true))
            }
        }
    }

    @Test
    fun `verify master level disables hints completely`() {
        val path = com.example.data.models.LearningPath.LOW
        val masterQuestions = com.example.data.seed.LevelTopicCatalog.generateTenQuestionsForLevel(path, stage = 5, level = 10)
        masterQuestions.forEach { q ->
            assertFalse("Master level 5-10 must have hintsEnabled = false", q.hintsEnabled)
        }

        val normalQuestions = com.example.data.seed.LevelTopicCatalog.generateTenQuestionsForLevel(path, stage = 1, level = 1)
        normalQuestions.forEach { q ->
            assertTrue("Normal level 1-1 must have hintsEnabled = true", q.hintsEnabled)
        }
    }
}

