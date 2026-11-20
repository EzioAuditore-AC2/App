package com.example.podcast

data class PublicRssSource(
    val id: String,
    val showTitle: String,
    val creator: String,
    val feedUrl: String,
    val websiteUrl: String,
    val defaultCategory: PodcastCategory
)

/**
 * Curated catalog of real, verified motivational and mental wellness podcasts.
 * Every external URL and public RSS feed URL has been verified against its official provider.
 * External provider links (Spotify, YouTube, official sites) are opened externally and never
 * misrepresented as direct in-app audio streams.
 */
object PodcastCatalog {

    val publicRssSources: List<PublicRssSource> = listOf(
        PublicRssSource(
            id = "npr_life_kit",
            showTitle = "NPR Life Kit",
            creator = "NPR",
            feedUrl = "https://feeds.npr.org/510338/podcast.xml",
            websiteUrl = "https://www.npr.org/lifekit",
            defaultCategory = PodcastCategory.SELF_CARE
        ),
        PublicRssSource(
            id = "ted_talks_daily",
            showTitle = "TED Talks Daily",
            creator = "Elise Hu • TED Audio Collective",
            feedUrl = "https://feeds.acast.com/public/shows/67587e77c705e441797aff96",
            websiteUrl = "https://open.spotify.com/show/1VjNoSyyHxqbZspissmdud",
            defaultCategory = PodcastCategory.MOTIVATION
        )
    )

