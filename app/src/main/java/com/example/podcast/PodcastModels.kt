package com.example.podcast

enum class PodcastCategory(val label: String) {
    ALL("All"),
    MOTIVATION("Motivation"),
    SELF_CARE("Self-Care"),
    CONFIDENCE("Confidence"),
    STRESS_MANAGEMENT("Stress Management"),
    PERSONAL_GROWTH("Personal Growth")
}

enum class PodcastProvider(val displayName: String, val ctaLabel: String) {
    SPOTIFY("Spotify", "Listen Now on Spotify"),
    YOUTUBE("YouTube", "Watch / Listen on YouTube"),
    OFFICIAL_WEB("Official Website", "Listen on Provider Website"),
    PUBLIC_RSS("Public Podcast Feed", "Open Episode Webpage")
}

data class RealPodcastEpisode(
    val id: String,
    val title: String,
    val showTitle: String,
    val creator: String,
    val category: PodcastCategory,
    val description: String,
    val durationLabel: String,
    val durationMinutes: Int,
    val provider: PodcastProvider,
    val externalUrl: String,
    val secondaryUrl: String? = null,
    val secondaryProviderLabel: String? = null,
    val directAudioStreamUrl: String? = null, // Only populated from legitimate public RSS <enclosure> audio URLs
    val artworkUrl: String? = null,
    val keyTopics: List<String> = emptyList(),
    val publishedLabel: String? = null
) {
    val supportsInAppPlayback: Boolean
        get() = !directAudioStreamUrl.isNullOrBlank()
}

data class PodcastPlayerState(
    val activeStreamEpisodeId: String? = null,
    val activeStreamTitle: String? = null,
    val activeStreamCreator: String? = null,
    val activeStreamArtworkUrl: String? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val errorMessage: String? = null
)

data class PodcastFeedState(
    val selectedCategory: PodcastCategory = PodcastCategory.ALL,
    val selectedEpisodeId: String = PodcastCatalog.curatedEpisodes.first().id,
    val showStreamableOnly: Boolean = false,
    val curatedEpisodes: List<RealPodcastEpisode> = PodcastCatalog.curatedEpisodes,
    val liveFeedEpisodes: List<RealPodcastEpisode> = emptyList(),
    val isLoadingFeeds: Boolean = false,
    val feedStatusNote: String? = null,
    val bookmarkedIds: Set<String> = setOf(PodcastCatalog.curatedEpisodes.first().id),
    val player: PodcastPlayerState = PodcastPlayerState()
) {
    val allEpisodes: List<RealPodcastEpisode>
        get() = liveFeedEpisodes + curatedEpisodes

    val filteredEpisodes: List<RealPodcastEpisode>
        get() = allEpisodes.filter { ep ->
            val categoryMatch = selectedCategory == PodcastCategory.ALL || ep.category == selectedCategory
            val streamMatch = !showStreamableOnly || ep.supportsInAppPlayback
            categoryMatch && streamMatch
        }

    val selectedEpisode: RealPodcastEpisode
        get() = allEpisodes.firstOrNull { it.id == selectedEpisodeId }
            ?: allEpisodes.first()
}
