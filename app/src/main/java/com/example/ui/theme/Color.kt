package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// MANDATORY EXPEDITION COLOR PALETTE
// =========================================================================
val DeepForestGreen = Color(0xFF15370D) // Primary Dominant Dark
val SageGreen       = Color(0xFF728E6D) // Secondary & Structural Accents
val SandGold        = Color(0xFFE5CD82) // Warm Accent & Exploration Rewards
val Cream           = Color(0xFFFFF2B4) // Atmospheric Parchment Background
val PureWhite       = Color(0xFFFFFFFF) // High-Clarity Surface Layer
val SemanticRed     = Color(0xFFBA1A1A) // Semantic Error / Wrong Answer Red

// =========================================================================
// THEME COMPATIBILITY MAPPINGS (Preserving existing references)
// =========================================================================
val AdventureOrange  = DeepForestGreen // Replaced orange with Deep Forest Green
val ExplorerCream    = Cream
val ForestGreen      = DeepForestGreen
val DeepJungleGreen  = DeepForestGreen
val ParchmentLight   = Cream
val SandCream        = SandGold
val GoldYellow       = SandGold
val GoldDark         = SandGold
val SeaBlue          = SageGreen
val RiverBlue        = SageGreen
val StoneGray        = SageGreen
val SnowWhite        = PureWhite
val CrystalViolet    = SageGreen
val EarthBrown       = DeepForestGreen
val DarkBackground   = DeepForestGreen
val SurfaceDark      = DeepForestGreen
val TextDark         = DeepForestGreen
val TextMuted        = DeepForestGreen.copy(alpha = 0.75f)
val SuccessGreen     = DeepForestGreen
val ErrorRed         = SemanticRed
