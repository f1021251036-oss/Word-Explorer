package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.LearningPath
import com.example.data.models.LevelInfo
import com.example.data.models.LevelStatus
import com.example.ui.theme.*

@Composable
fun IsometricWorldMap(
    path: LearningPath,
    levels: List<LevelInfo>,
    activeCompanionId: String,
    onLevelClick: (LevelInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    // Find active level (highest unlocked level)
    val activeLevel = levels.lastOrNull { it.status == LevelStatus.AVAILABLE }
        ?: levels.lastOrNull { it.status == LevelStatus.COMPLETED }
        ?: levels.firstOrNull()

    // Map Dimensions matching the 16:9 illustrated diorama world
    val canvasWidth = 2400f
    val canvasHeight = 1350f

    // Pan & Zoom state
    var scale by remember { mutableStateOf(0.65f) }
    var offsetX by remember { mutableStateOf(-300f) }
    var offsetY by remember { mutableStateOf(-300f) }

    // Pulsing animation for active level and character
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val characterBob by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "characterBob"
    )

    // Center camera on active level if available on first launch
    LaunchedEffect(activeLevel?.id) {
        activeLevel?.let {
            offsetX = -it.posX * scale + 400f
            offsetY = -it.posY * scale + 500f
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F261E))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        // --- Pan & Zoom Container ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = offsetX
                    translationY = offsetY
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                }
        ) {
            // 1. High-Resolution Illustrated 3D Isometric Fantasy World Backdrop
            Image(
                painter = painterResource(id = R.drawable.map_world_illustrated_1793116848643),
                contentDescription = "Fantasy Adventure World Diorama",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(2400.dp, 1350.dp)
                    .clip(RoundedCornerShape(32.dp))
            )

            // 2. Adventure Trail Overlay & Connectors
            Canvas(
                modifier = Modifier.size(2400.dp, 1350.dp)
            ) {
                drawAdventureTrail(levels)
                drawCheckpointGates(levels)
            }

            // 3. Interactive Level Nodes Layer
            levels.forEach { level ->
                val isCurrentActive = activeLevel?.id == level.id

                Box(
                    modifier = Modifier
                        .offset { IntOffset(level.posX.toInt() - 25, level.posY.toInt() - 25) }
                        .size(50.dp)
                        .clickable(enabled = level.status != LevelStatus.LOCKED) {
                            onLevelClick(level)
                        }
                        .testTag("level_node_${level.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    when (level.status) {
                        LevelStatus.LOCKED -> {
                            // Locked Stone Node
                            Surface(
                                shape = CircleShape,
                                color = Color(0xDD3A4742),
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF242F2C)),
                                shadowElevation = 4.dp,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked Level ${level.id}",
                                        tint = Color(0xFFA0AFA8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        LevelStatus.AVAILABLE -> {
                            // Active / Available Golden Pulsing Node
                            Box(
                                modifier = Modifier
                                    .size((48 * pulseScale).dp)
                                    .clip(CircleShape)
                                    .background(GoldYellow.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = GoldYellow,
                                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFFFF9D6)),
                                    shadowElevation = 8.dp,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = level.id,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            color = Color(0xFF4A2C00)
                                        )
                                        Text(
                                            text = "PLAY",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp,
                                            color = Color(0xFF7A4A00)
                                        )
                                    }
                                }
                            }
                        }
                        LevelStatus.COMPLETED -> {
                            // Completed Golden Medal Node with Stars
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF2E7D32),
                                border = androidx.compose.foundation.BorderStroke(2.5.dp, GoldYellow),
                                shadowElevation = 6.dp,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = level.id,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(top = 1.dp)
                                    ) {
                                        repeat(level.stars) {
                                            Text(text = "⭐", fontSize = 7.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Render Adventurer Character directly on the active level!
                    if (isCurrentActive) {
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(14, -42 + characterBob.toInt()) }
                                .size(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AdventurerPin(companionId = activeCompanionId)
                        }
                    }
                }
            }
        }

        // --- Quick Stage Jump Selector Overlay (Top Left) ---
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xDD112B21))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val stages = listOf(
                "S1: Forest" to Pair(500f, 850f),
                "S2: Island" to Pair(1450f, 900f),
                "S3: Ruins" to Pair(750f, 420f),
                "S4: Peak" to Pair(1280f, 250f),
                "S5: Cave" to Pair(1750f, 550f)
            )
            stages.forEachIndexed { idx, (label, target) ->
                Surface(
                    onClick = {
                        offsetX = -target.first * scale + 380f
                        offsetY = -target.second * scale + 550f
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Transparent,
                    modifier = Modifier.testTag("jump_stage_${idx + 1}")
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ExplorerCream,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // --- Zoom & Recenter Controls Overlay (Bottom Right) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Recenter Camera on Explorer
            FloatingActionButton(
                onClick = {
                    activeLevel?.let {
                        offsetX = -it.posX * scale + 380f
                        offsetY = -it.posY * scale + 550f
                    }
                },
                containerColor = DeepJungleGreen,
                contentColor = GoldYellow,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("recenter_explorer_button")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Center on Explorer", modifier = Modifier.size(22.dp))
            }

            // Zoom In
            FloatingActionButton(
                onClick = { if (scale < 1.4f) scale += 0.2f },
                containerColor = DeepJungleGreen,
                contentColor = Color.White,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(22.dp))
            }

            // Zoom Out
            FloatingActionButton(
                onClick = { if (scale > 0.45f) scale -= 0.2f },
                containerColor = DeepJungleGreen,
                contentColor = Color.White,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun AdventurerPin(companionId: String) {
    Box(contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Stylized 3D Little Explorer Character Image
            Surface(
                shape = CircleShape,
                color = AdventureOrange,
                border = androidx.compose.foundation.BorderStroke(2.5.dp, GoldYellow),
                shadowElevation = 8.dp,
                modifier = Modifier.size(40.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.char_adventurer_explorer_1793116866013),
                    contentDescription = "Adventurer Explorer",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Pointer Triangle
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = GoldYellow,
                modifier = Modifier
                    .size(width = 8.dp, height = 6.dp)
                    .offset(y = (-1).dp)
            ) {}
        }

        // Accompanying Pet Emoji Badge
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
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp, y = (-6).dp)
        )
    }
}

