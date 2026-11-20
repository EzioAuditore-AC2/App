package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.podcast.PodcastPlayerState
import com.example.podcast.PodcastProvider
import com.example.podcast.RealPodcastEpisode
import com.example.ui.components.PrototypeBadge
import com.example.ui.components.formatDurationSeconds
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.SageSoft

@Composable
internal fun PodcastArtworkThumb(
    artworkUrl: String?,
    contentDescription: String,
    size: Dp,
    cornerRadius: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    val fallback = painterResource(id = R.drawable.img_podcast_cover_1795175965182)
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(ForestDeep)
    ) {
        if (!artworkUrl.isNullOrBlank()) {
            AsyncImage(
                model = artworkUrl,
                contentDescription = contentDescription,
                placeholder = fallback,
                error = fallback,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = fallback,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

internal fun providerIcon(provider: PodcastProvider): ImageVector = when (provider) {
    PodcastProvider.SPOTIFY -> Icons.Rounded.Headphones
    PodcastProvider.YOUTUBE -> Icons.Rounded.SmartDisplay
    PodcastProvider.OFFICIAL_WEB -> Icons.Rounded.Public
    PodcastProvider.PUBLIC_RSS -> Icons.Rounded.GraphicEq
}

@Composable
internal fun Media3ActivePlayerCard(
    player: PodcastPlayerState,
    activeEpisode: RealPodcastEpisode?,
    isBookmarked: Boolean,
    onTogglePlayPause: () -> Unit,
    onSeekMs: (Long) -> Unit,
    onSkipMs: (Long) -> Unit,
    onCycleSpeed: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenExternal: (RealPodcastEpisode) -> Unit
) {
    val safeDurationMs = player.durationMs.coerceAtLeast(1_000L)
    val safePosMs = player.positionMs.coerceIn(0L, safeDurationMs)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("media3_player_card"),
        shape = RoundedCornerShape(28.dp),
        color = ForestDeep,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PrototypeBadge(text = "MEDIA3 IN-APP AUDIO", inverted = true)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (activeEpisode != null) {
                        IconButton(
                            onClick = { onOpenExternal(activeEpisode) },
                            modifier = Modifier.testTag("media3_open_external_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                contentDescription = "Open episode on provider website",
                                tint = Color(0xFFFAF6EE)
                            )
                        }
                    }
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.testTag("podcast_player_bookmark")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = "Bookmark playing episode",
                            tint = Color(0xFFFAF6EE)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PodcastArtworkThumb(
                    artworkUrl = player.activeStreamArtworkUrl,
                    contentDescription = player.activeStreamTitle ?: "Playing podcast artwork",
                    size = 64.dp,
                    cornerRadius = 16.dp
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = player.activeStreamTitle ?: "Direct Podcast Stream",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFAF6EE),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = player.activeStreamCreator ?: "Public Podcast RSS Feed",
                        style = MaterialTheme.typography.bodySmall,
                        color = SageSoft,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (!player.errorMessage.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF4A2520)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFFFD6CE),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = player.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFE9E5)
                        )
                    }
                }
            }

            Slider(
                value = safePosMs.toFloat(),
                onValueChange = { onSeekMs(it.toLong()) },
                valueRange = 0f..safeDurationMs.toFloat(),
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
                    text = formatDurationSeconds((safePosMs / 1000L).toInt()),
                    style = MaterialTheme.typography.labelSmall,
                    color = SageSoft
                )
                Text(
                    text = when {
                        player.isBuffering -> "Buffering stream…"
                        player.isPlaying -> "Playing via ExoPlayer"
                        else -> "Paused"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = SageSoft.copy(alpha = 0.9f)
                )
                Text(
                    text = formatDurationSeconds((safeDurationMs / 1000L).toInt()),
                    style = MaterialTheme.typography.labelSmall,
                    color = SageSoft
                )
            }

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
                        text = "${player.playbackSpeed}x",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFAF6EE),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }

                IconButton(
                    onClick = { onSkipMs(-15_000L) },
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
                        if (player.isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = ForestDeep,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (player.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (player.isPlaying) "Pause stream" else "Play stream",
                                tint = ForestDeep,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { onSkipMs(15_000L) },
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
                    tint = if (player.isPlaying) SageSoft else Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
internal fun FeaturedPodcastHeroCard(
    featured: RealPodcastEpisode,
    isBookmarked: Boolean,
    isLoadingFeeds: Boolean,
    feedStatusNote: String?,
    onListenNow: () -> Unit,
    onToggleBookmark: () -> Unit,
    onRefreshFeeds: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("podcast_featured_hero"),
        shape = RoundedCornerShape(28.dp),
        color = ForestDeep,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
            ) {
                val fallback = painterResource(id = R.drawable.img_podcast_cover_1795175965182)
                if (!featured.artworkUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = featured.artworkUrl,
                        contentDescription = "${featured.showTitle} cover artwork",
                        placeholder = fallback,
                        error = fallback,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = fallback,
                        contentDescription = "NeuroTalks botanical podcast illustration",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    ForestDeep.copy(alpha = 0.28f),
                                    ForestDeep.copy(alpha = 0.84f),
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
                    PrototypeBadge(
                        text = if (featured.supportsInAppPlayback) {
                            "DIRECT AUDIO READY"
                        } else {
                            "VERIFIED • ${featured.provider.displayName.uppercase()}"
                        },
                        inverted = true
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onRefreshFeeds,
                            modifier = Modifier.testTag("podcast_refresh_feeds_button")
                        ) {
                            if (isLoadingFeeds) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFFFAF6EE),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Refresh public podcast streams",
                                    tint = Color(0xFFFAF6EE)
                                )
                            }
                        }
                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier.testTag("podcast_hero_bookmark")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                contentDescription = "Save featured podcast",
                                tint = Color(0xFFFAF6EE)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "${featured.category.label} • ${featured.showTitle}",
                        style = MaterialTheme.typography.labelMedium,
                        color = SageSoft,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = featured.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFAF6EE),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = featured.creator,
                        style = MaterialTheme.typography.bodySmall,
                        color = SageSoft.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onListenNow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("podcast_hero_listen_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFAF6EE),
                        contentColor = ForestDeep
                    )
                ) {
                    Icon(
                        imageVector = if (featured.supportsInAppPlayback) {
                            Icons.Rounded.PlayArrow
                        } else {
                            Icons.AutoMirrored.Rounded.OpenInNew
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (featured.supportsInAppPlayback) {
                            "Listen Now In-App"
                        } else {
                            "Listen Now • ${featured.provider.displayName}"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!feedStatusNote.isNullOrBlank()) {
                    Text(
                        text = feedStatusNote,
                        style = MaterialTheme.typography.labelSmall,
                        color = SageSoft.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun EpisodeDetailSheet(
    episode: RealPodcastEpisode,
    isPlayingThisInApp: Boolean,
    isBookmarked: Boolean,
    onDismiss: () -> Unit,
    onPlayInApp: () -> Unit,
    onOpenPrimaryExternal: () -> Unit,
    onOpenSecondaryExternal: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PodcastArtworkThumb(
                    artworkUrl = episode.artworkUrl,
                    contentDescription = "${episode.showTitle} artwork",
                    size = 76.dp,
                    cornerRadius = 18.dp
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "${episode.category.label} • ${episode.provider.displayName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = episode.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${episode.showTitle} — ${episode.creator}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("sheet_bookmark_button")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = "Save episode",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = episode.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            if (episode.keyTopics.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    episode.keyTopics.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            if (episode.supportsInAppPlayback) {
                Button(
                    onClick = onPlayInApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("sheet_play_in_app_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestPrimary,
                        contentColor = Color(0xFFFAF6EE)
                    )
                ) {
                    Icon(
                        imageVector = if (isPlayingThisInApp) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPlayingThisInApp) {
                            "Pause In-App Playback"
                        } else {
                            "Listen Now In-App (Media3)"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onOpenPrimaryExternal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sheet_listen_external_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = episode.provider.ctaLabel)
                }
            } else {
                Button(
                    onClick = onOpenPrimaryExternal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("sheet_listen_now_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestPrimary,
                        contentColor = Color(0xFFFAF6EE)
                    )
                ) {
                    Icon(
                        imageVector = providerIcon(episode.provider),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Listen Now — ${episode.provider.displayName}",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (!episode.secondaryUrl.isNullOrBlank() && !episode.secondaryProviderLabel.isNullOrBlank()) {
                    OutlinedButton(
                        onClick = onOpenSecondaryExternal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("sheet_secondary_link_button"),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = episode.secondaryProviderLabel)
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ) {
                Text(
                    text = if (episode.supportsInAppPlayback) {
                        "Direct audio provided via official public podcast RSS feed and streamed with Android Media3 ExoPlayer."
                    } else {
                        "Verified external link: opens directly in ${episode.provider.displayName} or your browser so creators receive full attribution."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
