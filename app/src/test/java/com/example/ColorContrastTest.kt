package com.example

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.Cream
import com.example.ui.theme.CreamSoft
import com.example.ui.theme.DarkBorderSubtle
import com.example.ui.theme.DisabledBg
import com.example.ui.theme.DisabledFg
import com.example.ui.theme.Forest500
import com.example.ui.theme.Forest700
import com.example.ui.theme.Forest900
import com.example.ui.theme.Honey
import com.example.ui.theme.HoneyHover
import com.example.ui.theme.HoneyPressed
import com.example.ui.theme.HoneyTint
import com.example.ui.theme.Lagoon
import com.example.ui.theme.LagoonBright
import com.example.ui.theme.LagoonTint
import com.example.ui.theme.Leaf
import com.example.ui.theme.LeafBright
import com.example.ui.theme.LeafTint
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Sage
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.Sand
import com.example.ui.theme.SandHover
import com.example.ui.theme.SandPressed
import com.example.ui.theme.Terracotta
import com.example.ui.theme.TerracottaBright
import com.example.ui.theme.TerracottaTint
import kotlin.math.pow
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorContrastTest {

    private fun linearize(channel: Float): Double {
        val c = channel.toDouble()
        return if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
    }

    private fun relativeLuminance(color: Color): Double {
        return 0.2126 * linearize(color.red) +
            0.7152 * linearize(color.green) +
            0.0722 * linearize(color.blue)
    }

    private fun contrastRatio(fg: Color, bg: Color): Double {
        val l1 = relativeLuminance(fg)
        val l2 = relativeLuminance(bg)
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun assertContrastAtLeast(
        name: String,
        fg: Color,
        bg: Color,
        minRatio: Double
    ) {
        val ratio = contrastRatio(fg, bg)
        assertTrue(
            "$name contrast ratio %.2f:1 is below required %.2f:1".format(ratio, minRatio),
            ratio >= minRatio
        )
    }

    @Test
    fun `light surface text pairs meet WCAG AA 4_5 to 1`() {
        val lightSurfaces = listOf(
            "PureWhite" to PureWhite,
            "Cream" to Cream,
            "CreamSoft" to CreamSoft
        )
        for ((surfaceName, surface) in lightSurfaces) {
            assertContrastAtLeast("Forest900 on $surfaceName", Forest900, surface, 4.5)
            assertContrastAtLeast("Forest700 on $surfaceName", Forest700, surface, 4.5)
            assertContrastAtLeast("SageDark on $surfaceName", SageDark, surface, 4.5)
            assertContrastAtLeast("Leaf on $surfaceName", Leaf, surface, 4.5)
            assertContrastAtLeast("Terracotta on $surfaceName", Terracotta, surface, 4.5)
            assertContrastAtLeast("Lagoon on $surfaceName", Lagoon, surface, 4.5)
        }
    }

    @Test
    fun `dark surface text pairs meet WCAG AA 4_5 to 1`() {
        val darkSurfaces = listOf(
            "Forest900" to Forest900,
            "Forest700" to Forest700
        )
        for ((surfaceName, surface) in darkSurfaces) {
            assertContrastAtLeast("PureWhite on $surfaceName", PureWhite, surface, 4.5)
            assertContrastAtLeast("Cream on $surfaceName", Cream, surface, 4.5)
            assertContrastAtLeast("Sand on $surfaceName", Sand, surface, 4.5)
            assertContrastAtLeast("Honey on $surfaceName", Honey, surface, 4.5)
            assertContrastAtLeast("SageLight on $surfaceName", SageLight, surface, 4.5)
            assertContrastAtLeast("LeafBright on $surfaceName", LeafBright, surface, 4.5)
            assertContrastAtLeast("TerracottaBright on $surfaceName", TerracottaBright, surface, 4.5)
            assertContrastAtLeast("LagoonBright on $surfaceName", LagoonBright, surface, 4.5)
        }
    }

    @Test
    fun `interactive CTA and badge pairs meet WCAG AA 4_5 to 1`() {
        // Warm CTAs with Forest900 dark ink
        assertContrastAtLeast("Forest900 on Honey", Forest900, Honey, 4.5)
        assertContrastAtLeast("Forest900 on HoneyHover", Forest900, HoneyHover, 4.5)
        assertContrastAtLeast("Forest900 on HoneyPressed", Forest900, HoneyPressed, 4.5)
        assertContrastAtLeast("Forest900 on Sand", Forest900, Sand, 4.5)
        assertContrastAtLeast("Forest900 on SandHover", Forest900, SandHover, 4.5)
        assertContrastAtLeast("Forest900 on SandPressed", Forest900, SandPressed, 4.5)
        assertContrastAtLeast("Forest900 on HoneyTint", Forest900, HoneyTint, 4.5)

        // Saturated badges/chips with PureWhite text
        assertContrastAtLeast("PureWhite on Leaf", PureWhite, Leaf, 4.5)
        assertContrastAtLeast("PureWhite on Terracotta", PureWhite, Terracotta, 4.5)
        assertContrastAtLeast("PureWhite on Lagoon", PureWhite, Lagoon, 4.5)
        assertContrastAtLeast("PureWhite on Forest500", PureWhite, Forest500, 4.5)
        assertContrastAtLeast("Sand on Forest500", Sand, Forest500, 4.5)

        // Semantic tinted containers with dark semantic/ink text
        assertContrastAtLeast("Leaf on LeafTint", Leaf, LeafTint, 4.5)
        assertContrastAtLeast("Forest900 on LeafTint", Forest900, LeafTint, 4.5)
        assertContrastAtLeast("Terracotta on TerracottaTint", Terracotta, TerracottaTint, 4.5)
        assertContrastAtLeast("Forest900 on TerracottaTint", Forest900, TerracottaTint, 4.5)
        assertContrastAtLeast("Lagoon on LagoonTint", Lagoon, LagoonTint, 4.5)
        assertContrastAtLeast("Forest900 on LagoonTint", Forest900, LagoonTint, 4.5)

        // Disabled state legibility
        assertContrastAtLeast("DisabledFg on DisabledBg", DisabledFg, DisabledBg, 4.5)
        assertContrastAtLeast("SageLight on Forest500 (dark disabled)", SageLight, Forest500, 4.5)
    }

    @Test
    fun `structural border tokens meet WCAG AA 3_0 to 1 non-text contrast`() {
        assertContrastAtLeast("Sage border on PureWhite", Sage, PureWhite, 3.0)
        assertContrastAtLeast("Sage border on Forest700", Sage, Forest700, 3.0)
        assertContrastAtLeast("DarkBorderSubtle on Forest900", DarkBorderSubtle, Forest900, 3.0)
        assertContrastAtLeast("DarkBorderSubtle on Forest700", DarkBorderSubtle, Forest700, 3.0)
    }
}
