package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LearningPath
import com.example.data.models.UserProfile
import com.example.ui.theme.*

// =========================================================================
// 1. SIGNATURE ELEMENT: WAX SEAL BADGE
// =========================================================================
@Composable
fun WaxSealBadge(
    modifier: Modifier = Modifier,
    badgeSize: Dp = 44.dp,
    label: String,
    icon: @Composable () -> Unit,
    rotation: Float = -1.5f
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .size(badgeSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val r = (w.coerceAtMost(h) / 2f) * 0.94f

            // Scalloped wax edge
            val scallops = 14
            val sealPath = Path().apply {
                for (i in 0 until scallops) {
                    val angle = Math.toRadians((i * (360.0 / scallops) - 90.0)).toFloat()
                    val nextAngle = Math.toRadians(((i + 1) * (360.0 / scallops) - 90.0)).toFloat()
                    val midAngle = (angle + nextAngle) / 2f
                    val outerR = r * 1.04f
                    val innerR = r * 0.90f

                    val x1 = cx + kotlin.math.cos(angle) * innerR
                    val y1 = cy + kotlin.math.sin(angle) * innerR
                    val xm = cx + kotlin.math.cos(midAngle) * outerR
                    val ym = cy + kotlin.math.sin(midAngle) * outerR
                    val x2 = cx + kotlin.math.cos(nextAngle) * innerR
                    val y2 = cy + kotlin.math.sin(nextAngle) * innerR

                    if (i == 0) moveTo(x1, y1)
                    quadraticBezierTo(xm, ym, x2, y2)
                }
                close()
            }

            // Drop shadow simulation
            drawPath(sealPath, color = DeepForestGreen.copy(alpha = 0.25f))
            // Sand gold wax fill
            drawPath(sealPath, color = SandGold)
            // Stamped concentric double ring
            drawCircle(
                color = DeepForestGreen,
                radius = r * 0.82f,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawCircle(
                color = DeepForestGreen.copy(alpha = 0.45f),
                radius = r * 0.72f,
                style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 3f)))
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()
            if (label.isNotBlank()) {
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = label,
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = DeepForestGreen
                )
            }
        }
    }
}

// =========================================================================
// 2. SIGNATURE ELEMENT: LAYERED PARCHMENT DOSSIER CARD
// =========================================================================
@Composable
fun ParchmentDossierCard(
    modifier: Modifier = Modifier,
    rotation: Float = 0f,
    backgroundColor: Color = PureWhite,
    borderColor: Color = SageGreen,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .rotate(rotation)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp), spotColor = DeepForestGreen)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = backgroundColor,
            border = androidx.compose.foundation.BorderStroke(1.75.dp, borderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box {
                // Inked Corner Surveyor Crosshairs
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val crossLen = 7.dp.toPx()
                    val inset = 8.dp.toPx()
                    val strokeW = 1.2.dp.toPx()
                    val c = borderColor.copy(alpha = 0.7f)

                    // Top-Left Crosshair
                    drawLine(c, Offset(inset - crossLen / 2, inset), Offset(inset + crossLen / 2, inset), strokeW)
                    drawLine(c, Offset(inset, inset - crossLen / 2), Offset(inset, inset + crossLen / 2), strokeW)

                    // Top-Right Crosshair
                    val trX = size.width - inset
                    drawLine(c, Offset(trX - crossLen / 2, inset), Offset(trX + crossLen / 2, inset), strokeW)
                    drawLine(c, Offset(trX, inset - crossLen / 2), Offset(trX, inset + crossLen / 2), strokeW)

                    // Bottom-Left Crosshair
                    val blY = size.height - inset
                    drawLine(c, Offset(inset - crossLen / 2, blY), Offset(inset + crossLen / 2, blY), strokeW)
                    drawLine(c, Offset(inset, blY - crossLen / 2), Offset(inset, blY + crossLen / 2), strokeW)

                    // Bottom-Right Crosshair
                    val brX = size.width - inset
                    val brY = size.height - inset
                    drawLine(c, Offset(brX - crossLen / 2, brY), Offset(brX + crossLen / 2, brY), strokeW)
                    drawLine(c, Offset(brX, brY - crossLen / 2), Offset(brX, brY + crossLen / 2), strokeW)
                }

                Column(
                    modifier = Modifier.padding(18.dp),
                    content = content
                )
            }
        }
    }
}

