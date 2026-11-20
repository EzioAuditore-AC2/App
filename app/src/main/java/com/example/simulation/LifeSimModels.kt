package com.example.simulation

import androidx.compose.ui.graphics.Color

enum class SimPhase {
    INTRO,
    CHOOSE_LIFESTYLE,
    CUSTOMIZE_CHARACTER,
    PLAYING_WORLD
}

enum class HairStyle(val label: String) {
    SOFT_BOB("Soft Bob"),
    CURLY_PUFF("Curly Puff"),
    COZY_BUN("Cozy Bun"),
    WAVY_CROP("Wavy Crop")
}

enum class ClothingStyle(val label: String) {
    COZY_SWEATER("Cozy Sweater"),
    CRISP_BLAZER("Smart Blazer"),
    LINEN_TUNIC("Linen Tunic"),
    CREATIVE_APRON("Studio Apron")
}

enum class AccessoryStyle(val label: String) {
    HEADPHONES("Headphones"),
    ROUND_GLASSES("Glasses"),
    LEAF_SPROUT("Leaf Sprout"),
    SUN_HAT("Sun Hat")
}

enum class DreamLifestyle(
    val title: String,
    val subtitle: String,
    val focusTag: String,
    val mantra: String,
    val realLifeBridge: String,
    val featuredActivities: List<String>
) {
    PROFESSIONAL(
        title = "Becoming a Professional",
        subtitle = "Build purposeful work habits, calm leadership, and healthy work-life boundaries",
        focusTag = "Career & Balance",
        mantra = "Steady focus and honest rest help me grow into the professional I admire.",
        realLifeBridge = "Just like in Willowbend, real career growth grows from small repeatable habits: protecting deep work, asking thoughtful questions, and recharging before burnout.",
        featuredActivities = listOf("Deep Focus Project", "Mentor Coffee Chat", "Boundary Reset")
    ),
    PEACEFUL_LIVING(
        title = "Living Peacefully",
        subtitle = "Slow mornings, tending a botanical garden, reflection by the water, and mindful rest",
        focusTag = "Calm & Presence",
        mantra = "Peace is not something I wait for—it is something I practice one gentle breath at a time.",
        realLifeBridge = "Every quiet pause you chose in this scenario reflects a real skill: noticing your energy, slowing down without guilt, and savoring small everyday moments.",
        featuredActivities = listOf("Botanical Gardening", "Stillwater Breathwork", "Slow Herbal Tea")
    ),
    ACADEMIC_SUCCESS(
        title = "Pursuing Academic Success",
        subtitle = "Cultivate curiosity, steady study sessions, peer discussions, and exam confidence",
        focusTag = "Study & Mastery",
        mantra = "Every question I explore strengthens my curiosity and confidence.",
        realLifeBridge = "Real academic confidence comes from steady curiosity rather than perfection. Showing up for focused study blocks and taking real breaks builds lasting mastery.",
        featuredActivities = listOf("Focused Study Block", "Library Research", "Study Group Exchange")
    ),
    ENTREPRENEUR(
        title = "Building a Business",
        subtitle = "Shape a meaningful venture, brainstorm with mentors, test ideas, and lead with care",
        focusTag = "Vision & Resilience",
        mantra = "Big ideas begin as small, brave experiments refined with patience.",
        realLifeBridge = "Building something of your own takes curiosity, community feedback, and resilience after setbacks. Your steady steps here mirror how real founders turn ideas into reality.",
        featuredActivities = listOf("Sketch Venture Plan", "Community Feedback", "Pop-Up Showcase")
    ),
    CREATIVE_CAREER(
        title = "Following a Creative Career",
        subtitle = "Make art and music freely, gather nature inspiration, and share your authentic voice",
        focusTag = "Creativity & Expression",
        mantra = "My creative voice grows stronger every time I make something with curiosity over fear.",
        realLifeBridge = "Creative careers flourish through regular, playful making. Giving yourself room to experiment, walk outside for inspiration, and share work warmly builds real momentum.",
        featuredActivities = listOf("Paint & Compose", "Nature Color Walk", "Open Studio Showcase")
    )
}

enum class TimeOfDay(
    val label: String,
    val subtitle: String,
    val skyTint: Color,
    val overlayAlpha: Float
) {
    MORNING("Morning", "Golden sunlight", Color(0xFFFFF4D6), 0.08f),
    AFTERNOON("Afternoon", "Clear meadow breeze", Color(0xFFE9F5EC), 0.0f),
    GOLDEN_HOUR("Golden Hour", "Warm amber glow", Color(0xFFE08D4B), 0.16f),
    EVENING("Cozy Evening", "Lanterns & fireflies", Color(0xFF152924), 0.34f);

    fun next(): TimeOfDay = entries[(ordinal + 1) % entries.size]
}

data class CharacterAppearance(
    val name: String = "Maya",
    val skinToneIndex: Int = 1,
    val hairStyle: HairStyle = HairStyle.SOFT_BOB,
    val hairColorIndex: Int = 1,
    val clothingStyle: ClothingStyle = ClothingStyle.COZY_SWEATER,
    val outfitColorIndex: Int = 0,
    val accessory: AccessoryStyle = AccessoryStyle.HEADPHONES
)

data class PresetCharacter(
    val id: String,
    val label: String,
    val appearance: CharacterAppearance
)

enum class InteractableType {
    STUDIO_DESK,
    LIBRARY_NOOK,
    COZY_BED,
    GARDEN_BED,
    POND_BENCH,
    TEA_PATIO
}

data class WorldInteractable(
    val id: String,
    val type: InteractableType,
    val mapLabel: String,
    val name: String,
    val shortPrompt: String,
    val x: Float, // Normalized world coordinates 0f..1f
    val y: Float,
    val radius: Float = 0.125f
)

