package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.podcast.PodcastCategory
import com.example.podcast.RealPodcastEpisode
import com.example.ui.components.SectionHeader
import com.example.viewmodel.NeuroTalksUiState
import kotlinx.coroutines.launch

@Composable
fun PodcastScreen(
    uiState: NeuroTalksUiState,
    onRefreshFeeds: (Boolean) -> Unit,
    onSelectCategory: (PodcastCategory) -> Unit,
    onToggleStreamableOnly: () -> Unit,
    onSelectEpisode: (String) -> Unit,
    onPlayStreamableEpisode: (RealPodcastEpisode) -> Unit,
    onToggleMedia3PlayPause: () -> Unit,
    onSeekMedia3Ms: (Long) -> Unit,
    onSkipMedia3Ms: (Long) -> Unit,
    onCycleMedia3Speed: () -> Unit,
    onRecordExternalOpen: (RealPodcastEpisode, String) -> Unit,
    onToggleBookmark: (String) -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val feedState = uiState.podcastFeed
    val filteredEpisodes = feedState.filteredEpisodes
    val activeStreamEpisode = remember(feedState.player.activeStreamEpisodeId, feedState.allEpisodes) {
        feedState.allEpisodes.firstOrNull { it.id == feedState.player.activeStreamEpisodeId }
    }
    var detailSheetEpisode by remember { mutableStateOf<RealPodcastEpisode?>(null) }

    LaunchedEffect(Unit) {
        onRefreshFeeds(false)
    }

    fun launchExternalPodcast(episode: RealPodcastEpisode, url: String, platformLabel: String) {
        val opened = openExternalUrl(context, url)
        if (opened) {
            onRecordExternalOpen(episode, platformLabel)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("No app or browser available to open $platformLabel.")
            }
        }
    }

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
            // Top Hero: Active Media3 In-App Player OR Featured Verified Podcast Card
            item {
                if (feedState.player.activeStreamEpisodeId != null) {
                    Media3ActivePlayerCard(
                        player = feedState.player,
                        activeEpisode = activeStreamEpisode,
                        isBookmarked = feedState.player.activeStreamEpisodeId in feedState.bookmarkedIds,
                        onTogglePlayPause = onToggleMedia3PlayPause,
                        onSeekMs = onSeekMedia3Ms,
                        onSkipMs = onSkipMedia3Ms,
                        onCycleSpeed = onCycleMedia3Speed,
                        onToggleBookmark = {
                            feedState.player.activeStreamEpisodeId?.let(onToggleBookmark)
                        },
                        onOpenExternal = { ep ->
                            launchExternalPodcast(ep, ep.externalUrl, ep.provider.displayName)
                        }
                    )
                } else {
                    val featured = feedState.selectedEpisode
                    FeaturedPodcastHeroCard(
                        featured = featured,
                        isBookmarked = featured.id in feedState.bookmarkedIds,
                        isLoadingFeeds = feedState.isLoadingFeeds,
                        feedStatusNote = feedState.feedStatusNote,
                        onListenNow = {
                            if (featured.supportsInAppPlayback) {
                                onPlayStreamableEpisode(featured)
                            } else {
                                launchExternalPodcast(featured, featured.externalUrl, featured.provider.displayName)
                            }
                        },
                        onToggleBookmark = { onToggleBookmark(featured.id) },
                        onRefreshFeeds = { onRefreshFeeds(true) }
                    )
                }
            }

            // Category Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PodcastCategory.entries.forEach { category ->
                            FilterChip(
                                selected = feedState.selectedCategory == category,
                                onClick = { onSelectCategory(category) },
                                label = { Text(category.label) },
                                shape = RoundedCornerShape(50),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("category_filter_${category.name.lowercase()}")
                            )
                        }
                    }

                    if (feedState.liveFeedEpisodes.isNotEmpty()) {
                        FilterChip(
                            selected = feedState.showStreamableOnly,
                            onClick = onToggleStreamableOnly,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.GraphicEq,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text("In-App Audio Ready (${feedState.liveFeedEpisodes.size})")
                            },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.testTag("filter_streamable_only")
                        )
                    }
                }
            }

            // Episode Directory Section Header
            item {
                val streamCount = filteredEpisodes.count { it.supportsInAppPlayback }
                val subtitle = if (streamCount > 0) {
                    "${filteredEpisodes.size} real podcasts • $streamCount streamable in-app"
                } else {
                    "${filteredEpisodes.size} verified motivational & wellness podcasts"
                }
                SectionHeader(
                    title = "Motivational & Wellness Podcasts",
                    subtitle = subtitle
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
                                text = "No episodes match this filter yet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Try selecting All categories or turning off the In-App Audio filter to browse all verified podcasts.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredEpisodes, key = { it.id }) { episode ->
                    val isThisStreaming = feedState.player.activeStreamEpisodeId == episode.id
                    RealPodcastCard(
                        episode = episode,
                        isSelected = episode.id == feedState.selectedEpisodeId,
                        isPlayingInApp = isThisStreaming && feedState.player.isPlaying,
                        isBookmarked = episode.id in feedState.bookmarkedIds,
                        onOpenDetails = {
                            onSelectEpisode(episode.id)
                            detailSheetEpisode = episode
                        },
                        onQuickAction = {
                            if (episode.supportsInAppPlayback) {
                                if (isThisStreaming) {
                                    onToggleMedia3PlayPause()
                                } else {
                                    onPlayStreamableEpisode(episode)
                                }
                            } else {
                                launchExternalPodcast(episode, episode.externalUrl, episode.provider.displayName)
                            }
                        },
                        onToggleBookmark = { onToggleBookmark(episode.id) }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    detailSheetEpisode?.let { selected ->
        val isPlayingSelected =
            feedState.player.activeStreamEpisodeId == selected.id && feedState.player.isPlaying
        EpisodeDetailSheet(
            episode = selected,
            isPlayingThisInApp = isPlayingSelected,
            isBookmarked = selected.id in feedState.bookmarkedIds,
            onDismiss = { detailSheetEpisode = null },
            onPlayInApp = {
                if (feedState.player.activeStreamEpisodeId == selected.id) {
                    onToggleMedia3PlayPause()
                } else {
                    onPlayStreamableEpisode(selected)
                }
                detailSheetEpisode = null
            },
            onOpenPrimaryExternal = {
                launchExternalPodcast(selected, selected.externalUrl, selected.provider.displayName)
                detailSheetEpisode = null
            },
            onOpenSecondaryExternal = {
                val url = selected.secondaryUrl
                val label = selected.secondaryProviderLabel ?: "Website"
                if (!url.isNullOrBlank()) {
                    launchExternalPodcast(selected, url, label)
                }
                detailSheetEpisode = null
            },
            onToggleBookmark = { onToggleBookmark(selected.id) }
        )
    }
}

@Composable
private fun RealPodcastCard(
    episode: RealPodcastEpisode,
    isSelected: Boolean,
    isPlayingInApp: Boolean,
    isBookmarked: Boolean,
    onOpenDetails: () -> Unit,
    onQuickAction: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onOpenDetails() }
            .testTag("podcast_card_${episode.id}"),
        shape = RoundedCornerShape(22.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PodcastArtworkThumb(
                    artworkUrl = episode.artworkUrl,
                    contentDescription = "${episode.showTitle} artwork",
                    size = 58.dp,
                    cornerRadius = 14.dp
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = episode.category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (episode.supportsInAppPlayback) {
                                "In-App Audio"
                            } else {
                                episode.provider.displayName
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = episode.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${episode.showTitle} • ${episode.creator}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("episode_bookmark_${episode.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = "Save ${episode.title}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = episode.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.86f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = episode.publishedLabel?.let { "$it • ${episode.durationLabel}" } ?: episode.durationLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onQuickAction() }
                        .testTag("episode_listen_${episode.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                episode.supportsInAppPlayback && isPlayingInApp -> Icons.Rounded.Pause
                                episode.supportsInAppPlayback -> Icons.Rounded.PlayArrow
                                else -> Icons.AutoMirrored.Rounded.OpenInNew
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = when {
                                episode.supportsInAppPlayback && isPlayingInApp -> "Pause"
                                episode.supportsInAppPlayback -> "Play In-App"
                                else -> "Listen Now"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun openExternalUrl(context: Context, url: String): Boolean {
    return runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        true
    }.getOrDefault(false)
}
