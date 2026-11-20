package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.MoodOption
import com.example.ui.components.NeuroTalksBrandMark
import com.example.ui.components.PrototypeBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.SoftDisclaimerCard
import com.example.ui.theme.ClayContainer
import com.example.ui.theme.ClayWarm
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageSoft
import com.example.viewmodel.NeuroTalksUiState
import java.util.Calendar

@Composable
fun HomeScreen(
    uiState: NeuroTalksUiState,
    onSelectMood: (MoodOption) -> Unit,
    onNavigatePodcast: () -> Unit,
    onNavigateSimulation: () -> Unit,
    onNavigateForum: () -> Unit,
    onNavigateEmergency: () -> Unit,
    onNavigateGuidelines: () -> Unit,
    onNavigateProfile: () -> Unit
) {
    val greeting = timeOfDayGreeting()

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
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Hero Greeting Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = ForestDeep,
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                NeuroTalksBrandMark(size = 40.dp)
                                PrototypeBadge(text = "PROTOTYPE DEMO", inverted = true)
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.14f),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { onNavigateProfile() }
                                    .testTag("home_profile_chip")
                            ) {
                                Text(
                                    text = uiState.userAlias,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFFAF6EE),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }

                        Text(
                            text = "$greeting,\n${uiState.userAlias}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color(0xFFFAF6EE)
                        )
                        Text(
                            text = "Take a slow breath. Everything here is local demo content you can explore at your own pace.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SageSoft
                        )
                    }
                }
            }

            // Gentle Daily Mood Check-in
            if (uiState.settings.showDailyCheckIn) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Spa,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "How does your mind feel right now?",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MoodOption.entries.forEach { mood ->
                                    val selected = uiState.selectedMood == mood
                                    FilterChip(
                                        selected = selected,
                                        onClick = { onSelectMood(mood) },
                                        label = { Text(mood.label) },
                                        shape = RoundedCornerShape(50),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier.testTag("mood_chip_${mood.name.lowercase()}")
                                    )
                                }
                            }

                            val feedbackText = uiState.selectedMood?.let {
                                "${it.label}: ${it.tagline}. Saved to your local session activity."
                            } ?: "Tap a gentle check-in word to tailor today's reflection."

                            Text(
                                text = feedbackText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Core Spaces Section
            item {
                SectionHeader(
                    title = "Explore Your Spaces",
                    subtitle = "Choose whatever supports you most right now"
                )
            }

            // Feature Card 1: NeuroTalks Podcast
            item {
                val activePodcast = uiState.podcastFeed.selectedEpisode
                FeatureHeroCard(
                    title = "NeuroTalks Podcast",
                    subtitle = "Featured: ${activePodcast.title}",
                    badge = "${activePodcast.category.label} • ${activePodcast.provider.displayName}",
                    icon = Icons.Rounded.Headphones,
                    accentBg = SageContainer,
                    accentTint = ForestDeep,
                    ctaLabel = if (uiState.isPlayingAudio) "Now Playing" else "Listen Now",
                    onClick = onNavigatePodcast,
                    testTag = "home_card_podcast"
                )
            }

            // Two-Column Bento Cards: Ideal Life Simulation + Community Forum
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureCompactCard(
                        title = "Ideal Life Simulation",
                        subtitle = "Customize your avatar & explore Willowbend",
                        icon = Icons.Rounded.SelfImprovement,
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f),
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = onNavigateSimulation,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_card_simulation")
                    )
                    FeatureCompactCard(
                        title = "Community Forum",
                        subtitle = "${uiState.forumPosts.count { !it.isReported }} anonymous demo stories & replies",
                        icon = Icons.Rounded.Forum,
                        containerColor = ClayContainer.copy(alpha = 0.65f),
                        iconTint = ClayWarm,
                        onClick = onNavigateForum,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_card_forum")
                    )
                }
            }

            // Emergency Support Banner Card (Always Easy to Reach)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onNavigateEmergency() }
                        .testTag("home_card_emergency"),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.HealthAndSafety,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Emergency & Crisis Support",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Box breathing & verified 24/7 crisis hotlines",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Open emergency support",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Guidelines & Progress Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickLinkPillCard(
                        title = "Guidelines",
                        subtitle = "Safety & values",
                        icon = Icons.Rounded.GppGood,
                        onClick = onNavigateGuidelines,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_card_guidelines")
                    )
                    QuickLinkPillCard(
                        title = "Your Progress",
                        subtitle = "${uiState.simulationsCompletedCount} sims • ${uiState.checkInsCompleted} check-ins",
                        icon = Icons.Rounded.AutoAwesome,
                        onClick = onNavigateProfile,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_card_progress")
                    )
                }
            }

            // Prototype Reminder Footer
            item {
                SoftDisclaimerCard(
                    title = "Local Prototype Mode",
                    message = "All stories, audio previews, and progress stats stay locally in this preview. NeuroTalks does not provide therapy or clinical diagnosis."
                )
            }
        }
    }
}

@Composable
private fun FeatureHeroCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentBg: Color,
    accentTint: Color,
    ctaLabel: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(accentBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentTint,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = ctaLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureCompactCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(178.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
private fun QuickLinkPillCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Column {
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
        }
    }
}

private fun timeOfDayGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
}
