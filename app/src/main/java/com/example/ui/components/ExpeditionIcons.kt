package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

/**
 * Hand-crafted Expedition Icon Set
 * Consistent hand-inked line weight, rounded stroke caps, and palette colors.
 * Eliminates stock Material icons and standard emojis.
 */

@Composable
fun ExpeditionIconCompass(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val r = (w.coerceAtMost(h) / 2f) * 0.88f
        val strokeW = 1.75.dp.toPx()

        // Outer brass ring
        drawCircle(
            color = tint,
            radius = r,
            center = Offset(cx, cy),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )
        // Inner subtle dial ring
        drawCircle(
            color = tint.copy(alpha = 0.35f),
            radius = r * 0.72f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeW * 0.75f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)))
        )
        // Cardinal ticks (N, S, E, W)
        drawLine(tint, Offset(cx, cy - r), Offset(cx, cy - r * 0.82f), strokeW, StrokeCap.Round)
        drawLine(tint, Offset(cx, cy + r * 0.82f), Offset(cx, cy + r), strokeW, StrokeCap.Round)
        drawLine(tint, Offset(cx - r, cy), Offset(cx - r * 0.82f, cy), strokeW, StrokeCap.Round)
        drawLine(tint, Offset(cx + r * 0.82f, cy), Offset(cx + r, cy), strokeW, StrokeCap.Round)

        // North needle (filled accent)
        val northNeedle = Path().apply {
            moveTo(cx, cy - r * 0.75f)
            lineTo(cx + r * 0.22f, cy)
            lineTo(cx, cy - r * 0.08f)
            close()
        }
        drawPath(northNeedle, color = accentTint)
        drawPath(northNeedle, color = tint, style = Stroke(width = strokeW * 0.75f))

        // South needle (hollow/inked)
        val southNeedle = Path().apply {
            moveTo(cx, cy + r * 0.75f)
            lineTo(cx - r * 0.22f, cy)
            lineTo(cx, cy + r * 0.08f)
            close()
        }
        drawPath(southNeedle, color = tint.copy(alpha = 0.2f))
        drawPath(southNeedle, color = tint, style = Stroke(width = strokeW * 0.75f))

        // Center pivot brass rivet
        drawCircle(color = tint, radius = strokeW * 1.5f, center = Offset(cx, cy))
        drawCircle(color = accentTint, radius = strokeW * 0.75f, center = Offset(cx, cy))
    }
}

@Composable
fun ExpeditionIconMap(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        // 3-panel folded parchment map contour
        val p1x = w * 0.12f
        val p2x = w * 0.38f
        val p3x = w * 0.64f
        val p4x = w * 0.90f

        val top1 = h * 0.22f
        val top2 = h * 0.14f
        val top3 = h * 0.22f
        val top4 = h * 0.14f

        val bot1 = h * 0.86f
        val bot2 = h * 0.78f
        val bot3 = h * 0.86f
        val bot4 = h * 0.78f

        val fold1 = Path().apply {
            moveTo(p1x, top1)
            lineTo(p2x, top2)
            lineTo(p2x, bot2)
            lineTo(p1x, bot1)
            close()
        }
        val fold2 = Path().apply {
            moveTo(p2x, top2)
            lineTo(p3x, top3)
            lineTo(p3x, bot3)
            lineTo(p2x, bot2)
            close()
        }
        val fold3 = Path().apply {
            moveTo(p3x, top3)
            lineTo(p4x, top4)
            lineTo(p4x, bot4)
            lineTo(p3x, bot3)
            close()
        }

        drawPath(fold1, color = tint.copy(alpha = 0.1f))
        drawPath(fold2, color = accentTint.copy(alpha = 0.25f))
        drawPath(fold3, color = tint.copy(alpha = 0.15f))

        drawPath(fold1, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(fold2, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(fold3, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Inked trail line inside map
        val trail = Path().apply {
            moveTo(p1x + 6f, (top1 + bot1) / 2f + 4f)
            quadraticBezierTo(p2x, (top2 + bot2) / 2f - 8f, (p2x + p3x) / 2f, (top2 + bot3) / 2f + 2f)
            lineTo(p4x - 6f, (top4 + bot4) / 2f - 6f)
        }
        drawPath(
            trail,
            color = accentTint,
            style = Stroke(
                width = strokeW * 0.9f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f))
            )
        )
    }
}