// Draw the distinct yellow-stone bordered winding adventure trail
private fun DrawScope.drawAdventureTrail(levels: List<LevelInfo>) {
    if (levels.isEmpty()) return

    val trailPath = Path().apply {
        moveTo(levels[0].posX, levels[0].posY)
        for (i in 1 until levels.size) {
            val prev = levels[i - 1]
            val curr = levels[i]
            val midX = (prev.posX + curr.posX) / 2f
            val midY = (prev.posY + curr.posY) / 2f
            quadraticTo(prev.posX, prev.posY, midX, midY)
        }
        val last = levels.last()
        lineTo(last.posX, last.posY)
    }

    // 1. Glowing Yellow Stone Border (Outer layer, 18px wide)
    drawPath(
        path = trailPath,
        color = Color(0xFFF4C430).copy(alpha = 0.85f),
        style = Stroke(
            width = 18f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // 2. Gray Cobblestone Walking Path (Inner layer, 10px wide)
    drawPath(
        path = trailPath,
        color = Color(0xFFECEFF1).copy(alpha = 0.9f),
        style = Stroke(
            width = 10f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

// Draw Checkpoint Banners/Gates at Level 10 of each Stage
private fun DrawScope.drawCheckpointGates(levels: List<LevelInfo>) {
    val checkpointLevelIds = listOf("1-10", "2-10", "3-10", "4-10", "5-10")
    levels.filter { it.id in checkpointLevelIds }.forEach { checkpoint ->
        val x = checkpoint.posX
        val y = checkpoint.posY

        // Checkpoint Flag Post
        drawLine(
            color = Color(0xFFF4C430),
            start = Offset(x + 22f, y + 16f),
            end = Offset(x + 22f, y - 40f),
            strokeWidth = 4f
        )
        // Red / Gold Checkpoint Flag
        val flag = Path().apply {
            moveTo(x + 22f, y - 40f)
            lineTo(x + 50f, y - 28f)
            lineTo(x + 22f, y - 16f)
            close()
        }
        drawPath(flag, Color(0xFFD32F2F))
    }
}