// =========================================================================
// 3. SIGNATURE ELEMENT: HAND-DRAWN WINDING TRAIL PROGRESS
// =========================================================================
@Composable
fun HandDrawnWindingTrail(
    completedSteps: Int,
    totalSteps: Int = 3,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(38.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cy = h / 2f
        val strokeW = 2.5.dp.toPx()

        // S-curve winding trail path
        val startX = w * 0.08f
        val endX = w * 0.92f
        val segW = (endX - startX) / (totalSteps - 1).coerceAtLeast(1)

        val trailPath = Path().apply {
            moveTo(startX, cy)
            for (i in 0 until totalSteps - 1) {
                val x1 = startX + i * segW
                val x2 = startX + (i + 1) * segW
                val midX = (x1 + x2) / 2f
                val waveOffset = if (i % 2 == 0) -8f else 8f
                cubicTo(midX, cy + waveOffset, midX, cy - waveOffset, x2, cy)
            }
        }

        // Draw Inactive Dashed Trail in Sage Green
        drawPath(
            trailPath,
            color = SageGreen.copy(alpha = 0.45f),
            style = Stroke(
                width = strokeW,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
            )
        )

        // Draw Completed Solid Trail in Deep Forest Green
        if (completedSteps > 0) {
            val progressFrac = (completedSteps.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
            val measure = PathMeasure()
            measure.setPath(trailPath, false)
            val subPath = Path()
            measure.getSegment(0f, measure.length * progressFrac, subPath, true)
            drawPath(
                subPath,
                color = DeepForestGreen,
                style = Stroke(width = strokeW * 1.2f, cap = StrokeCap.Round)
            )
        }

        // Draw Waypoint Diamond Nodes
        for (i in 0 until totalSteps) {
            val nodeX = startX + i * segW
            val isDone = i < completedSteps
            val isCurrent = i == completedSteps

            val nodeRadius = if (isCurrent) 7.dp.toPx() else 5.5.dp.toPx()
            val diamondPath = Path().apply {
                moveTo(nodeX, cy - nodeRadius)
                lineTo(nodeX + nodeRadius, cy)
                lineTo(nodeX, cy + nodeRadius)
                lineTo(nodeX - nodeRadius, cy)
                close()
            }

            // Fill & Inked outline
            val fillColor = when {
                isDone -> DeepForestGreen
                isCurrent -> SandGold
                else -> PureWhite
            }
            val outlineColor = if (isCurrent) DeepForestGreen else SageGreen

            drawPath(diamondPath, color = fillColor)
            drawPath(diamondPath, color = outlineColor, style = Stroke(width = 1.75.dp.toPx()))

            // Inner marker glyph
            if (isDone) {
                drawCircle(color = SandGold, radius = 2.dp.toPx(), center = Offset(nodeX, cy))
            } else if (isCurrent) {
                drawCircle(color = DeepForestGreen, radius = 2.5.dp.toPx(), center = Offset(nodeX, cy))
            }
        }
    }
}

// =========================================================================
// 4. CHUNKY TACTILE SIGNPOST BUTTON
// =========================================================================
@Composable
fun TactileSignpostButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = DeepForestGreen,
    contentColor: Color = PureWhite,
    label: String,
    subLabel: String? = null,
    testTag: String = "tactile_signpost_button",
    icon: @Composable (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val pressOffsetY by animateFloatAsState(targetValue = if (isPressed) 3f else 0f, label = "pressOffset")

    val buttonBorderColor = if (enabled) SandGold else SageGreen.copy(alpha = 0.5f)
    val shadowBevelColor = if (enabled) Color(0xFF0F261E) else Color(0x660F261E)

    Box(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(62.dp)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                            onClick()
                        }
                    )
                }
            }
    ) {
        // Physical 3D bottom bevel chassis - precisely aligned with the top plate
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = shadowBevelColor,
            border = androidx.compose.foundation.BorderStroke(1.75.dp, Color(0xFF091712)),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .offset(y = 4.dp)
        ) {}

        // Main tactile button plate - matches shape and border stroke exactly
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (enabled) backgroundColor else SageGreen.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.75.dp, buttonBorderColor),
            shadowElevation = if (isPressed) 1.dp else 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .offset(y = pressOffsetY.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = label.uppercase(),
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp,
                        color = contentColor
                    )
                    if (subLabel != null) {
                        Text(
                            text = subLabel,
                            fontFamily = JakartaFontFamily,
                            fontSize = 11.sp,
                            color = Cream.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 5. BESPOKE EXPEDITION TOP BAR
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerTopBar(
    profile: UserProfile?,
    selectedPath: LearningPath,
    onPathSelected: (LearningPath) -> Unit,
    onLogoutClick: () -> Unit,
    isSoundEnabled: Boolean = true,
    onToggleSound: () -> Unit = {}
) {
    var showPathMenu by remember { mutableStateOf(false) }

    Surface(
        color = DeepForestGreen,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brass Surveyor Plaque (Path Selector)
            Box {
                Surface(
                    onClick = { showPathMenu = true },
                    shape = RoundedCornerShape(14.dp),
                    color = SandGold,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DeepForestGreen),
                    shadowElevation = 3.dp,
                    modifier = Modifier.testTag("path_selector_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExpeditionIconCompass(
                            modifier = Modifier.size(16.dp),
                            tint = DeepForestGreen,
                            accentTint = PureWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = selectedPath.code,
                            fontFamily = CinzelFontFamily,
                            style = MaterialTheme.typography.labelLarge,
                            color = DeepForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                DropdownMenu(
                    expanded = showPathMenu,
                    onDismissRequest = { showPathMenu = false }
                ) {
                    LearningPath.values().forEach { path ->
                        val isAllowed = profile?.currentPath?.canAccess(path) == true
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${path.title} (${path.cefrLabel})",
                                            fontFamily = CinzelFontFamily,
                                            fontWeight = if (path == selectedPath) FontWeight.Bold else FontWeight.Normal,
                                            color = DeepForestGreen
                                        )
                                        if (!isAllowed) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            ExpeditionIconLock(modifier = Modifier.size(12.dp), tint = SageGreen)
                                        }
                                    }
                                    Text(
                                        text = path.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SageGreen
                                    )
                                }
                            },
                            onClick = {
                                showPathMenu = false
                                if (isAllowed) {
                                    onPathSelected(path)
                                }
                            },
                            enabled = isAllowed
                        )
                    }
                }
            }

            // Streak Wax Seal, Sound, and Logout
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Streak Wax Seal
                WaxSealBadge(
                    badgeSize = 40.dp,
                    label = "${profile?.currentStreak ?: 0}d",
                    icon = {
                        ExpeditionIconStreakFlame(
                            modifier = Modifier.size(14.dp),
                            tint = DeepForestGreen,
                            accentTint = SandGold
                        )
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Sound Toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("sound_toggle_button")
                ) {
                    ExpeditionIconSound(
                        modifier = Modifier.size(20.dp),
                        isEnabled = isSoundEnabled,
                        tint = SandGold
                    )
                }

                // Camp Gate Logout
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("logout_icon_button")
                ) {
                    ExpeditionIconExit(
                        modifier = Modifier.size(20.dp),
                        tint = Cream
                    )
                }
            }
        }
    }
}