@Composable
fun ExpeditionIconTent(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        val peakX = w * 0.42f
        val peakY = h * 0.20f
        val leftX = w * 0.12f
        val rightFrontX = w * 0.62f
        val rightBackX = w * 0.90f
        val groundY = h * 0.82f

        // Rear ridgepole line
        val backWall = Path().apply {
            moveTo(peakX, peakY)
            lineTo(w * 0.72f, peakY + 4f)
            lineTo(rightBackX, groundY)
            lineTo(rightFrontX, groundY)
            close()
        }
        drawPath(backWall, color = tint.copy(alpha = 0.15f))
        drawPath(backWall, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Front A-frame tent flap
        val frontWall = Path().apply {
            moveTo(peakX, peakY)
            lineTo(leftX, groundY)
            lineTo(rightFrontX, groundY)
            close()
        }
        drawPath(frontWall, color = accentTint.copy(alpha = 0.3f))
        drawPath(frontWall, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Tent door slit
        val door = Path().apply {
            moveTo(peakX, peakY + 4f)
            lineTo(w * 0.38f, groundY)
            lineTo(w * 0.46f, groundY)
            close()
        }
        drawPath(door, color = tint)

        // Guy wire stake
        drawLine(tint, Offset(leftX, groundY), Offset(leftX - 4f, groundY + 4f), strokeW)
        drawLine(tint, Offset(rightBackX, groundY), Offset(rightBackX + 4f, groundY + 4f), strokeW)
    }
}

@Composable
fun ExpeditionIconCampfire(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        // Crossed wooden logs
        drawLine(tint, Offset(w * 0.15f, h * 0.84f), Offset(w * 0.85f, h * 0.68f), strokeW * 1.3f, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.15f, h * 0.68f), Offset(w * 0.85f, h * 0.84f), strokeW * 1.3f, StrokeCap.Round)

        // Outer Flame tongue
        val outerFlame = Path().apply {
            moveTo(w * 0.50f, h * 0.12f)
            cubicTo(w * 0.75f, h * 0.35f, w * 0.82f, h * 0.58f, w * 0.68f, h * 0.72f)
            cubicTo(w * 0.55f, h * 0.78f, w * 0.45f, h * 0.78f, w * 0.32f, h * 0.72f)
            cubicTo(w * 0.18f, h * 0.58f, w * 0.25f, h * 0.35f, w * 0.50f, h * 0.12f)
            close()
        }
        drawPath(outerFlame, color = accentTint)
        drawPath(outerFlame, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Inner fire heart
        val innerFlame = Path().apply {
            moveTo(w * 0.50f, h * 0.38f)
            cubicTo(w * 0.62f, h * 0.50f, w * 0.65f, h * 0.62f, w * 0.58f, h * 0.70f)
            cubicTo(w * 0.50f, h * 0.74f, w * 0.44f, h * 0.70f, w * 0.42f, h * 0.70f)
            cubicTo(w * 0.35f, h * 0.62f, w * 0.38f, h * 0.50f, w * 0.50f, h * 0.38f)
            close()
        }
        drawPath(innerFlame, color = Color(0xFFA8422B))
    }
}

@Composable
fun ExpeditionIconBackpack(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        // Bedroll on top
        drawRoundRect(
            color = accentTint,
            topLeft = Offset(w * 0.20f, h * 0.12f),
            size = androidx.compose.ui.geometry.Size(w * 0.60f, h * 0.16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.20f, h * 0.12f),
            size = androidx.compose.ui.geometry.Size(w * 0.60f, h * 0.16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
            style = Stroke(width = strokeW)
        )

        // Main rucksack body
        val bag = Path().apply {
            moveTo(w * 0.22f, h * 0.30f)
            lineTo(w * 0.78f, h * 0.30f)
            lineTo(w * 0.82f, h * 0.86f)
            quadraticBezierTo(w * 0.50f, h * 0.90f, w * 0.18f, h * 0.86f)
            close()
        }
        drawPath(bag, color = tint.copy(alpha = 0.15f))
        drawPath(bag, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Front pouch
        drawRoundRect(
            color = PureWhite,
            topLeft = Offset(w * 0.30f, h * 0.52f),
            size = androidx.compose.ui.geometry.Size(w * 0.40f, h * 0.28f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.30f, h * 0.52f),
            size = androidx.compose.ui.geometry.Size(w * 0.40f, h * 0.28f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
            style = Stroke(width = strokeW)
        )

        // Brass buckles
        drawCircle(accentTint, strokeW * 1.2f, Offset(w * 0.50f, h * 0.42f))
        drawCircle(accentTint, strokeW * 1.2f, Offset(w * 0.50f, h * 0.66f))
    }
}

@Composable
fun ExpeditionIconLantern(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        // Handle
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.30f, h * 0.08f),
            size = androidx.compose.ui.geometry.Size(w * 0.40f, h * 0.24f),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )

        // Cap & Base
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.25f, h * 0.26f),
            size = androidx.compose.ui.geometry.Size(w * 0.50f, h * 0.10f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.22f, h * 0.76f),
            size = androidx.compose.ui.geometry.Size(w * 0.56f, h * 0.12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )

        // Glass chamber (glowing with SandGold flame)
        val glass = Path().apply {
            moveTo(w * 0.28f, h * 0.36f)
            lineTo(w * 0.72f, h * 0.36f)
            lineTo(w * 0.68f, h * 0.76f)
            lineTo(w * 0.32f, h * 0.76f)
            close()
        }
        drawPath(glass, color = accentTint.copy(alpha = 0.45f))
        drawPath(glass, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Inner glowing flame
        drawCircle(color = accentTint, radius = strokeW * 2.2f, center = Offset(w * 0.50f, h * 0.56f))
        drawCircle(color = Color(0xFFA8422B), radius = strokeW * 1.1f, center = Offset(w * 0.50f, h * 0.56f))
    }
}

