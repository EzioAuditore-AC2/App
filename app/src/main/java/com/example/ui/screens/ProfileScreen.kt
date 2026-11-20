package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WavingHand
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ActivityEntry
import com.example.data.DemoSettings
import com.example.ui.components.NeuroTalksBrandMark
import com.example.ui.components.PrototypeBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.SoftDisclaimerCard
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.SageSoft
import com.example.viewmodel.NeuroTalksUiState

@Composable
fun ProfileScreen(
    uiState: NeuroTalksUiState,
    onCycleAlias: () -> Unit,
    onUpdateSettings: ((DemoSettings) -> DemoSettings) -> Unit,
    onResetDemoData: () -> Unit,
    onOpenGuidelines: () -> Unit,
    onOpenEmergency: () -> Unit,
    onReplayWelcome: () -> Unit
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Anonymous Demo Identity Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = ForestDeep,
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PrototypeBadge(text = "ANONYMOUS DEMO PROFILE", inverted = true)
                            NeuroTalksBrandMark(size = 40.dp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SageSoft,
                                modifier = Modifier.size(58.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = uiState.userAlias
                                            .split(" ")
                                            .mapNotNull { it.firstOrNull()?.uppercase() }
                                            .take(2)
                                            .joinToString(""),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = ForestDeep,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.userAlias,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color(0xFFFAF6EE)
                                )
                                Text(
                                    text = "No sign-in • Zero personal data stored",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SageSoft
                                )
                            }
                        }

                        Button(
                            onClick = onCycleAlias,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFAF6EE),
                                contentColor = ForestDeep
                            ),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("profile_switch_alias_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shuffle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Switch Anonymous Alias", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Local Progress Overview
            item {
                SectionHeader(
                    title = "Your Gentle Progress",
                    subtitle = "Tracked locally during this prototype preview"
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        icon = Icons.Rounded.Spa,
                        value = "${uiState.checkInsCompleted}",
                        label = "Check-ins",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Rounded.Headphones,
                        value = "${uiState.bookmarkedEpisodeIds.size}",
                        label = "Saved Audio",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Rounded.SelfImprovement,
                        value = "${uiState.simulationsCompletedCount}",
                        label = "Simulations",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Rounded.Forum,
                        value = "${uiState.notesSharedCount}",
                        label = "Notes",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Wellness Rhythm Progress Bar Card
            item {
                val weeklySteps = (
                    uiState.checkInsCompleted +
                        uiState.bookmarkedEpisodeIds.size +
                        uiState.simulationsCompletedCount +
                        uiState.notesSharedCount
                    ).coerceAtMost(10)
                val progressFraction = weeklySteps / 10f

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Prototype Exploration Rhythm",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Text(
                                text = "$weeklySteps / 10 steps",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50)),
                            color = ForestPrimary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer,
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "Try checking in, saving a podcast, completing a simulation, or replying in the forum to watch your demo activity grow.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Recent Activity Feed
            item {
                SectionHeader(
                    title = "Recent Local Activity",
                    subtitle = "Updates automatically as you test each screen"
                )
            }

            items(uiState.activities.take(5), key = { it.id }) { entry ->
                ActivityRowCard(entry = entry)
            }

            // Settings & Preferences
            item {
                SectionHeader(
                    title = "Prototype Settings",
                    subtitle = "Customize how NeuroTalks feels on your device"
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        SettingToggleRow(
                            icon = Icons.Rounded.DarkMode,
                            title = "Deep Canopy Dark Theme",
                            subtitle = "Switch between warm cream and night forest colors",
                            checked = uiState.settings.darkCanopyTheme,
                            onCheckedChange = { enabled ->
                                onUpdateSettings { it.copy(darkCanopyTheme = enabled) }
                            },
                            testTag = "setting_dark_theme_switch"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        SettingToggleRow(
                            icon = Icons.Rounded.Spa,
                            title = "Daily Mood Check-in Card",
                            subtitle = "Show quick reflection prompt on Home dashboard",
                            checked = uiState.settings.showDailyCheckIn,
                            onCheckedChange = { enabled ->
                                onUpdateSettings { it.copy(showDailyCheckIn = enabled) }
                            },
                            testTag = "setting_checkin_switch"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        SettingToggleRow(
                            icon = Icons.Rounded.Headphones,
                            title = "Calm Audio Cues (Demo)",
                            subtitle = "Gentle visual audio indicators on Podcast screen",
                            checked = uiState.settings.calmAudioCues,
                            onCheckedChange = { enabled ->
                                onUpdateSettings { it.copy(calmAudioCues = enabled) }
                            },
                            testTag = "setting_audio_cues_switch"
                        )
                    }
                }
            }

            // Quick Navigation Links
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        ProfileNavRow(
                            icon = Icons.Rounded.GppGood,
                            title = "Review Community Guidelines",
                            subtitle = "Respect, safety, confidentiality, and reporting",
                            onClick = onOpenGuidelines,
                            testTag = "profile_nav_guidelines"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        ProfileNavRow(
                            icon = Icons.Rounded.HealthAndSafety,
                            title = "Emergency & Crisis Support",
                            subtitle = "Box breathing and verified 24/7 hotlines",
                            tint = MaterialTheme.colorScheme.error,
                            onClick = onOpenEmergency,
                            testTag = "profile_nav_emergency"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        ProfileNavRow(
                            icon = Icons.Rounded.WavingHand,
                            title = "Replay Welcome Screen",
                            subtitle = "View the NeuroTalks introduction again",
                            onClick = onReplayWelcome,
                            testTag = "profile_nav_welcome"
                        )
                    }
                }
            }

            // Reset Local Demo Data Button
            item {
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("profile_reset_demo_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Local Demo Progress")
                }
            }

            item {
                SoftDisclaimerCard(
                    title = "Privacy & Prototype Guarantee",
                    message = "This NeuroTalks build has no user accounts, no cloud database, and collects zero personal data. All progress resets if the app is restarted."
                )
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset local demo data?") },
            text = {
                Text("This clears your session mood check-in, custom demo posts, replies, and activity history back to the initial prototype state.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDemoData()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary)
                ) {
                    Text("Reset Demo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ActivityRowCard(entry: ActivityEntry) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = entry.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = entry.detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                text = entry.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = ForestPrimary,
                checkedThumbColor = Color(0xFFFAF6EE)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun ProfileNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}