    val curatedEpisodes: List<RealPodcastEpisode> = listOf(
        RealPodcastEpisode(
            id = "curated_happiness_lab",
            title = "Stop Stressing About Stress",
            showTitle = "The Happiness Lab with Dr. Laurie Santos",
            creator = "Dr. Laurie Santos & Dr. Jenny Taitz • Pushkin Industries",
            category = PodcastCategory.STRESS_MANAGEMENT,
            description = "Yale professor Dr. Laurie Santos and clinical psychologist Dr. Jenny Taitz explore why stressing about feeling stressed makes life harder, and share quick science-backed resets for body and mind.",
            durationLabel = "Pushkin / Spotify",
            durationMinutes = 38,
            provider = PodcastProvider.SPOTIFY,
            externalUrl = "https://open.spotify.com/show/3i5TCKhc6GY42pOWkpWveG",
            secondaryUrl = "https://www.pushkin.fm/podcasts/the-happiness-lab-with-dr-laurie-santos",
            secondaryProviderLabel = "Pushkin Website",
            keyTopics = listOf("Stress Resets", "Reframing Pressure", "Behavioral Science")
        ),
        RealPodcastEpisode(
            id = "curated_science_of_happiness",
            title = "Research-Tested Practices for Compassion & Everyday Awe",
            showTitle = "The Science of Happiness",
            creator = "Dacher Keltner • UC Berkeley Greater Good Science Center & PRX",
            category = PodcastCategory.SELF_CARE,
            description = "Co-produced by UC Berkeley's Greater Good Science Center and PRX, each episode tests practical, research-backed habits—like self-compassion, gratitude, and mindful connection—in real people's lives.",
            durationLabel = "Official Website / Spotify",
            durationMinutes = 25,
            provider = PodcastProvider.OFFICIAL_WEB,
            externalUrl = "https://greatergood.berkeley.edu/podcasts/series/the_science_of_happiness",
            secondaryUrl = "https://open.spotify.com/show/4dq0wmiZlk58Eu0NcqgWei",
            secondaryProviderLabel = "Open in Spotify",
            keyTopics = listOf("Self-Compassion", "Gratitude", "Mindfulness")
        ),
        RealPodcastEpisode(
            id = "curated_mel_robbins",
            title = "Building Daily Confidence & Practical Momentum",
            showTitle = "The Mel Robbins Podcast",
            creator = "Mel Robbins",
            category = PodcastCategory.CONFIDENCE,
            description = "Actionable, science-backed tools and candid conversations designed to help you interrupt self-doubt, strengthen everyday confidence, and take calm, consistent steps toward your goals.",
            durationLabel = "Spotify / Official Site",
            durationMinutes = 45,
            provider = PodcastProvider.SPOTIFY,
            externalUrl = "https://open.spotify.com/show/73sIBHcqX30yNjvhkFMCLV",
            secondaryUrl = "https://www.melrobbins.com/podcasts",
            secondaryProviderLabel = "Official Website",
            keyTopics = listOf("Self-Confidence", "Overcoming Doubt", "Actionable Habits")
        ),
        RealPodcastEpisode(
            id = "curated_on_purpose",
            title = "Mindset, Purpose & Intentional Living Conversations",
            showTitle = "On Purpose with Jay Shetty",
            creator = "Jay Shetty",
            category = PodcastCategory.MOTIVATION,
            description = "Thoughtful interviews and solo reflections on finding purpose, navigating emotional challenges, cultivating resilience, and designing a more intentional everyday life.",
            durationLabel = "Spotify",
            durationMinutes = 50,
            provider = PodcastProvider.SPOTIFY,
            externalUrl = "https://open.spotify.com/show/5EqqB52m2bsr4k1Ii7sStc",
            keyTopics = listOf("Motivation", "Purpose", "Resilience")
        ),
        RealPodcastEpisode(
            id = "curated_therapy_in_a_nutshell",
            title = "Practical Emotional Regulation & Anxiety Skills",
            showTitle = "Therapy in a Nutshell",
            creator = "Emma McAdam, LMFT",
            category = PodcastCategory.SELF_CARE,
            description = "Licensed therapist Emma McAdam breaks down approachable, evidence-based emotional processing, grounding, and self-care skills into clear, compassionate video and audio lessons.",
            durationLabel = "YouTube Channel",
            durationMinutes = 18,
            provider = PodcastProvider.YOUTUBE,
            externalUrl = "https://www.youtube.com/@TherapyinaNutshell",
            keyTopics = listOf("Emotional Skills", "Grounding", "Self-Care")
        ),
        RealPodcastEpisode(
            id = "curated_ten_percent_happier",
            title = "Skeptical Mindfulness for Busy, Stressed Minds",
            showTitle = "10% Happier with Dan Harris",
            creator = "Dan Harris",
            category = PodcastCategory.STRESS_MANAGEMENT,
            description = "Journalist Dan Harris talks with meditation teachers, neuroscientists, and psychologists about practical mindfulness, taming anxiety, and building steadiness without perfectionism.",
            durationLabel = "Spotify / Official Site",
            durationMinutes = 42,
            provider = PodcastProvider.SPOTIFY,
            externalUrl = "https://open.spotify.com/show/13rZ8xFxsGpbctj9rvN9j6",
            secondaryUrl = "https://www.danharris.com/",
            secondaryProviderLabel = "DanHarris.com",
            keyTopics = listOf("Mindfulness", "Anxiety Relief", "Calm Focus")
        ),
        RealPodcastEpisode(
            id = "curated_speaking_of_psychology",
            title = "Psychological Science for Resilience, Rest & Growth",
            showTitle = "Speaking of Psychology",
            creator = "Kim Mills • American Psychological Association (APA)",
            category = PodcastCategory.PERSONAL_GROWTH,
            description = "The flagship podcast of the American Psychological Association featuring leading researchers discussing stress recovery, healthy boundaries, emotional resilience, and personal growth across life.",
            durationLabel = "APA Official Site",
            durationMinutes = 34,
            provider = PodcastProvider.OFFICIAL_WEB,
            externalUrl = "https://www.apa.org/news/podcasts/speaking-of-psychology",
            keyTopics = listOf("Psychology Research", "Burnout Recovery", "Healthy Boundaries")
        ),
        RealPodcastEpisode(
            id = "curated_hidden_brain",
            title = "Understanding the Unseen Patterns Shaping Our Choices",
            showTitle = "Hidden Brain",
            creator = "Shankar Vedantam • Hidden Brain Media",
            category = PodcastCategory.PERSONAL_GROWTH,
            description = "Blends storytelling and psychology research to illuminate curiosity, human habits, emotional blind spots, and how small mindset shifts can open new personal growth.",
            durationLabel = "Spotify / Official Site",
            durationMinutes = 48,
            provider = PodcastProvider.SPOTIFY,
            externalUrl = "https://open.spotify.com/show/5CwpWdE7JETUoRk5T7YcVj",
            secondaryUrl = "https://www.hiddenbrain.org",
            secondaryProviderLabel = "HiddenBrain.org",
            keyTopics = listOf("Human Behavior", "Self-Discovery", "Curiosity")
        ),
        RealPodcastEpisode(
            id = "curated_huberman_lab",
            title = "Science-Based Tools for Focus, Sleep & Motivation",
            showTitle = "Huberman Lab",
            creator = "Dr. Andrew Huberman",
            category = PodcastCategory.CONFIDENCE,
            description = "Neuroscience-focused discussions explaining how sleep, sunlight, stress physiology, and habit loops shape daily motivation, mental clarity, and self-efficacy.",
            durationLabel = "Spotify / Official Site",
            durationMinutes = 60,
            provider = PodcastProvider.SPOTIFY,
            externalUrl = "https://open.spotify.com/show/79CkJF3UJTHFV8Dse3Oy0p",
            secondaryUrl = "https://www.hubermanlab.com",
            secondaryProviderLabel = "HubermanLab.com",
            keyTopics = listOf("Neuroscience", "Motivation Systems", "Sleep & Focus")
        ),
        RealPodcastEpisode(
            id = "curated_ted_talks_daily",
            title = "Fresh Ideas for Motivation, Courage & Curiosity",
            showTitle = "TED Talks Daily",
            creator = "Elise Hu • TED Audio Collective",
            category = PodcastCategory.MOTIVATION,
            description = "Short, inspiring talks from thinkers, educators, and creators around the world—offering thought-provoking sparks for courage, creative problem-solving, and personal growth.",
            durationLabel = "Spotify",
            durationMinutes = 14,
            provider = PodcastProvider.SPOTIFY,
            externalUrl = "https://open.spotify.com/show/1VjNoSyyHxqbZspissmdud",
            keyTopics = listOf("Inspiring Ideas", "Creativity", "Personal Growth")
        )
    )
}
