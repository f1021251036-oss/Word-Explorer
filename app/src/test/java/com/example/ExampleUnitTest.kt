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
}
