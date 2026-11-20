package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.DemoRepository
import com.example.data.PodcastEpisode
import com.example.data.PodcastStyle
import com.example.ui.components.PrototypeBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.formatDurationSeconds
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.SageSoft
import com.example.viewmodel.NeuroTalksUiState

@Composable
fun PodcastScreen(
    uiState: NeuroTalksUiState,
    onSelectTopic: (String) -> Unit,
    onSelectStyle: (PodcastStyle) -> Unit,
    onSelectEpisode: (String) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSkip: (Int) -> Unit,
    onCycleSpeed: () -> Unit,
    onToggleBookmark: (String) -> Unit
) {
    val filteredEpisodes = remember(uiState.selectedPodcastTopic, uiState.selectedPodcastStyle) {
        DemoRepository.podcastEpisodes.filter { ep ->
            val topicOk = uiState.selectedPodcastTopic == "All Topics" || ep.topic == uiState.selectedPodcastTopic
            val styleOk = uiState.selectedPodcastStyle == PodcastStyle.ALL || ep.style == uiState.selectedPodcastStyle
            topicOk && styleOk
        }
    }

    val currentEp = uiState.currentEpisode

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
            // Active Episode Player Card with Editorial Artwork
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = ForestDeep,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(164.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_podcast_cover_1795175965182),
                                contentDescription = "NeuroTalks podcast studio illustration with botanical ferns",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                ForestDeep.copy(alpha = 0.2f),
                                                ForestDeep.copy(alpha = 0.85f),
                                                ForestDeep
                                            )
                                        )
                                    )
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PrototypeBadge(text = "DEMO AUDIO PLAYER", inverted = true)
                                IconButton(
                                    onClick = { onToggleBookmark(currentEp.id) },
                                    modifier = Modifier.testTag("podcast_player_bookmark")
                                ) {
                                    Icon(
                                        imageVector = if (currentEp.id in uiState.bookmarkedEpisodeIds) {
                                            Icons.Rounded.Bookmark
                                        } else {
                                            Icons.Rounded.BookmarkBorder
                                        },
                                        contentDescription = "Bookmark current episode",
                                        tint = Color(0xFFFAF6EE)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "${currentEp.topic} • ${currentEp.style.label}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SageSoft
                                )
                                Text(
                                    text = currentEp.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFFFAF6EE)
                                )
                                Text(
                                    text = currentEp.host,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SageSoft.copy(alpha = 0.85f)
                                )
                            }
                        }

                        // Scrub Slider + Playback Controls
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, bottom = 18.dp)
                        ) {
                            Slider(
                                value = uiState.playbackSeconds.toFloat(),
                                onValueChange = { onSeek(it.toInt()) },
                                valueRange = 0f..currentEp.durationSeconds.toFloat().coerceAtLeast(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFFAF6EE),
                                    activeTrackColor = SageSoft,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.22f)
                                ),
                                modifier = Modifier.testTag("podcast_progress_slider")
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = formatDurationSeconds(uiState.playbackSeconds),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SageSoft
                                )
                                Text(
                                    text = if (uiState.isPlayingAudio) "Playing preview…" else "Paused (demo timer)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SageSoft.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = formatDurationSeconds(currentEp.durationSeconds),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SageSoft
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.White.copy(alpha = 0.14f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { onCycleSpeed() }
                                        .testTag("podcast_speed_button")
                                ) {
                                    Text(
                                        text = "${uiState.playbackSpeed}x",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFFFAF6EE),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onSkip(-15) },
                                    modifier = Modifier.testTag("podcast_rewind_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Replay10,
                                        contentDescription = "Rewind 15 seconds",
                                        tint = Color(0xFFFAF6EE),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFAF6EE),
                                    modifier = Modifier
                                        .size(58.dp)
                                        .clip(CircleShape)
                                        .clickable { onTogglePlayPause() }
                                        .testTag("podcast_play_pause_button")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (uiState.isPlayingAudio) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                            contentDescription = if (uiState.isPlayingAudio) "Pause episode" else "Play episode",
                                            tint = ForestDeep,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onSkip(15) },
                                    modifier = Modifier.testTag("podcast_forward_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Forward10,
                                        contentDescription = "Forward 15 seconds",
                                        tint = Color(0xFFFAF6EE),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Rounded.GraphicEq,
                                    contentDescription = null,
                                    tint = if (uiState.isPlayingAudio) SageSoft else Color.White.copy(alpha = 0.35f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Topics Filter Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Topics",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DemoRepository.podcastTopics.forEach { topic ->
                            FilterChip(
                                selected = uiState.selectedPodcastTopic == topic,
                                onClick = { onSelectTopic(topic) },
                                label = { Text(topic) },
                                shape = RoundedCornerShape(50),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("topic_filter_${topic.lowercase().replace(" ", "_")}")
                            )
                        }
                    }
                }
            }

            // Conversation Styles Filter Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Conversation Style",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PodcastStyle.entries.forEach { style ->
                            FilterChip(
                                selected = uiState.selectedPodcastStyle == style,
                                onClick = { onSelectStyle(style) },
                                label = { Text(style.label) },
                                shape = RoundedCornerShape(50),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier.testTag("style_filter_${style.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            // Episode Library Header
            item {
                SectionHeader(
                    title = "Episode Library",
                    subtitle = "${filteredEpisodes.size} gentle listening sessions available"
                )
            }

            if (filteredEpisodes.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No episodes match both filters yet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Try switching back to All Topics or All Styles to browse the full demo library.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredEpisodes, key = { it.id }) { episode ->
                    PodcastEpisodeCard(
                        episode = episode,
                        isSelected = episode.id == uiState.currentEpisodeId,
                        isPlaying = episode.id == uiState.currentEpisodeId && uiState.isPlayingAudio,
                        isBookmarked = episode.id in uiState.bookmarkedEpisodeIds,
                        onPlaySelect = {
                            if (episode.id == uiState.currentEpisodeId) onTogglePlayPause()
                            else onSelectEpisode(episode.id)
                        },
                        onToggleBookmark = { onToggleBookmark(episode.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PodcastEpisodeCard(
    episode: PodcastEpisode,
    isSelected: Boolean,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    onPlaySelect: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    var expanded by remember(episode.id) { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onPlaySelect() }
                        .testTag("episode_play_${episode.id}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlaying) "Pause ${episode.title}" else "Play ${episode.title}",
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${episode.topic} • ${episode.style.label} • ${episode.durationMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = episode.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = episode.host,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("episode_bookmark_${episode.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = "Save episode",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = episode.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (expanded) "Hide gentle takeaways" else "View gentle takeaways & prompt",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Key Takeaways",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    episode.takeaways.forEach { point ->
                        Text(
                            text = "• $point",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reflection Prompt: ${episode.reflectionPrompt}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
