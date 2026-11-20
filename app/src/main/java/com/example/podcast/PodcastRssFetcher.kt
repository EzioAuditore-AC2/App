package com.example.podcast

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Fetches and parses legitimately published public podcast RSS feeds (e.g., NPR Life Kit,
 * TED Talks Daily) to surface real episodes with official publisher audio stream enclosures
 * playable via Android Media3 ExoPlayer.
 */
object PodcastRssFetcher {

    suspend fun fetchPublicStreamableEpisodes(maxPerFeed: Int = 4): List<RealPodcastEpisode> =
        withContext(Dispatchers.IO) {
            val results = mutableListOf<RealPodcastEpisode>()
            for (source in PodcastCatalog.publicRssSources) {
                val xml = runCatching { downloadXml(source.feedUrl) }.getOrNull() ?: continue
                val parsed = runCatching { parseRssXml(xml, source, maxPerFeed) }.getOrDefault(emptyList())
                results.addAll(parsed)
            }
            results
        }

    private fun downloadXml(feedUrl: String): String? {
        val connection = (URL(feedUrl).openConnection() as? HttpURLConnection) ?: return null
        return try {
            connection.connectTimeout = 6000
            connection.readTimeout = 7000
            connection.instanceFollowRedirects = true
            connection.setRequestProperty("User-Agent", "NeuroTalks-Android/1.0")
            if (connection.responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }
        } finally {
            connection.disconnect()
        }
    }

    internal fun parseRssXml(
        xml: String,
        source: PublicRssSource,
        maxItems: Int = 4
    ): List<RealPodcastEpisode> {
        val episodes = mutableListOf<RealPodcastEpisode>()
        val factory = XmlPullParserFactory.newInstance().apply { isNamespaceAware = false }
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))

        var channelImageUrl: String? = null
        var inItem = false
        var title = ""
        var description = ""
        var link = ""
        var author = ""
        var durationRaw = ""
        var enclosureUrl: String? = null
        var itemImageUrl: String? = null
        var pubDate = ""

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT && episodes.size < maxItems) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    val tag = parser.name.lowercase()
                    when {
                        tag == "item" -> {
                            inItem = true
                            title = ""
                            description = ""
                            link = ""
                            author = ""
                            durationRaw = ""
                            enclosureUrl = null
                            itemImageUrl = null
                            pubDate = ""
                        }
                        !inItem && (tag == "itunes:image") -> {
                            val href = parser.getAttributeValue(null, "href")
                            if (!href.isNullOrBlank()) channelImageUrl = href
                        }
                        inItem && tag == "title" -> title = readTextSafe(parser)
                        inItem && (tag == "description" || tag == "itunes:summary") -> {
                            val text = readTextSafe(parser)
                            if (description.isBlank() || tag == "itunes:summary") {
                                description = stripHtml(text)
                            }
                        }
                        inItem && tag == "link" -> link = readTextSafe(parser)
                        inItem && (tag == "itunes:author" || tag == "author" || tag == "dc:creator") -> {
                            val text = readTextSafe(parser)
                            if (text.isNotBlank()) author = text
                        }
                        inItem && tag == "itunes:duration" -> durationRaw = readTextSafe(parser)
                        inItem && tag == "pubdate" -> pubDate = readTextSafe(parser)
                        inItem && tag == "itunes:image" -> {
                            val href = parser.getAttributeValue(null, "href")
                            if (!href.isNullOrBlank()) itemImageUrl = href
                        }
                        inItem && tag == "enclosure" -> {
                            val url = parser.getAttributeValue(null, "url")
                            val type = parser.getAttributeValue(null, "type").orEmpty().lowercase()
                            if (!url.isNullOrBlank() && (type.isBlank() || type.contains("audio"))) {
                                enclosureUrl = url
                            }
                        }
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name.equals("item", ignoreCase = true) && inItem) {
                        inItem = false
                        val cleanTitle = stripHtml(title).trim()
                        val cleanStream = enclosureUrl?.trim()
                        if (cleanTitle.isNotBlank() && !cleanStream.isNullOrBlank() && cleanStream.startsWith("http")) {
                            val mins = parseDurationMinutes(durationRaw)
                            val category = classifyCategory("$cleanTitle $description", source.defaultCategory)
                            val safeDesc = description.ifBlank {
                                "Official episode from ${source.showTitle}. Stream directly in-app or open on the publisher's website."
                            }.take(240)
                            episodes.add(
                                RealPodcastEpisode(
                                    id = "rss_${source.id}_${episodes.size}_${cleanTitle.hashCode()}",
                                    title = cleanTitle,
                                    showTitle = source.showTitle,
                                    creator = author.ifBlank { source.creator },
                                    category = category,
                                    description = safeDesc,
                                    durationLabel = if (mins > 0) "$mins min • Direct Audio" else "Direct Audio",
                                    durationMinutes = mins.coerceAtLeast(1),
                                    provider = PodcastProvider.PUBLIC_RSS,
                                    externalUrl = link.ifBlank { source.websiteUrl },
                                    directAudioStreamUrl = cleanStream,
                                    artworkUrl = itemImageUrl ?: channelImageUrl,
                                    keyTopics = listOf(source.showTitle, category.label, "In-App Audio"),
                                    publishedLabel = formatPubDate(pubDate)
                                )
                            )
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return episodes
    }

    private fun readTextSafe(parser: XmlPullParser): String {
        return runCatching {
            parser.nextText().orEmpty()
        }.getOrDefault("")
    }

    internal fun stripHtml(raw: String): String {
        return raw
            .replace(Regex("<[^>]*>"), " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&nbsp;", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    internal fun parseDurationMinutes(raw: String): Int {
        val clean = raw.trim()
        if (clean.isEmpty()) return 15
        clean.toIntOrNull()?.let { seconds ->
            return (seconds / 60).coerceAtLeast(1)
        }
        val parts = clean.split(":").mapNotNull { it.toIntOrNull() }
        return when (parts.size) {
            3 -> (parts[0] * 60 + parts[1]).coerceAtLeast(1)
            2 -> parts[0].coerceAtLeast(1)
            else -> 15
        }
    }

    internal fun classifyCategory(text: String, fallback: PodcastCategory): PodcastCategory {
        val lower = text.lowercase()
        return when {
            listOf("stress", "anxiet", "burnout", "overwhelm", "calm", "breathe", "nervous").any { it in lower } ->
                PodcastCategory.STRESS_MANAGEMENT
            listOf("confiden", "self-esteem", "courage", "brave", "impostor", "speak up").any { it in lower } ->
                PodcastCategory.CONFIDENCE
            listOf("self-care", "sleep", "rest", "boundary", "boundaries", "compassion", "health", "well-being").any { it in lower } ->
                PodcastCategory.SELF_CARE
            listOf("motivat", "inspir", "purpose", "dream", "goal", "achieve", "momentum").any { it in lower } ->
                PodcastCategory.MOTIVATION
            listOf("grow", "habit", "learn", "mindset", "career", "creative", "curios").any { it in lower } ->
                PodcastCategory.PERSONAL_GROWTH
            else -> fallback
        }
    }

    private fun formatPubDate(raw: String): String? {
        val clean = raw.trim()
        if (clean.isEmpty()) return null
        // Typical RFC-2822: "Tue, 18 Nov 2026 08:00:00 +0000" -> "18 Nov 2026"
        val tokens = clean.split(" ").filter { it.isNotBlank() }
        return if (tokens.size >= 4) "${tokens[1]} ${tokens[2]} ${tokens[3]}" else clean.take(16)
    }
}