// =========================================================================
// 6. BESPOKE EXPEDITION BOTTOM NAVIGATION DOCK
// =========================================================================
@Composable
fun ExplorerBottomNavBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = DeepForestGreen,
        contentColor = Cream,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf(
            "map" to "Map",
            "dashboard" to "Camp",
            "vocab" to "Lexicon",
            "stories" to "Lore",
            "companions" to "Beasts"
        )

        items.forEach { (route, label) ->
            val isSelected = currentTab == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(route) },
                icon = {
                    when (route) {
                        "map" -> ExpeditionIconMap(modifier = Modifier.size(22.dp), tint = if (isSelected) DeepForestGreen else Cream, accentTint = SandGold)
                        "dashboard" -> ExpeditionIconTent(modifier = Modifier.size(22.dp), tint = if (isSelected) DeepForestGreen else Cream, accentTint = SandGold)
                        "vocab" -> ExpeditionIconBook(modifier = Modifier.size(22.dp), tint = if (isSelected) DeepForestGreen else Cream, accentTint = SandGold)
                        "stories" -> ExpeditionIconScroll(modifier = Modifier.size(22.dp), tint = if (isSelected) DeepForestGreen else Cream, accentTint = SandGold)
                        else -> ExpeditionIconBeastPaw(modifier = Modifier.size(22.dp), tint = if (isSelected) DeepForestGreen else Cream, accentTint = SandGold)
                    }
                },
                label = {
                    Text(
                        text = label.uppercase(),
                        fontFamily = CinzelFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepForestGreen,
                    selectedTextColor = SandGold,
                    indicatorColor = SandGold,
                    unselectedIconColor = Cream.copy(alpha = 0.65f),
                    unselectedTextColor = Cream.copy(alpha = 0.65f)
                ),
                modifier = Modifier.testTag("nav_tab_$route")
            )
        }
    }
}