data class WorldObstacle(
    val id: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

data class WorldNpc(
    val id: String,
    val name: String,
    val role: String,
    val homeX: Float,
    val homeY: Float,
    val skinColor: Color,
    val hairColor: Color,
    val outfitColor: Color,
    val greeting: String,
    val lifestyleTips: Map<DreamLifestyle, List<String>>
)

data class WorldActivityOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val energyDelta: Int,
    val calmDelta: Int,
    val creativityDelta: Int,
    val connectionDelta: Int,
    val outcomeMessage: String,
    val motivationalBubble: String,
    val keepsakeNote: String? = null,
    val isUnlockable: Boolean = false,
    val unlockRequirementText: String? = null
)

data class LifestyleMilestone(
    val id: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int = 0
) {
    val isCompleted: Boolean get() = currentCount >= targetCount
}

data class SimJournalEntry(
    val id: String,
    val dayNumber: Int,
    val timeLabel: String,
    val title: String,
    val note: String
)

data class MotivationalBubble(
    val id: Long,
    val speaker: String,
    val tag: String,
    val message: String
)

data class LifeSimState(
    val phase: SimPhase = SimPhase.INTRO,
    val hasSavedSession: Boolean = false,
    val appearance: CharacterAppearance = CharacterAppearance(),
    val lifestyle: DreamLifestyle = DreamLifestyle.CREATIVE_CAREER,
    val dayNumber: Int = 1,
    val timeOfDay: TimeOfDay = TimeOfDay.MORNING,
    // Player 2D spatial state (normalized 0f..1f)
    val playerX: Float = 0.48f,
    val playerY: Float = 0.50f,
    val targetX: Float? = null,
    val targetY: Float? = null,
    val facingX: Float = 0f,
    val facingY: Float = 1f,
    val walkPhase: Float = 0f,
    // Nurturing non-competitive personal growth stats (0..100)
    val energy: Int = 90,
    val calm: Int = 60,
    val creativity: Int = 55,
    val connection: Int = 50,
    // Visual world evolution & activity counters
    val gardenBloomStage: Int = 0, // 0..4 visually sprouts more flowers
    val studioArtworksCount: Int = 0,
    val booksReadCount: Int = 0,
    val teaBreaksCount: Int = 0,
    val pondReflectionsCount: Int = 0,
    val totalActivitiesDone: Int = 0,
    val npcTalkCounts: Map<String, Int> = emptyMap(),
    // Progression, unlocks & self-discovery
    val unlockedActivityIds: Set<String> = emptySet(),
    val completedLifestyles: Set<DreamLifestyle> = emptySet(),
    val milestones: List<LifestyleMilestone> = emptyList(),
    val journalEntries: List<SimJournalEntry> = emptyList(),
    val lastBannerMessage: String = "Tap anywhere on the map or hold the direction arrows to explore Willowbend!",
    val activeChatBubble: MotivationalBubble? = null,
    val showNewDaySummary: Boolean = false,
    val lastDayHighlight: String = "",
    val showScenarioCompleteReflection: Boolean = false,
    val hasShownCompletionForCurrentRun: Boolean = false
)

object SimPalette {
    val skinTones = listOf(
        Color(0xFFF6D7C3),
        Color(0xFFE7BA9A),
        Color(0xFFC9906B),
        Color(0xFF9C6644),
        Color(0xFF6B4229)
    )
    val hairColors = listOf(
        Color(0xFF2B211E),
        Color(0xFF63412C),
        Color(0xFFD1A054),
        Color(0xFF4F7A5E),
        Color(0xFF8C4A3B)
    )
    val outfitColors = listOf(
        Color(0xFF234B36), // Forest Tunic
        Color(0xFF6A9A78), // Soft Sage
        Color(0xFFC47A53), // Terracotta
        Color(0xFFD6A85C), // Golden Oat
        Color(0xFF4C728A)  // Lake Chambray
    )

    val presets = listOf(
        PresetCharacter(
            id = "preset_maya",
            label = "Maya",
            appearance = CharacterAppearance(
                name = "Maya",
                skinToneIndex = 1,
                hairStyle = HairStyle.SOFT_BOB,
                hairColorIndex = 1,
                clothingStyle = ClothingStyle.CREATIVE_APRON,
                outfitColorIndex = 2,
                accessory = AccessoryStyle.HEADPHONES
            )
        ),
        PresetCharacter(
            id = "preset_leo",
            label = "Leo",
            appearance = CharacterAppearance(
                name = "Leo",
                skinToneIndex = 2,
                hairStyle = HairStyle.WAVY_CROP,
                hairColorIndex = 0,
                clothingStyle = ClothingStyle.COZY_SWEATER,
                outfitColorIndex = 0,
                accessory = AccessoryStyle.ROUND_GLASSES
            )
        ),
        PresetCharacter(
            id = "preset_sam",
            label = "Sam",
            appearance = CharacterAppearance(
                name = "Sam",
                skinToneIndex = 3,
                hairStyle = HairStyle.CURLY_PUFF,
                hairColorIndex = 0,
                clothingStyle = ClothingStyle.CRISP_BLAZER,
                outfitColorIndex = 4,
                accessory = AccessoryStyle.HEADPHONES
            )
        ),
        PresetCharacter(
            id = "preset_ari",
            label = "Ari",
            appearance = CharacterAppearance(
                name = "Ari",
                skinToneIndex = 0,
                hairStyle = HairStyle.COZY_BUN,
                hairColorIndex = 4,
                clothingStyle = ClothingStyle.LINEN_TUNIC,
                outfitColorIndex = 1,
                accessory = AccessoryStyle.LEAF_SPROUT
            )
        )
    )
}
