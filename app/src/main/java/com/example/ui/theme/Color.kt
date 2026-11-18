package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// EXPEDITION COLOR SYSTEM (WCAG 2.1 AA VERIFIED)
// =========================================================================

// Forest & Neutral Parchment Base
val Forest900 = Color(0xFF0F2A09) // Deepest ink text & elevated dark surface
val Forest700 = Color(0xFF15370D) // Primary dark surface (headers, dark canvas)
val Forest500 = Color(0xFF2E5A1F) // Secondary dark fill & elevated dark surface/border

val SageDark  = Color(0xFF51654E) // Secondary/caption text on Cream & PureWhite (>= 5.6:1 AA)
val Sage      = Color(0xFF728E6D) // Structural borders & non-text UI only (>= 3.2:1, NEVER small text)
val SageLight = Color(0xFFC2D3BE) // Muted text & disabled labels on dark Forest surfaces (>= 5.1:1 on Forest500 AA)

val Cream     = Color(0xFFFFF2B4) // Atmospheric parchment canvas & primary light text on dark
val CreamSoft = Color(0xFFFFF8D6) // Subtle elevated parchment container
val PureWhite = Color(0xFFFFFFFF) // High-clarity card, modal, dropdown & option surface

// Warm Earthy Exploration Accents & CTAs
val Sand        = Color(0xFFE5CD82) // Secondary CTA fill & active chip/pill
val SandHover   = Color(0xFFEAD69A) // Secondary CTA hover/focus
val SandPressed = Color(0xFFDFC267) // Secondary CTA pressed

val Honey        = Color(0xFFD9A441) // Primary CTA fill & star/XP highlight (pair with Forest900 ink)
val HoneyHover   = Color(0xFFDFB25E) // Primary CTA hover/focus
val HoneyPressed = Color(0xFFCC9329) // Primary CTA pressed
val HoneyTint    = Color(0xFFFBF1DC) // Subtle warm highlight container

// Semantic & Section Accents (lightness-adjusted for WCAG AA)
val Leaf       = Color(0xFF447632) // Success / Completed / Camp accent on light (5.41:1 vs White)
val LeafBright = Color(0xFF76BB5D) // Success / Camp accent on dark Forest (5.70:1 vs Forest700)
val LeafTint   = Color(0xFFE8F3E4) // Completed / success container tint

val Terracotta       = Color(0xFFA54C37) // Warm error / Beasts / high-tier on light (5.69:1 vs White)
val TerracottaBright = Color(0xFFD69484) // Beasts / error highlight on dark Forest (5.32:1 vs Forest700)
val TerracottaTint   = Color(0xFFFDECE8) // Error / alert container tint

val Lagoon       = Color(0xFF316E6C) // Lexicon / Info accent on light (5.87:1 vs White)
val LagoonBright = Color(0xFF60B9B6) // Lexicon / Info accent on dark Forest (5.75:1 vs Forest700)
val LagoonTint   = Color(0xFFE5F3F2) // Info / Lexicon container tint

// Elevation, Scrim, Dark Borders & Disabled States
val ShadowDark       = Color(0xFF091905) // Card drop shadow, scrim & CTA 3D lip
val DarkBorderSubtle = Color(0xFF588B48) // Visible UI border over Forest900/Forest700 (>= 3.2:1)
val DisabledBg       = Color(0xFFE6E2D1) // Disabled fill on light surfaces
val DisabledFg       = Color(0xFF5F665B) // Disabled label/icon on DisabledBg (AA readable)

// =========================================================================
// LEGACY & COMPATIBILITY MAPPINGS (Preserving existing exported symbols)
// =========================================================================
val DeepForestGreen = Forest700
val SageGreen       = Sage
val SandGold        = Sand
val SemanticRed     = Terracotta

val AdventureOrange  = Honey
val ExplorerCream    = Cream
val ForestGreen      = Forest500
val DeepJungleGreen  = Forest900
val ParchmentLight   = CreamSoft
val SandCream        = Sand
val GoldYellow       = Sand
val GoldDark         = HoneyPressed
val SeaBlue          = Lagoon
val RiverBlue        = LagoonBright
val StoneGray        = SageDark
val SnowWhite        = PureWhite
val CrystalViolet    = LagoonTint
val EarthBrown       = Terracotta
val DarkBackground   = Forest700
val SurfaceDark      = Forest900
val TextDark         = Forest900
val TextMuted        = SageDark
val SuccessGreen     = Leaf
val ErrorRed         = Terracotta

