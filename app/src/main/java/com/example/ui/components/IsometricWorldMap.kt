package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
    onReplayGuide: () -> Unit = {},
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
    var showLegend by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(ShadowDark)
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

                // 2. Connecting Route Curve Canvas behind markers (distinguishes traversed vs locked trails)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pathWidthPx = 3.dp.toPx()
                    val casingWidthPx = 6.dp.toPx()
                    val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))

                    // Draw connecting path line between consecutive level points
                    for (i in 0 until levels.size - 1) {
                        val curr = levels[i]
                        val next = levels[i + 1]

                        val startX = (curr.posX / NATIVE_MAP_WIDTH) * size.width
                        val startY = (curr.posY / NATIVE_MAP_HEIGHT) * size.height
                        val endX = (next.posX / NATIVE_MAP_WIDTH) * size.width
                        val endY = (next.posY / NATIVE_MAP_HEIGHT) * size.height

                        val isTraversedOrOpen = curr.status == LevelStatus.COMPLETED || next.status != LevelStatus.LOCKED

                        if (isTraversedOrOpen) {
                            // Inked dark casing + vivid warm gold core
                            drawLine(
                                color = ShadowDark.copy(alpha = 0.65f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = casingWidthPx,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = HoneyHover,
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = pathWidthPx,
                                cap = StrokeCap.Round
                            )
                        } else {
                            // Subtle dark backing + dashed uncharted locked trail
                            drawLine(
                                color = ShadowDark.copy(alpha = 0.40f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = pathWidthPx * 1.4f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = CreamSoft.copy(alpha = 0.72f),
                                start = Offset(startX, startY),
                                end = Offset(endX, endY),
                                strokeWidth = pathWidthPx * 0.75f,
                                pathEffect = dashedEffect,
                                cap = StrokeCap.Round
                            )
                        }
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
                    val visibleSize = if (isCurrent) 38.dp else 32.dp

                    Box(
                        modifier = Modifier
                            .offset(
                                x = nodeXDp - 24.dp,
                                y = nodeYDp - 24.dp
                            )
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (isLocked) {
                                    val prevLevel = levels.find {
                                        it.stageNumber == level.stageNumber && it.levelNumber == level.levelNumber - 1
                                    } ?: levels.find { it.stageNumber == level.stageNumber - 1 && it.levelNumber == 10 }
                                    val req = prevLevel?.let { "Level ${it.stageNumber}-${it.levelNumber}" } ?: "level sebelumnya"
                                    lockedAlertMessage = "Selesaikan $req terlebih dahulu untuk membuka Level ${level.stageNumber}-${level.levelNumber}!"
                                } else {
                                    onLevelClick(level)
                                }
                            }
                            .testTag("level_node_${level.stageNumber}_${level.levelNumber}"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulsing outer halo for active level
                        if (isCurrent) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Sand.copy(alpha = pulseAlpha * 0.55f),
                                    radius = (size.minDimension / 2.1f) * pulseScale
                                )
                                drawCircle(
                                    color = PureWhite.copy(alpha = 0.90f),
                                    radius = (size.minDimension / 2.25f) * pulseScale,
                                    style = Stroke(width = 2.5f)
                                )
                            }
                        }

                        // Circular Node Surface
                        Surface(
                            shape = CircleShape,
                            color = when {
                                isCurrent -> Honey
                                isCompleted -> Leaf
                                isAvailable -> PureWhite
                                else -> DisabledBg
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isCurrent) 2.5.dp else 2.dp,
                                color = when {
                                    isCurrent -> Forest900
                                    isCompleted -> PureWhite
                                    isAvailable -> Forest700
                                    else -> SageDark
                                }
                            ),
                            shadowElevation = if (isCurrent) 8.dp else 4.dp,
                            modifier = Modifier.size(visibleSize)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                when {
                                    isLocked -> {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Terkunci Level ${level.stageNumber}-${level.levelNumber}",
                                            tint = DisabledFg,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    isCompleted -> {
                                        Text(
                                            text = "${level.levelNumber}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = PureWhite
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = "${level.levelNumber}",
                                            fontSize = if (isCurrent) 14.sp else 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Forest900
                                        )
                                    }
                                }
                            }
                        }

                        // Crisp Vector Star Pips below completed nodes (replaces tiny blurry emoji)
                        if (isCompleted && level.stars > 0) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = 2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Forest900)
                                    .border(1.dp, Sand, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(level.stars.coerceIn(1, 3)) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Honey,
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        }

                        // Crisp Vector Milestone Badge on Level 10
                        if (isCheckpoint) {
                            Surface(
                                shape = CircleShape,
                                color = Sand,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Forest900),
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-1).dp, y = (-1).dp)
                                    .size(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = "Milestone Level 10",
                                        tint = Forest900,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Prominent Compact PLAY Pill directly below the active level
                    if (isCurrent) {
                        Surface(
                            onClick = { onLevelClick(level) },
                            shape = RoundedCornerShape(12.dp),
                            color = Forest900,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Honey),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .offset(
                                    x = nodeXDp - 31.dp,
                                    y = nodeYDp + 24.dp
                                )
                                .testTag("active_play_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Level ${level.stageNumber}-${level.levelNumber}",
                                    tint = Honey,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "PLAY",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp,
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

                    // Interactive Compact Floating Region Pill near the artwork banner
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
                            !unlocked -> PureWhite.copy(alpha = 0.94f)
                            isCompleted -> Leaf
                            isActiveStage -> Honey
                            else -> Cream
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            when {
                                !unlocked -> SageDark
                                isCompleted -> Sand
                                isActiveStage -> Forest900
                                else -> Forest700
                            }
                        ),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .offset(
                                x = bannerOffsetXDp - 38.dp,
                                y = bannerOffsetYDp - 14.dp
                            )
                            .testTag("region_banner_${region.name.lowercase().replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = region.iconEmoji, fontSize = 11.sp)
                            Text(
                                text = region.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isCompleted) PureWhite else Forest900
                            )
                            when {
                                !unlocked -> Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Terkunci",
                                    tint = SageDark,
                                    modifier = Modifier.size(11.dp)
                                )
                                isCompleted -> Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selesai",
                                    tint = PureWhite,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }

                // 5. Interactive Adventurer Pin Overlay
                // Centered directly above the active node with pointer stem
                val normCharX = animatedCharX / NATIVE_MAP_WIDTH
                val normCharY = animatedCharY / NATIVE_MAP_HEIGHT
                val charXDp = fitWidth * normCharX
                val charYDp = fitHeight * normCharY

                Box(
                    modifier = Modifier
                        .offset(
                            x = charXDp - 22.dp,
                            y = charYDp - 50.dp + characterBob.dp
                        )
                        .size(44.dp)
                        .clip(CircleShape)
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

        // --- Quick Stage Jump Selector Bar (Top Left, horizontally scrollable without colliding with top-right stats) ---
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 10.dp, top = 10.dp, end = 152.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Forest900.copy(alpha = 0.92f))
                .border(1.dp, DarkBorderSubtle, RoundedCornerShape(18.dp))
                .horizontalScroll(rememberScrollState())
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
                        isCurrent -> Honey
                        unlocked -> Forest500
                        else -> Forest900
                    },
                    modifier = Modifier.testTag("jump_stage_${reg.stageNumber}")
                ) {
                    val shortRegionLabel = if (reg.name == "Ancient Rome") "Rome" else reg.name
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = reg.iconEmoji, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = shortRegionLabel,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = when {
                                isCurrent -> Forest900
                                unlocked -> PureWhite
                                else -> SageLight
                            }
                        )
                        if (!unlocked) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = SageLight,
                                modifier = Modifier.size(9.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- Map Legend Toggle & Expanded Key (Bottom Left) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (showLegend) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PureWhite,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Forest700),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .widthIn(max = 224.dp)
                        .testTag("map_legend_card")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LEGENDA PETA",
                                fontFamily = CinzelFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Forest900
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup legenda",
                                tint = SageDark,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { showLegend = false }
                            )
                        }
                        HorizontalDivider(color = Sage.copy(alpha = 0.4f))
                        LegendNodeRow(
                            fillColor = Honey,
                            borderColor = Forest900,
                            label = "Level Aktif Sekarang"
                        )
                        LegendNodeRow(
                            fillColor = Leaf,
                            borderColor = PureWhite,
                            label = "Selesai (+ Bintang)"
                        )
                        LegendNodeRow(
                            fillColor = PureWhite,
                            borderColor = Forest700,
                            label = "Terbuka & Siap Dimainkan"
                        )
                        LegendNodeRow(
                            fillColor = DisabledBg,
                            borderColor = SageDark,
                            label = "Masih Terkunci",
                            showLock = true
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Sand,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Forest900),
                                modifier = Modifier.size(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = Forest900,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Level 10 (Pos Wilayah)",
                                fontSize = 11.sp,
                                color = Forest900,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        HorizontalDivider(color = Sage.copy(alpha = 0.35f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Canvas(modifier = Modifier.size(width = 16.dp, height = 6.dp)) {
                                drawLine(
                                    color = Honey,
                                    start = Offset(0f, size.height / 2f),
                                    end = Offset(size.width, size.height / 2f),
                                    strokeWidth = 2.5.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                            Text(
                                text = "Jalur Terbuka / Selesai",
                                fontSize = 11.sp,
                                color = SageDark
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Canvas(modifier = Modifier.size(width = 16.dp, height = 6.dp)) {
                                drawLine(
                                    color = SageDark,
                                    start = Offset(0f, size.height / 2f),
                                    end = Offset(size.width, size.height / 2f),
                                    strokeWidth = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f)),
                                    cap = StrokeCap.Round
                                )
                            }
                            Text(
                                text = "Jalur Belum Terbuka",
                                fontSize = 11.sp,
                                color = SageDark
                            )
                        }
                        HorizontalDivider(color = Sage.copy(alpha = 0.35f))
                        Surface(
                            onClick = {
                                showLegend = false
                                onReplayGuide()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = HoneyTint,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Forest700),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 44.dp)
                                .testTag("legend_replay_guide_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = null,
                                    tint = Forest900,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Putar Ulang Panduan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Forest900
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                onClick = { showLegend = !showLegend },
                shape = RoundedCornerShape(14.dp),
                color = Forest900.copy(alpha = 0.94f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Sand),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                    .testTag("map_legend_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Legenda Peta",
                        tint = Sand,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (showLegend) "Tutup" else "Legenda",
                        color = PureWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- Grouped Map Pan & Zoom Dock (Bottom Right, >= 48dp touch targets) ---
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Forest900.copy(alpha = 0.94f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DarkBorderSubtle),
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 18.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Recenter Camera on Adventurer
                IconButton(
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
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("recenter_explorer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Center on Explorer",
                        tint = Honey,
                        modifier = Modifier.size(20.dp)
                    )
                }

                HorizontalDivider(
                    color = DarkBorderSubtle.copy(alpha = 0.7f),
                    modifier = Modifier.width(28.dp)
                )

                // Reset Map to Whole Island View
                IconButton(
                    onClick = {
                        scale = 1.0f
                        val clamped = clampPanOffsets(0f, 0f, 1.0f)
                        offsetX = clamped.first
                        offsetY = clamped.second
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("reset_map_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset Island Map",
                        tint = Cream,
                        modifier = Modifier.size(20.dp)
                    )
                }

                HorizontalDivider(
                    color = DarkBorderSubtle.copy(alpha = 0.7f),
                    modifier = Modifier.width(28.dp)
                )

                // Zoom In (+)
                IconButton(
                    onClick = {
                        val newScale = (scale + 0.35f).coerceAtMost(3.0f)
                        val clamped = clampPanOffsets(offsetX, offsetY, newScale)
                        scale = newScale
                        offsetX = clamped.first
                        offsetY = clamped.second
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("zoom_in_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                HorizontalDivider(
                    color = DarkBorderSubtle.copy(alpha = 0.7f),
                    modifier = Modifier.width(28.dp)
                )

                // Zoom Out (-)
                IconButton(
                    onClick = {
                        val newScale = (scale - 0.35f).coerceAtLeast(1.0f)
                        val clamped = clampPanOffsets(offsetX, offsetY, newScale)
                        scale = newScale
                        offsetX = clamped.first
                        offsetY = clamped.second
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("zoom_out_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // --- Locked Notification Snackbar / Alert ---
        lockedAlertMessage?.let { msg ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Forest900.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Honey),
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
                        color = Cream,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { lockedAlertMessage = null },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Sand, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendNodeRow(
    fillColor: Color,
    borderColor: Color,
    label: String,
    showLock: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = fillColor,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
            shadowElevation = 1.dp,
            modifier = Modifier.size(16.dp)
        ) {
            if (showLock) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = DisabledFg,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = Forest900,
            fontWeight = FontWeight.Medium
        )
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
                color = Honey,
                border = androidx.compose.foundation.BorderStroke(2.dp, Forest900),
                shadowElevation = 6.dp,
                modifier = Modifier.size(34.dp)
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
                color = Forest900,
                border = androidx.compose.foundation.BorderStroke(1.dp, Sand),
                modifier = Modifier
                    .size(width = 7.dp, height = 5.dp)
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