@Composable
fun ExpeditionIconStreakFlame(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        val flame = Path().apply {
            moveTo(w * 0.50f, h * 0.10f)
            cubicTo(w * 0.78f, h * 0.30f, w * 0.88f, h * 0.58f, w * 0.72f, h * 0.78f)
            cubicTo(w * 0.58f, h * 0.90f, w * 0.42f, h * 0.90f, w * 0.28f, h * 0.78f)
            cubicTo(w * 0.12f, h * 0.58f, w * 0.22f, h * 0.30f, w * 0.50f, h * 0.10f)
            close()
        }
        drawPath(flame, color = accentTint)
        drawPath(flame, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Inked inner wick
        val innerWick = Path().apply {
            moveTo(w * 0.50f, h * 0.44f)
            cubicTo(w * 0.62f, h * 0.56f, w * 0.60f, h * 0.70f, w * 0.50f, h * 0.76f)
            cubicTo(w * 0.40f, h * 0.70f, w * 0.38f, h * 0.56f, w * 0.50f, h * 0.44f)
            close()
        }
        drawPath(innerWick, color = Color(0xFFA8422B))
        drawPath(innerWick, color = tint, style = Stroke(width = strokeW * 0.75f))
    }
}

@Composable
fun ExpeditionIconStarSeal(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeW = 1.75.dp.toPx()

        // 8-point nautical surveyor star
        val rOuter = w * 0.44f
        val rInner = w * 0.18f
        val star = Path().apply {
            for (i in 0 until 16) {
                val angle = Math.toRadians((i * 22.5 - 90.0)).toFloat()
                val r = if (i % 2 == 0) rOuter else rInner
                val x = cx + kotlin.math.cos(angle) * r
                val y = cy + kotlin.math.sin(angle) * r
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(star, color = accentTint)
        drawPath(star, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color = tint, radius = strokeW * 1.5f, center = Offset(cx, cy))
    }
}

@Composable
fun ExpeditionIconLock(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        // Shackle
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.28f, h * 0.14f),
            size = androidx.compose.ui.geometry.Size(w * 0.44f, h * 0.44f),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )

        // Heavy padlock body
        drawRoundRect(
            color = tint.copy(alpha = 0.15f),
            topLeft = Offset(w * 0.20f, h * 0.44f),
            size = androidx.compose.ui.geometry.Size(w * 0.60f, h * 0.46f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.20f, h * 0.44f),
            size = androidx.compose.ui.geometry.Size(w * 0.60f, h * 0.46f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
            style = Stroke(width = strokeW)
        )

        // Keyhole
        drawCircle(color = tint, radius = strokeW * 1.4f, center = Offset(w * 0.50f, h * 0.62f))
        drawLine(tint, Offset(w * 0.50f, h * 0.62f), Offset(w * 0.50f, h * 0.74f), strokeW * 1.4f, StrokeCap.Round)
    }
}

