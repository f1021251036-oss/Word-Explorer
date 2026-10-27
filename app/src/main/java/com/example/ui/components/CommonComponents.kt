package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LearningPath
import com.example.data.models.UserProfile
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerTopBar(
    profile: UserProfile?,
    selectedPath: LearningPath,
    onPathSelected: (LearningPath) -> Unit,
    onLogoutClick: () -> Unit
) {
    var showPathMenu by remember { mutableStateOf(false) }

    Surface(
        color = DeepJungleGreen,
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
            // Path Indicator with Dropdown
            Box {
                Surface(
                    onClick = { showPathMenu = true },
                    shape = RoundedCornerShape(20.dp),
                    color = AdventureOrange,
                    shadowElevation = 2.dp,
                    modifier = Modifier.testTag("path_selector_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🗺️ ${selectedPath.code}",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Switch Path",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
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
                                            fontWeight = if (path == selectedPath) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (!isAllowed) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Locked",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = path.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
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

            // Streak & Daily Target Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Streak badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF24483B),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile?.currentStreak ?: 0}d",
                            color = GoldYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Companion Icon
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF24483B),
                    modifier = Modifier
                        .size(36.dp)
                        .padding(end = 4.dp),
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = when (profile?.activeCompanionId) {
                                "parrot" -> "🦜"
                                "otter" -> "🦦"
                                "fennec" -> "🐾"
                                "hawk" -> "🦅"
                                "griffin" -> "🦁"
                                "phoenix" -> "🔥"
                                "dragon" -> "🐉"
                                else -> "🦊"
                            },
                            fontSize = 18.sp
                        )
                    }
                }

                // Logout button
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("logout_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = ExplorerCream
                    )
                }
            }
        }
    }
}

@Composable
fun ExplorerBottomNavBar(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = DeepJungleGreen,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf(
            Triple("map", "Map", Icons.Default.Map to Icons.Outlined.Map),
            Triple("dashboard", "Camp", Icons.Default.Home to Icons.Outlined.Home),
            Triple("vocab", "Lexicon", Icons.Default.MenuBook to Icons.Outlined.MenuBook),
            Triple("stories", "Lore", Icons.Default.AutoStories to Icons.Outlined.AutoStories),
            Triple("companions", "Beasts", Icons.Default.Pets to Icons.Outlined.Pets)
        )

        items.forEach { (route, label, icons) ->
            val isSelected = currentTab == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepJungleGreen,
                    selectedTextColor = ExplorerCream,
                    indicatorColor = GoldYellow,
                    unselectedIconColor = ExplorerCream.copy(alpha = 0.7f),
                    unselectedTextColor = ExplorerCream.copy(alpha = 0.7f)
                ),
                modifier = Modifier.testTag("nav_tab_$route")
            )
        }
    }
}
