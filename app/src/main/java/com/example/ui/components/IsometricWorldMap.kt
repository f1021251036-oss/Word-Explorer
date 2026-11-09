package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.ExplorationRegion
import com.example.data.models.LearningPath
import com.example.data.models.LevelInfo
import com.example.data.models.LevelStatus
import com.example.data.seed.InitialDataSeed
import com.example.ui.theme.*

// Native dimensions of the portrait fantasy adventure map illustration (9:16 aspect ratio)
private const val NATIVE_MAP_WIDTH = 899f
private const val NATIVE_MAP_HEIGHT = 1600f
private const val MAP_ASPECT_RATIO = NATIVE_MAP_WIDTH / NATIVE_MAP_HEIGHT

@Composable
fun IsometricWorldMap(
    path: LearningPath,
    levels: List<LevelInfo>,
    activeCompanionId: String,
    onLevelClick: (LevelInfo) -> Unit,
    onRegionClick: (ExplorationRegion) -> Unit,
    isRegionUnlocked: (Int) -> Boolean,
    modifier: Modifier = Modifier
) {
    // Determine active level along the calibrated trail
    val activeLevel = levels.lastOrNull { it.status == LevelStatus.AVAILABLE }
        ?: levels.lastOrNull { it.status == LevelStatus.COMPLETED }
        ?: levels.firstOrNull()

    // Determine current active stage number (1..5)
    val activeStageNumber = activeLevel?.stageNumber ?: 1

    // Target coordinates for adventurer (starts at Forest start point: 324, 1460)
    val charTargetX = activeLevel?.posX ?: 324f
    val charTargetY = activeLevel?.posY ?: 1460f

    // Smooth animated explorer movement along the trail
    val animatedCharX by animateFloatAsState(
        targetValue = charTargetX,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "charX"
    )
    val animatedCharY by animateFloatAsState(
        targetValue = charTargetY,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "charY"
    )

    // Pulsing animations for active level marker & adventurer idle bobbing
    val infiniteTransition = rememberInfiniteTransition(label = "mapAnimations")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.20f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val characterBob by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "characterBob"
    )

    // Feedback message state when tapping locked levels or regions
    var lockedAlertMessage by remember { mutableStateOf<String?>(null) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF091B14))
    ) {
        val density = LocalDensity.current
        val viewportWidth = maxWidth
        val viewportHeight = maxHeight

        val viewportWpx = with(density) { viewportWidth.toPx() }
        val viewportHpx = with(density) { viewportHeight.toPx() }

        // Proportional containment size calculation (object-fit: contain)
        // Ensures the 9:16 portrait map NEVER stretches or distorts on any phone, tablet, or foldable screen
        val containerAspect = viewportWpx / viewportHpx
        val (fitWidth, fitHeight, fitWpx, fitHpx) = remember(viewportWpx, viewportHpx) {
            if (containerAspect > MAP_ASPECT_RATIO) {
                // Viewport is wider than 9:16 -> height is the limiting factor
                val h = viewportHeight
                val w = viewportHeight * MAP_ASPECT_RATIO
                val hPx = viewportHpx
                val wPx = viewportHpx * MAP_ASPECT_RATIO
                Tuple4(w, h, wPx, hPx)
            } else {
                // Viewport is narrower or equal to 9:16 -> width is the limiting factor
                val w = viewportWidth
                val h = viewportWidth / MAP_ASPECT_RATIO
                val wPx = viewportWpx
                val hPx = viewportWpx / MAP_ASPECT_RATIO
                Tuple4(w, h, wPx, hPx)
            }
        }

        // Pan & Zoom state (scale clamped between 1.0f and 3.0f)
        var scale by remember { mutableStateOf(1.0f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        // Clamping helper ensuring map stays neatly within viewport
        fun clampPanOffsets(rawX: Float, rawY: Float, currentScale: Float): Pair<Float, Float> {
            val renderedW = fitWpx * currentScale
            val renderedH = fitHpx * currentScale

            val minX = if (renderedW > viewportWpx) viewportWpx - renderedW else (viewportWpx - renderedW) / 2f
            val maxX = if (renderedW > viewportWpx) 0f else (viewportWpx - renderedW) / 2f

            val minY = if (renderedH > viewportHpx) viewportHpx - renderedH else (viewportHpx - renderedH) / 2f
            val maxY = if (renderedH > viewportHpx) 0f else (viewportHpx - renderedH) / 2f

            return Pair(rawX.coerceIn(minX, maxX), rawY.coerceIn(minY, maxY))
        }

        // Initially center on active level (e.g. Forest 1-1 at bottom)
        LaunchedEffect(fitWpx, fitHpx, viewportWpx, viewportHpx) {
            val normX = charTargetX / NATIVE_MAP_WIDTH
            val normY = charTargetY / NATIVE_MAP_HEIGHT
            val targetScale = 1.0f
            val targetBaseX = normX * fitWpx
            val targetBaseY = normY * fitHpx
            val centeredX = viewportWpx / 2f - targetBaseX * targetScale
            val centeredY = viewportHpx * 0.75f - targetBaseY * targetScale
            val clamped = clampPanOffsets(centeredX, centeredY, targetScale)
            offsetX = clamped.first
            offsetY = clamped.second
        }

        // --- Interactive Map Viewport with Pinch-to-Zoom & Pan ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(fitWpx, fitHpx, viewportWpx, viewportHpx) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(1.0f, 3.0f)
                        scale = newScale
                        val clamped = clampPanOffsets(offsetX + pan.x, offsetY + pan.y, newScale)
                        offsetX = clamped.first
                        offsetY = clamped.second
                    }
                }
        ) {
            // Proportional Scaled Map Canvas Layer
            Box(
                modifier = Modifier
                    .size(fitWidth, fitHeight)
                    .graphicsLayer {
                        translationX = offsetX
                        translationY = offsetY
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
            ) {
                // 1. Exact Portrait Adventure Map Background (899 x 1600, unmodified continuous illustration)
                Image(
                    painter = painterResource(id = R.drawable.peta_petualangan_portrait),
                    contentDescription = "Fantasy Adventure Map - Portrait Level Selection",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )

                // 2. Connecting Gold Route Curve Canvas behind markers
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pathWidthPx = 3.dp.toPx()
                    val goldGlowColor = Color(0x66FFD700)
                    val goldCoreColor = Color(0xCCFFA000)

                    // Draw connecting path line between consecutive level points
                    for (i in 0 until levels.size - 1) {
                        val curr = levels[i]
                        val next = levels[i + 1]

                        val startX = (curr.posX / NATIVE_MAP_WIDTH) * size.width
                        val startY = (curr.posY / NATIVE_MAP_HEIGHT) * size.height
                        val endX = (next.posX / NATIVE_MAP_WIDTH) * size.width
                        val endY = (next.posY / NATIVE_MAP_HEIGHT) * size.height

                        // Draw subtle glow line
                        drawLine(
                            color = goldGlowColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = pathWidthPx * 1.8f,
                            cap = StrokeCap.Round
                        )
                        // Draw core line
                        drawLine(
                            color = goldCoreColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = pathWidthPx,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // 3. 50 Numbered Level Points (10 points per chapter)
                levels.forEach { level ->
                    val isCurrent = level.id == activeLevel?.id
                    val isCompleted = level.status == LevelStatus.COMPLETED
                    val isAvailable = level.status == LevelStatus.AVAILABLE
                    val isLocked = level.status == LevelStatus.LOCKED

                    val normX = level.posX / NATIVE_MAP_WIDTH
                    val normY = level.posY / NATIVE_MAP_HEIGHT

                    val nodeXDp = fitWidth * normX
                    val nodeYDp = fitHeight * normY

                    // Milestone checkpoint flags on level 10 of each chapter
                    val isCheckpoint = level.levelNumber == 10

                    Box(
                        modifier = Modifier
                            .offset(
                                x = nodeXDp - 18.dp,
                                y = nodeYDp - 18.dp
                            )
                            .size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulsing outer halo for active level
                        if (isCurrent) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = SandGold.copy(alpha = pulseAlpha * 0.5f),
                                    radius = (size.minDimension / 2f) * pulseScale
                                )
                                drawCircle(
                                    color = DeepForestGreen.copy(alpha = 0.85f),
                                    radius = (size.minDimension / 2.2f) * pulseScale,
                                    style = Stroke(width = 2.5f)
                                )
                            }
                        }

                        // Circular Node Surface
                        Surface(
                            onClick = {
                                if (isLocked) {
                                    val prevLevel = levels.find {
                                        it.stageNumber == level.stageNumber && it.levelNumber == level.levelNumber - 1
                                    } ?: levels.find { it.stageNumber == level.stageNumber - 1 && it.levelNumber == 10 }
                                    val req = prevLevel?.let { "Level ${it.stageNumber}-${it.levelNumber}" } ?: "level sebelumnya"
                                    lockedAlertMessage = "Selesaikan $req terlebih dahulu untuk membuka Level ${level.stageNumber}-${level.levelNumber}!"
                                } else {
                                    onLevelClick(level)
                                }
                            },
                            shape = CircleShape,
                            color = when {
                                isCurrent -> SandGold
                                isCompleted -> DeepForestGreen
                                isAvailable -> Cream
                                else -> SageGreen.copy(alpha = 0.35f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isCurrent) 2.5.dp else 1.5.dp,
                                color = when {
                                    isCurrent -> DeepForestGreen
                                    isCompleted -> SandGold
                                    isAvailable -> SageGreen
                                    else -> SageGreen.copy(alpha = 0.6f)
                                }
                            ),
                            shadowElevation = if (isCurrent) 8.dp else 3.dp,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("level_node_${level.stageNumber}_${level.levelNumber}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                when {
                                    isLocked -> {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked Level ${level.stageNumber}-${level.levelNumber}",
                                            tint = SageGreen,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    isCompleted -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "${level.levelNumber}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PureWhite
                                            )
                                        }
                                    }
                                    else -> {
                                        Text(
                                            text = "${level.levelNumber}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DeepForestGreen
                                        )
                                    }
                                }
                            }
                        }

                        // Star badge below completed nodes
                        if (isCompleted && level.stars > 0) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = 10.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DeepForestGreen)
                                    .padding(horizontal = 3.dp, vertical = 1.dp)
                            ) {
                                repeat(level.stars) {
                                    Text(text = "⭐", fontSize = 7.sp)
                                }
                            }
                        }

                        // Checkpoint Flag
                        if (isCheckpoint) {
                            Text(
                                text = "🚩",
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-8).dp)
                            )
                        }
                    }

                    // Prominent PLAY Button badge near active level
                    if (isCurrent) {
                        Surface(
                            onClick = { onLevelClick(level) },
                            shape = RoundedCornerShape(14.dp),
                            color = DeepForestGreen,
                            border = androidx.compose.foundation.BorderStroke(2.dp, SandGold),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .offset(
                                    x = nodeXDp - 32.dp,
                                    y = nodeYDp + 20.dp
                                )
                                .testTag("active_play_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = PureWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "PLAY",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = PureWhite
                                )
                            }
                        }
                    }
                }

                // 4. Five Exploration Areas Hitboxes and Banners: Forest -> Island -> Ancient Rome -> Mountain -> Cave
                InitialDataSeed.EXPLORATION_REGIONS.forEach { region ->
                    val unlocked = isRegionUnlocked(region.stageNumber)
                    val stageLevels = levels.filter { it.stageNumber == region.stageNumber }
                    val isCompleted = stageLevels.isNotEmpty() && stageLevels.all { it.status == LevelStatus.COMPLETED }
                    val isActiveStage = region.stageNumber == activeStageNumber

                    val normBannerX = region.bannerX / NATIVE_MAP_WIDTH
                    val normBannerY = region.bannerY / NATIVE_MAP_HEIGHT

                    val bannerOffsetXDp = fitWidth * normBannerX
                    val bannerOffsetYDp = fitHeight * normBannerY

                    // Interactive Floating Region Badge near the artwork banner
                    Surface(
                        onClick = {
                            if (unlocked) {
                                onRegionClick(region)
                            } else {
                                val prevRegion = InitialDataSeed.EXPLORATION_REGIONS.getOrNull(region.stageNumber - 2)
                                lockedAlertMessage = "Selesaikan area ${prevRegion?.name ?: "sebelumnya"} untuk membuka ${region.name}!"
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            !unlocked -> PureWhite.copy(alpha = 0.92f)
                            isCompleted -> DeepForestGreen
                            isActiveStage -> SandGold
                            else -> Cream
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            when {
                                !unlocked -> SageGreen.copy(alpha = 0.7f)
                                isCompleted -> SandGold
                                isActiveStage -> DeepForestGreen
                                else -> SageGreen
                            }
                        ),
                        shadowElevation = 5.dp,
                        modifier = Modifier
                            .offset(
                                x = bannerOffsetXDp - 40.dp,
                                y = bannerOffsetYDp - 16.dp
                            )
                            .testTag("region_banner_${region.name.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = region.iconEmoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = region.name.uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isCompleted) PureWhite else DeepForestGreen
                                )
                                Text(
                                    text = when {
                                        !unlocked -> "LOCKED"
                                        isCompleted -> "COMPLETED"
                                        isActiveStage -> "ACTIVE"
                                        else -> "EXPLORE"
                                    },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 8.sp,
                                    color = when {
                                        !unlocked -> SageGreen
                                        isCompleted -> SandGold
                                        else -> DeepForestGreen
                                    }
                                )
                            }
                        }
                    }
                }

                // 5. Interactive Adventurer Pin Overlay
                // Smoothly animated along the exploration trail
                val normCharX = animatedCharX / NATIVE_MAP_WIDTH
                val normCharY = animatedCharY / NATIVE_MAP_HEIGHT
                val charXDp = fitWidth * normCharX
                val charYDp = fitHeight * normCharY

                Box(
                    modifier = Modifier
                        .offset(
                            x = charXDp - 42.dp,
                            y = charYDp - 30.dp + characterBob.dp
                        )
                        .size(42.dp)
                        .clickable {
                            activeLevel?.let { onLevelClick(it) }
                        }
                        .testTag("adventurer_explorer_pin"),
                    contentAlignment = Alignment.Center
                ) {
                    AdventurerPin(companionId = activeCompanionId)
                }
            }
        }

        // --- Quick Stage Jump Selector Bar (Top Left) ---
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 10.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xD90D241C))
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InitialDataSeed.EXPLORATION_REGIONS.forEach { reg ->
                val unlocked = isRegionUnlocked(reg.stageNumber)
                val isCurrent = reg.stageNumber == activeStageNumber

                Surface(
                    onClick = {
                        if (unlocked) {
                            val normX = reg.pixelX / NATIVE_MAP_WIDTH
                            val normY = reg.pixelY / NATIVE_MAP_HEIGHT
                            val targetScale = 1.35f
                            scale = targetScale
                            val targetBaseX = normX * fitWpx
                            val targetBaseY = normY * fitHpx
                            val centeredX = viewportWpx / 2f - targetBaseX * targetScale
                            val centeredY = viewportHpx / 2f - targetBaseY * targetScale
                            val clamped = clampPanOffsets(centeredX, centeredY, targetScale)
                            offsetX = clamped.first
                            offsetY = clamped.second
                            onRegionClick(reg)
                        } else {
                            val prevRegion = InitialDataSeed.EXPLORATION_REGIONS.getOrNull(reg.stageNumber - 2)
                            lockedAlertMessage = "Selesaikan area ${prevRegion?.name ?: "sebelumnya"} untuk membuka ${reg.name}!"
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        isCurrent -> AdventureOrange
                        unlocked -> Color(0x333F5E52)
                        else -> Color(0x1A25332E)
                    },
                    modifier = Modifier.testTag("jump_stage_${reg.stageNumber}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = reg.iconEmoji, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = reg.name,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (unlocked) Color.White else Color(0xFF7A8D86)
                        )
                        if (!unlocked) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFF7A8D86),
                                modifier = Modifier.size(9.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- Map Pan & Zoom Controls (Bottom Right) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recenter Camera on Adventurer
            FloatingActionButton(
                onClick = {
                    val targetScale = 1.4f
                    scale = targetScale
                    val normX = animatedCharX / NATIVE_MAP_WIDTH
                    val normY = animatedCharY / NATIVE_MAP_HEIGHT
                    val targetBaseX = normX * fitWpx
                    val targetBaseY = normY * fitHpx
                    val centeredX = viewportWpx / 2f - targetBaseX * targetScale
                    val centeredY = viewportHpx / 2f - targetBaseY * targetScale
                    val clamped = clampPanOffsets(centeredX, centeredY, targetScale)
                    offsetX = clamped.first
                    offsetY = clamped.second
                },
                containerColor = DeepJungleGreen,
                contentColor = GoldYellow,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("recenter_explorer_button")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Center on Explorer", modifier = Modifier.size(18.dp))
            }

            // Reset Map to Whole Island View
            FloatingActionButton(
                onClick = {
                    scale = 1.0f
                    val clamped = clampPanOffsets(0f, 0f, 1.0f)
                    offsetX = clamped.first
                    offsetY = clamped.second
                },
                containerColor = DeepJungleGreen,
                contentColor = ExplorerCream,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("reset_map_button")
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = "Reset Island Map", modifier = Modifier.size(19.dp))
            }

            // Zoom In (+)
            FloatingActionButton(
                onClick = {
                    val newScale = (scale + 0.35f).coerceAtMost(3.0f)
                    val clamped = clampPanOffsets(offsetX, offsetY, newScale)
                    scale = newScale
                    offsetX = clamped.first
                    offsetY = clamped.second
                },
                containerColor = DeepJungleGreen,
                contentColor = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
            }

            // Zoom Out (-)
            FloatingActionButton(
                onClick = {
                    val newScale = (scale - 0.35f).coerceAtLeast(1.0f)
                    val clamped = clampPanOffsets(offsetX, offsetY, newScale)
                    scale = newScale
                    offsetX = clamped.first
                    offsetY = clamped.second
                },
                containerColor = DeepJungleGreen,
                contentColor = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
            }
        }

        // --- Locked Notification Snackbar / Alert ---
        lockedAlertMessage?.let { msg ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xF03B1E08),
                border = androidx.compose.foundation.BorderStroke(1.dp, AdventureOrange),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔒", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = msg,
                        color = ExplorerCream,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { lockedAlertMessage = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GoldYellow, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// 4-tuple helper for size & pixel calculations
private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun AdventurerPin(companionId: String) {
    Box(contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = AdventureOrange,
                border = androidx.compose.foundation.BorderStroke(2.dp, GoldYellow),
                shadowElevation = 6.dp,
                modifier = Modifier.size(32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.char_adventurer_explorer_1793116866013),
                    contentDescription = "Adventurer Explorer",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = GoldYellow,
                modifier = Modifier
                    .size(width = 6.dp, height = 4.dp)
                    .offset(y = (-1).dp)
            ) {}
        }

        val petEmoji = when (companionId) {
            "parrot" -> "🦜"
            "otter" -> "🦦"
            "fennec" -> "🐾"
            "hawk" -> "🦅"
            "griffin" -> "🦁"
            "phoenix" -> "🔥"
            "dragon" -> "🐉"
            else -> "🦊"
        }
        Text(
            text = petEmoji,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 6.dp, y = (-2).dp)
        )
    }
}