@Composable
fun ExpeditionIconCheckmark(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color(0xFF2D5A27) // Leaf green
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        val check = Path().apply {
            moveTo(w * 0.20f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.74f)
            lineTo(w * 0.82f, h * 0.26f)
        }
        drawPath(check, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ExpeditionIconBook(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        val spineX = w * 0.50f
        val spineTop = h * 0.28f
        val spineBot = h * 0.86f

        // Left Page
        val leftPage = Path().apply {
            moveTo(spineX, spineTop)
            quadraticBezierTo(w * 0.30f, spineTop - 6f, w * 0.12f, spineTop)
            lineTo(w * 0.12f, spineBot)
            quadraticBezierTo(w * 0.30f, spineBot - 6f, spineX, spineBot)
            close()
        }
        // Right Page
        val rightPage = Path().apply {
            moveTo(spineX, spineTop)
            quadraticBezierTo(w * 0.70f, spineTop - 6f, w * 0.88f, spineTop)
            lineTo(w * 0.88f, spineBot)
            quadraticBezierTo(w * 0.70f, spineBot - 6f, spineX, spineBot)
            close()
        }

        drawPath(leftPage, color = PureWhite)
        drawPath(rightPage, color = accentTint.copy(alpha = 0.35f))

        drawPath(leftPage, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(rightPage, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawLine(tint, Offset(spineX, spineTop), Offset(spineX, spineBot), strokeW * 1.5f, StrokeCap.Round)
    }
}

@Composable
fun ExpeditionIconScroll(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        val scroll = Path().apply {
            moveTo(w * 0.22f, h * 0.22f)
            cubicTo(w * 0.32f, h * 0.12f, w * 0.68f, h * 0.28f, w * 0.78f, h * 0.18f)
            lineTo(w * 0.78f, h * 0.76f)
            cubicTo(w * 0.68f, h * 0.86f, w * 0.32f, h * 0.70f, w * 0.22f, h * 0.80f)
            close()
        }
        drawPath(scroll, color = accentTint.copy(alpha = 0.3f))
        drawPath(scroll, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Inked writing lines
        drawLine(tint, Offset(w * 0.34f, h * 0.38f), Offset(w * 0.66f, h * 0.38f), strokeW * 0.75f, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.34f, h * 0.50f), Offset(w * 0.66f, h * 0.50f), strokeW * 0.75f, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.34f, h * 0.62f), Offset(w * 0.54f, h * 0.62f), strokeW * 0.75f, StrokeCap.Round)
    }
}

@Composable
fun ExpeditionIconBeastPaw(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        // Main foot pad
        val pad = Path().apply {
            moveTo(w * 0.50f, h * 0.50f)
            cubicTo(w * 0.74f, h * 0.50f, w * 0.82f, h * 0.74f, w * 0.66f, h * 0.86f)
            cubicTo(w * 0.50f, h * 0.94f, w * 0.34f, h * 0.86f, w * 0.20f, h * 0.74f)
            cubicTo(w * 0.16f, h * 0.50f, w * 0.26f, h * 0.50f, w * 0.50f, h * 0.50f)
            close()
        }
        drawPath(pad, color = accentTint)
        drawPath(pad, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // 4 Claws/Toe pads
        drawCircle(color = tint, radius = w * 0.08f, center = Offset(w * 0.24f, h * 0.36f))
        drawCircle(color = tint, radius = w * 0.09f, center = Offset(w * 0.42f, h * 0.24f))
        drawCircle(color = tint, radius = w * 0.09f, center = Offset(w * 0.60f, h * 0.24f))
        drawCircle(color = tint, radius = w * 0.08f, center = Offset(w * 0.78f, h * 0.36f))
    }
}

@Composable
fun ExpeditionIconSound(
    modifier: Modifier = Modifier.size(24.dp),
    isEnabled: Boolean,
    tint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        // Speaker cone
        val speaker = Path().apply {
            moveTo(w * 0.18f, h * 0.38f)
            lineTo(w * 0.34f, h * 0.38f)
            lineTo(w * 0.54f, h * 0.20f)
            lineTo(w * 0.54f, h * 0.80f)
            lineTo(w * 0.34f, h * 0.62f)
            lineTo(w * 0.18f, h * 0.62f)
            close()
        }
        drawPath(speaker, color = tint.copy(alpha = 0.3f))
        drawPath(speaker, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        if (isEnabled) {
            // Sound wave arcs
            drawArc(
                color = tint,
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(w * 0.42f, h * 0.32f),
                size = androidx.compose.ui.geometry.Size(w * 0.34f, h * 0.36f),
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
            drawArc(
                color = tint,
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(w * 0.50f, h * 0.20f),
                size = androidx.compose.ui.geometry.Size(w * 0.44f, h * 0.60f),
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
        } else {
            // Mute slash
            drawLine(
                color = Color(0xFFA8422B),
                start = Offset(w * 0.68f, h * 0.32f),
                end = Offset(w * 0.88f, h * 0.68f),
                strokeWidth = strokeW * 1.2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFFA8422B),
                start = Offset(w * 0.88f, h * 0.32f),
                end = Offset(w * 0.68f, h * 0.68f),
                strokeWidth = strokeW * 1.2f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun ExpeditionIconExit(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Cream
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        // Camp Gate / Doorway
        val door = Path().apply {
            moveTo(w * 0.50f, h * 0.82f)
            lineTo(w * 0.22f, h * 0.82f)
            lineTo(w * 0.22f, h * 0.18f)
            lineTo(w * 0.50f, h * 0.18f)
        }
        drawPath(door, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Exit arrow pointing outward
        drawLine(tint, Offset(w * 0.38f, h * 0.50f), Offset(w * 0.84f, h * 0.50f), strokeW, StrokeCap.Round)
        val arrow = Path().apply {
            moveTo(w * 0.70f, h * 0.36f)
            lineTo(w * 0.84f, h * 0.50f)
            lineTo(w * 0.70f, h * 0.64f)
        }
        drawPath(arrow, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ExpeditionIconCross(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = Color(0xFFA8422B) // Muted terracotta
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()
        // Hand-inked cross
        drawLine(tint, Offset(w * 0.25f, h * 0.25f), Offset(w * 0.75f, h * 0.75f), strokeW, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.75f, h * 0.25f), Offset(w * 0.25f, h * 0.75f), strokeW, StrokeCap.Round)
    }
}

@Composable
fun ExpeditionIconReset(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()
        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.36f

        drawArc(
            color = tint,
            startAngle = 45f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(cx - r, cy - r),
            size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
            style = Stroke(width = strokeW, cap = StrokeCap.Round)
        )
        // Arrow head at arc start
        val arrowHead = Path().apply {
            moveTo(cx + r * 0.70f - 4f, cy + r * 0.70f - 8f)
            lineTo(cx + r * 0.70f, cy + r * 0.70f)
            lineTo(cx + r * 0.70f + 8f, cy + r * 0.70f - 4f)
        }
        drawPath(arrowHead, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ExpeditionIconTree(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        // Tree trunk
        val trunk = Path().apply {
            moveTo(w * 0.44f, h * 0.65f)
            lineTo(w * 0.44f, h * 0.88f)
            lineTo(w * 0.56f, h * 0.88f)
            lineTo(w * 0.56f, h * 0.65f)
            close()
        }
        drawPath(trunk, color = tint)

        // Tiered pine foliage layers
        val foliage = Path().apply {
            // Top tier
            moveTo(w * 0.50f, h * 0.12f)
            lineTo(w * 0.66f, h * 0.35f)
            lineTo(w * 0.58f, h * 0.35f)
            // Middle tier
            lineTo(w * 0.74f, h * 0.52f)
            lineTo(w * 0.62f, h * 0.52f)
            // Bottom tier
            lineTo(w * 0.82f, h * 0.68f)
            lineTo(w * 0.18f, h * 0.68f)
            // Left side
            lineTo(w * 0.38f, h * 0.52f)
            lineTo(w * 0.26f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.35f)
            lineTo(w * 0.34f, h * 0.35f)
            close()
        }
        drawPath(foliage, color = accentTint.copy(alpha = 0.4f))
        drawPath(foliage, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
fun ExpeditionIconBoots(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = DeepForestGreen,
    accentTint: Color = SandGold
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 1.75.dp.toPx()

        val boot = Path().apply {
            moveTo(w * 0.32f, h * 0.20f)
            lineTo(w * 0.55f, h * 0.20f)
            lineTo(w * 0.55f, h * 0.55f)
            lineTo(w * 0.80f, h * 0.62f)
            cubicTo(w * 0.86f, h * 0.66f, w * 0.86f, h * 0.78f, w * 0.80f, h * 0.82f)
            lineTo(w * 0.25f, h * 0.82f)
            lineTo(w * 0.25f, h * 0.55f)
            close()
        }
        drawPath(boot, color = accentTint.copy(alpha = 0.35f))
        drawPath(boot, color = tint, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Boot sole treads
        drawLine(tint, Offset(w * 0.25f, h * 0.82f), Offset(w * 0.80f, h * 0.82f), strokeW * 1.5f, StrokeCap.Round)
    }
}


