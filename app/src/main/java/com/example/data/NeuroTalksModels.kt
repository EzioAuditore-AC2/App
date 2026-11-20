package com.example.data

enum class MoodOption(val label: String, val tagline: String) {
    CALM("Calm", "Steady breath, grounded moment"),
    REFLECTIVE("Reflective", "Noticing thoughts gently"),
    HOPEFUL("Hopeful", "Open to small bright steps"),
    TIRED("Low Energy", "Permission to move slowly today"),
    OVERWHELMED("Overwhelmed", "One gentle breath at a time")
}

data class GuidelineItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val iconKey: String
)

enum class PodcastStyle(val label: String) {
    ALL("All Styles"),
    SOLO_REFLECTION("Solo Reflection"),
    GENTLE_INTERVIEW("Gentle Interview"),
    GUIDED_AUDIO("Guided Audio"),
    FIRESIDE_CHAT("Fireside Chat")
}

data class PodcastEpisode(
    val id: String,
    val title: String,
    val host: String,
    val topic: String,
    val style: PodcastStyle,
    val durationMinutes: Int,
    val durationSeconds: Int,
    val summary: String,
    val takeaways: List<String>,
    val reflectionPrompt: String
)

data class SimulationCharacter(
    val id: String,
    val name: String,
    val role: String,
    val vibe: String,
    val bio: String,
    val initials: String
)

data class SimulationGoal(
    val id: String,
    val title: String,
    val subtitle: String,
    val focusTag: String
)

data class SimulationChoice(
    val id: String,
    val label: String,
    val outcomeText: String,
    val calmDelta: Int,
    val clarityDelta: Int,
    val connectionDelta: Int,
    val energyDelta: Int
)

data class SimulationStage(
    val stageNumber: Int,
    val timeOfDay: String,
    val title: String,
    val narrative: String,
    val choices: List<SimulationChoice>
)

data class SimulationMetrics(
    val calm: Int = 55,
    val clarity: Int = 50,
    val connection: Int = 50,
    val energy: Int = 55
) {
    fun apply(choice: SimulationChoice): SimulationMetrics = SimulationMetrics(
        calm = (calm + choice.calmDelta).coerceIn(10, 100),
        clarity = (clarity + choice.clarityDelta).coerceIn(10, 100),
        connection = (connection + choice.connectionDelta).coerceIn(10, 100),
        energy = (energy + choice.energyDelta).coerceIn(10, 100)
    )
}

enum class ForumCategory(val label: String) {
    ALL("All Notes"),
    SMALL_WINS("Small Wins"),
    VENTING("Venting Gently"),
    ADVICE("Peer Ideas"),
    COPING("Coping Tools")
}

data class ForumReply(
    val id: String,
    val authorAlias: String,
    val timestamp: String,
    val content: String
)

data class ForumPost(
    val id: String,
    val authorAlias: String,
    val timestamp: String,
    val category: ForumCategory,
    val moodTag: String,
    val title: String,
    val body: String,
    val warmthCount: Int,
    val relateCount: Int,
    val hasSentWarmth: Boolean = false,
    val hasRelated: Boolean = false,
    val isReported: Boolean = false,
    val replies: List<ForumReply>
)

data class HotlineResource(
    val id: String,
    val name: String,
    val region: String,
    val numberDisplay: String,
    val dialNumber: String,
    val availability: String,
    val description: String,
    val isPlaceholder: Boolean = false,
    val actionLabel: String = "Open Dialer"
)

data class ActivityEntry(
    val id: String,
    val title: String,
    val detail: String,
    val timestamp: String,
    val category: String
)

data class DemoSettings(
    val darkCanopyTheme: Boolean = false,
    val showDailyCheckIn: Boolean = true,
    val calmAudioCues: Boolean = true,
    val gentleMotionMode: Boolean = false
)
