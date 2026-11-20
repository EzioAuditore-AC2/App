package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.podcast.PodcastCatalog
import com.example.podcast.PodcastCategory
import com.example.podcast.PodcastProvider
import com.example.podcast.PodcastRssFetcher
import com.example.podcast.PublicRssSource
import com.example.viewmodel.NeuroTalksViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PodcastVerificationTest {

    @Test
    fun curatedCatalogCoversAllRequiredCategoriesAndVerifiedExternalLinks() {
        val episodes = PodcastCatalog.curatedEpisodes
        assertTrue("Expected at least 8 curated podcasts", episodes.size >= 8)

        val requiredCategories = setOf(
            PodcastCategory.MOTIVATION,
            PodcastCategory.SELF_CARE,
            PodcastCategory.CONFIDENCE,
            PodcastCategory.STRESS_MANAGEMENT,
            PodcastCategory.PERSONAL_GROWTH
        )
        val presentCategories = episodes.map { it.category }.toSet()
        assertTrue("All 5 wellness categories must be covered", presentCategories.containsAll(requiredCategories))

        episodes.forEach { ep ->
            assertTrue("Title must not be blank for ${ep.id}", ep.title.isNotBlank())
            assertTrue("Creator must not be blank for ${ep.id}", ep.creator.isNotBlank())
            assertTrue("Description must not be blank for ${ep.id}", ep.description.isNotBlank())
            assertTrue("External URL must be HTTPS for ${ep.id}", ep.externalUrl.startsWith("https://"))
            // External providers (Spotify, YouTube, official web) must never be misrepresented as direct audio streams
            assertNull("Curated external link must not claim direct stream: ${ep.id}", ep.directAudioStreamUrl)
            assertFalse("External link must not claim in-app playback: ${ep.id}", ep.supportsInAppPlayback)
            if (ep.provider == PodcastProvider.SPOTIFY) {
                assertTrue(ep.externalUrl.contains("open.spotify.com"))
            }
            if (ep.provider == PodcastProvider.YOUTUBE) {
                assertTrue(ep.externalUrl.contains("youtube.com"))
            }
        }
    }

    @Test
    fun rssParserExtractsDirectAudioArtworkAndCdataDescription() {
        val sampleRss = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0" xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">
              <channel>
                <title>NPR Life Kit</title>
                <itunes:image href="https://media.npr.org/assets/img/lifekit-cover.jpg" />
                <item>
                  <title>How to manage daily stress and protect your rest</title>
                  <itunes:author>NPR Life Kit</itunes:author>
                  <description><![CDATA[<p>Practical, research-backed tips for managing overwhelm and calming your nervous system.</p>]]></description>
                  <link>https://www.npr.org/lifekit</link>
                  <itunes:duration>18:30</itunes:duration>
                  <pubDate>Tue, 18 Nov 2026 08:00:00 +0000</pubDate>
                  <enclosure url="https://play.podtrac.com/npr-510338/example-episode.mp3" type="audio/mpeg" length="123456" />
                </item>
              </channel>
            </rss>
        """.trimIndent()

        val source = PublicRssSource(
            id = "npr_life_kit",
            showTitle = "NPR Life Kit",
            creator = "NPR",
            feedUrl = "https://feeds.npr.org/510338/podcast.xml",
            websiteUrl = "https://www.npr.org/lifekit",
            defaultCategory = PodcastCategory.SELF_CARE
        )

        val parsed = PodcastRssFetcher.parseRssXml(sampleRss, source, maxItems = 4)
        assertEquals(1, parsed.size)
        val ep = parsed.first()
        assertEquals("How to manage daily stress and protect your rest", ep.title)
        assertEquals("NPR Life Kit", ep.creator)
        assertTrue(ep.description.contains("Practical, research-backed tips"))
        assertFalse(ep.description.contains("<p>"))
        assertEquals("https://media.npr.org/assets/img/lifekit-cover.jpg", ep.artworkUrl)
        assertEquals("https://play.podtrac.com/npr-510338/example-episode.mp3", ep.directAudioStreamUrl)
        assertTrue(ep.supportsInAppPlayback)
        assertEquals(PodcastCategory.STRESS_MANAGEMENT, ep.category)
        assertEquals(18, ep.durationMinutes)
        assertNotNull(ep.publishedLabel)
    }

    @Test
    fun viewModelFiltersCategoriesLogsExternalOpensAndGuardsExternalLinksFromMedia3() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = NeuroTalksViewModel(app)

        vm.selectPodcastCategory(PodcastCategory.CONFIDENCE)
        val confidenceList = vm.uiState.value.podcastFeed.filteredEpisodes
        assertTrue(confidenceList.isNotEmpty())
        assertTrue(confidenceList.all { it.category == PodcastCategory.CONFIDENCE })

        // Calling playStreamableEpisode on an external Spotify link must safely no-op
        val spotifyShow = PodcastCatalog.curatedEpisodes.first { it.provider == PodcastProvider.SPOTIFY }
        vm.playStreamableEpisode(spotifyShow)
        assertNull(vm.uiState.value.podcastFeed.player.activeStreamEpisodeId)

        // Recording an external Listen Now open updates selection & activity log
        vm.recordExternalPodcastOpen(spotifyShow, "Spotify")
        assertEquals(spotifyShow.id, vm.uiState.value.podcastFeed.selectedEpisodeId)
        assertTrue(vm.uiState.value.activities.first().title.contains("Spotify"))

        // Bookmarking updates both podcastFeed.bookmarkedIds and Profile's bookmarkedEpisodeIds
        val targetId = PodcastCatalog.curatedEpisodes[2].id
        vm.toggleRealPodcastBookmark(targetId)
        assertTrue(targetId in vm.uiState.value.podcastFeed.bookmarkedIds)
        assertTrue(targetId in vm.uiState.value.bookmarkedEpisodeIds)

        // Playback speed cycles cleanly
        assertEquals(1.0f, vm.uiState.value.podcastFeed.player.playbackSpeed)
        vm.cycleMedia3Speed()
        assertEquals(1.25f, vm.uiState.value.podcastFeed.player.playbackSpeed)
    }
}
