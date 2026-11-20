package com.example.simulation

import androidx.compose.ui.graphics.Color
import kotlin.math.hypot

object LifeSimEngine {

    const val MASTER_ACTIVITY_UNLOCK_ID = "master_lifestyle_activity"

    /**
     * Solid 2D collision boxes for buildings, raised beds, café table, and pond water
     * so the player walks naturally around objects rather than clipping through them.
     */
    val worldObstacles: List<WorldObstacle> = listOf(
        WorldObstacle("obs_studio", left = 0.12f, top = 0.14f, right = 0.30f, bottom = 0.27f),
        WorldObstacle("obs_library", left = 0.42f, top = 0.11f, right = 0.58f, bottom = 0.22f),
        WorldObstacle("obs_cottage", left = 0.72f, top = 0.12f, right = 0.89f, bottom = 0.26f),
        WorldObstacle("obs_garden", left = 0.13f, top = 0.69f, right = 0.31f, bottom = 0.81f),
        WorldObstacle("obs_pond", left = 0.42f, top = 0.79f, right = 0.65f, bottom = 0.93f),
        WorldObstacle("obs_tea_table", left = 0.75f, top = 0.68f, right = 0.85f, bottom = 0.77f)
    )

    /**
     * Adapts the 6 Willowbend interactive locations to the player's chosen Dream Lifestyle.
     */
    fun getWorldInteractables(lifestyle: DreamLifestyle): List<WorldInteractable> {
        val studioInfo = when (lifestyle) {
            DreamLifestyle.PROFESSIONAL -> Triple("Workspace", "Sunlit Career Workspace", "Focus Work")
            DreamLifestyle.PEACEFUL_LIVING -> Triple("Craft Deck", "Mindful Craft & Music Deck", "Slow Craft")
            DreamLifestyle.ACADEMIC_SUCCESS -> Triple("Study Desk", "Focused Study Desk", "Study Session")
            DreamLifestyle.ENTREPRENEUR -> Triple("Venture Hub", "Founder's Venture Studio", "Build Venture")
            DreamLifestyle.CREATIVE_CAREER -> Triple("Art Studio", "Sunlit Art & Music Studio", "Create Art")
        }

        val libraryInfo = when (lifestyle) {
            DreamLifestyle.PROFESSIONAL -> Triple("Skill Books", "Leadership & Skill Nook", "Grow Skills")
            DreamLifestyle.PEACEFUL_LIVING -> Triple("Reading Nook", "Cedar Quiet Reading Nook", "Gentle Read")
            DreamLifestyle.ACADEMIC_SUCCESS -> Triple("Academy", "Willowbend Research Library", "Deep Research")
            DreamLifestyle.ENTREPRENEUR -> Triple("Strategy", "Founder's Strategy Nook", "Plan Strategy")
            DreamLifestyle.CREATIVE_CAREER -> Triple("Inspiration", "Creative Reference Nook", "Spark Ideas")
        }

        val patioInfo = when (lifestyle) {
            DreamLifestyle.PROFESSIONAL -> Triple("Coffee Spot", "Mentor Coffee Patio", "Coffee & Recharge")
            DreamLifestyle.ENTREPRENEUR -> Triple("Pop-Up Café", "Community Pop-Up Patio", "Connect & Tea")
            else -> Triple("Tea Patio", "Willow Tea Patio", "Tea & Recharge")
        }

        return listOf(
            WorldInteractable(
                id = "studio_desk",
                type = InteractableType.STUDIO_DESK,
                mapLabel = studioInfo.first,
                name = studioInfo.second,
                shortPrompt = studioInfo.third,
                x = 0.21f,
                y = 0.30f,
                radius = 0.135f
            ),
            WorldInteractable(
                id = "library_nook",
                type = InteractableType.LIBRARY_NOOK,
                mapLabel = libraryInfo.first,
                name = libraryInfo.second,
                shortPrompt = libraryInfo.third,
                x = 0.50f,
                y = 0.25f,
                radius = 0.13f
            ),
            WorldInteractable(
                id = "cozy_bed",
                type = InteractableType.COZY_BED,
                mapLabel = "Home Rest",
                name = "Cozy Rest Cottage",
                shortPrompt = "Rest & New Day",
                x = 0.80f,
                y = 0.29f,
                radius = 0.135f
            ),
            WorldInteractable(
                id = "garden_bed",
                type = InteractableType.GARDEN_BED,
                mapLabel = "Garden",
                name = "Botanical Flower Garden",
                shortPrompt = "Tend & Explore",
                x = 0.22f,
                y = 0.66f,
                radius = 0.135f
            ),
            WorldInteractable(
                id = "pond_bench",
                type = InteractableType.POND_BENCH,
                mapLabel = "Pond Bench",
                name = "Stillwater Reflection Bench",
                shortPrompt = "Reflect & Journal",
                x = 0.53f,
                y = 0.74f,
                radius = 0.135f
            ),
            WorldInteractable(
                id = "tea_patio",
                type = InteractableType.TEA_PATIO,
                mapLabel = patioInfo.first,
                name = patioInfo.second,
                shortPrompt = patioInfo.third,
                x = 0.80f,
                y = 0.64f,
                radius = 0.135f
            )
        )
    }

    val worldNpcs: List<WorldNpc> = listOf(
        WorldNpc(
            id = "npc_nora",
            name = "Nora",
            role = "Creative Mentor",
            homeX = 0.35f,
            homeY = 0.38f,
            skinColor = Color(0xFFE7BA9A),
            hairColor = Color(0xFF8C4A3B),
            outfitColor = Color(0xFFC47A53),
            greeting = "Welcome back! I love seeing you show up for your own path.",
            lifestyleTips = mapOf(
                DreamLifestyle.PROFESSIONAL to listOf(
                    "Strong professionals aren't the ones who never pause—they're the ones who focus clearly and respect their own limits.",
                    "Every small project finished with care builds quiet, lasting confidence."
                ),
                DreamLifestyle.PEACEFUL_LIVING to listOf(
                    "Notice the soft green shadows under the trees? Slowing down makes ordinary moments feel spacious.",
                    "You never have to earn your right to rest—peace is a daily practice."
                ),
                DreamLifestyle.ACADEMIC_SUCCESS to listOf(
                    "Curiosity beats cramming every time. One focused chapter with real rest sinks in deeper than hours of stress!",
                    "Asking questions is a sign of strength, not weakness. Keep exploring at your pace."
                ),
                DreamLifestyle.ENTREPRENEUR to listOf(
                    "Every meaningful business started as a rough sketch on a quiet morning. Trust your small experiments!",
                    "Listening to neighbors over tea often reveals the best ideas for serving people well."
                ),
                DreamLifestyle.CREATIVE_CAREER to listOf(
                    "Unfinished sketches still count as creative magic. Try creating for pure curiosity today!",
                    "Your studio wall is coming alive—showing up consistently turns sparks into a real craft."
                )
            )
        ),
        WorldNpc(
            id = "npc_bram",
            name = "Bram",
            role = "Botanical Guide",
            homeX = 0.36f,
            homeY = 0.62f,
            skinColor = Color(0xFF9C6644),
            hairColor = Color(0xFF2B211E),
            outfitColor = Color(0xFF36664B),
            greeting = "Glad to see you in the meadow! Every sprout grows on its own quiet timetable.",
            lifestyleTips = mapOf(
                DreamLifestyle.PROFESSIONAL to listOf(
                    "Stepping outdoors between work blocks clears decision fatigue better than forcing another hour at a desk.",
                    "Sustainable careers grow like perennial gardens—through steady seasons, not overnight sprints."
                ),
                DreamLifestyle.PEACEFUL_LIVING to listOf(
                    "Try tending the flower bed and watching new blooms appear—gentle care compounds beautifully.",
                    "Let the Stillwater Bench be your reminder that stillness is productive for the heart."
                ),
                DreamLifestyle.ACADEMIC_SUCCESS to listOf(
                    "Brains consolidate new learning while you walk and rest! A five-minute garden break actually sharpens memory.",
                    "Celebrate understanding one tricky concept today instead of worrying about the entire syllabus."
                ),
                DreamLifestyle.ENTREPRENEUR to listOf(
                    "Roots grow underground before anyone sees the flowers. Keep nurturing your foundation with patience!",
                    "Resilience is simply watering your vision again after a cloudy day."
                ),
                DreamLifestyle.CREATIVE_CAREER to listOf(
                    "Nature is the oldest sketchbook in the world—every petal and shadow holds new inspiration.",
                    "Give your creative mind fresh air whenever an idea feels stuck."
                )
            )
        ),
        WorldNpc(
            id = "npc_tessa",
            name = "Tessa",
            role = "Patio Host & Connector",
            homeX = 0.67f,
            homeY = 0.58f,
            skinColor = Color(0xFFC9906B),
            hairColor = Color(0xFF2B211E),
            outfitColor = Color(0xFF4C728A),
            greeting = "Pull up a warm chair anytime! How is your energy feeling right now?",
            lifestyleTips = mapOf(
                DreamLifestyle.PROFESSIONAL to listOf(
                    "Great networking is really just genuine kindness and listening. You already have that in you!",
                    "Take a tea break when your energy dips—your best thinking happens when you're rested."
                ),
                DreamLifestyle.PEACEFUL_LIVING to listOf(
                    "Let your shoulders drop an inch as you sip your tea. Nothing is chasing you right now.",
                    "When evening lanterns glow, heading back to your Cozy Cottage welcomes a fresh morning."
                ),
                DreamLifestyle.ACADEMIC_SUCCESS to listOf(
                    "Talking through what you're studying with a friendly listener makes tough ideas click into place.",
                    "Remember to nourish yourself during study days—warm tea and sleep are real study tools."
                ),
                DreamLifestyle.ENTREPRENEUR to listOf(
                    "People remember how warmly you treat them. Building trust one conversation at a time matters most!",
                    "When you're ready, unlock your Pop-Up Showcase at the Venture Studio to celebrate your progress."
                ),
                DreamLifestyle.CREATIVE_CAREER to listOf(
                    "Rest is part of the creative process, too. Refill your energy cup whenever it dips!",
                    "Sharing your craft with supportive neighbors makes creative journeys feel way less lonely."
                )
            )
        )
    )

    fun createMilestonesForLifestyle(lifestyle: DreamLifestyle): List<LifestyleMilestone> {
        return when (lifestyle) {
            DreamLifestyle.PROFESSIONAL -> listOf(
                LifestyleMilestone("m_studio", "Complete 2 Focus Work Blocks", "Build steady momentum at your Career Workspace", 2),
                LifestyleMilestone("m_read", "Grow a Core Skill", "Read a leadership or craft guide at the Skill Nook", 1),
                LifestyleMilestone("m_neighbors", "Connect with 2 Mentors", "Chat with Nora, Bram, or Tessa in Willowbend", 2),
                LifestyleMilestone("m_journal", "Reflect on Work-Life Balance", "Pause at the Stillwater Bench to capture a thought", 1),
                LifestyleMilestone("m_rest", "Honor Rest & Welcome Day 2", "Recharge at the Cozy Rest Cottage after your workday", 1)
            )
            DreamLifestyle.PEACEFUL_LIVING -> listOf(
                LifestyleMilestone("m_garden", "Bloom 2 Garden Patches", "Water and tend wildflowers in the Botanical Garden", 2),
                LifestyleMilestone("m_journal", "2 Mindful Pond Reflections", "Breathe and journal by the Stillwater Bench", 2),
                LifestyleMilestone("m_tea", "Savor a Slow Tea Break", "Rest and sip herbal tea at the Willow Tea Patio", 1),
                LifestyleMilestone("m_bram", "Share a Quiet Chat with Bram", "Talk with Bram the Botanical Guide", 1),
                LifestyleMilestone("m_rest", "Welcome a Fresh Sunrise", "Rest at the Cozy Rest Cottage to begin Day 2", 1)
            )
            DreamLifestyle.ACADEMIC_SUCCESS -> listOf(
                LifestyleMilestone("m_studio", "Finish 2 Study Sessions", "Practice focused study at your Study Desk", 2),
                LifestyleMilestone("m_read", "Complete 2 Research Readings", "Explore chapters at the Willowbend Research Library", 2),
                LifestyleMilestone("m_tea", "Take a Brain-Refresh Break", "Step away to recharge energy at the Tea Patio", 1),
                LifestyleMilestone("m_neighbors", "Discuss Ideas with a Neighbor", "Share curiosity with Nora, Bram, or Tessa", 1),
                LifestyleMilestone("m_rest", "Sleep Well for Memory", "Rest at the Cozy Rest Cottage to start Day 2", 1)
            )
            DreamLifestyle.ENTREPRENEUR -> listOf(
                LifestyleMilestone("m_studio", "Shape 2 Venture Milestones", "Plan and prototype at the Founder's Venture Studio", 2),
                LifestyleMilestone("m_read", "Review Strategy Notes", "Gather calm insight at the Founder's Strategy Nook", 1),
                LifestyleMilestone("m_neighbors", "Talk with 2 Community Voices", "Hear warm perspectives from Nora, Bram, or Tessa", 2),
                LifestyleMilestone("m_journal", "Write a Vision Reflection", "Clarify your purpose at the Stillwater Bench", 1),
                LifestyleMilestone("m_rest", "Protect Founder Rest", "Recharge overnight at the Cozy Rest Cottage", 1)
            )
            DreamLifestyle.CREATIVE_CAREER -> listOf(
                LifestyleMilestone("m_studio", "Create 2 Original Pieces", "Paint or compose at your Sunlit Art & Music Studio", 2),
                LifestyleMilestone("m_garden", "Walk the Garden for Inspiration", "Tend flowers and notice colors in the Botanical Garden", 1),
                LifestyleMilestone("m_nora", "Swap Ideas with Nora", "Chat with Nora the Creative Mentor", 1),
                LifestyleMilestone("m_journal", "Capture a Creative Spark", "Reflect quietly at the Stillwater Bench", 1),
                LifestyleMilestone("m_rest", "Rest & Start Day 2 Inspired", "Sleep at the Cozy Rest Cottage for a new morning", 1)
            )
        }
    }

    fun getActivitiesForInteractable(
        type: InteractableType,
        lifestyle: DreamLifestyle,
        unlockedActivityIds: Set<String>
    ): List<WorldActivityOption> {
        val masterUnlocked = MASTER_ACTIVITY_UNLOCK_ID in unlockedActivityIds

        return when (type) {
            InteractableType.STUDIO_DESK -> {
                val core = when (lifestyle) {
                    DreamLifestyle.PROFESSIONAL -> listOf(
                        WorldActivityOption(
                            id = "pro_deep_work",
                            title = "Complete a Focused Project Sprint",
                            subtitle = "Work steadily on one clear priority (-14 Energy, +14 Growth, +8 Calm)",
                            energyDelta = -14, calmDelta = 8, creativityDelta = 14, connectionDelta = 4,
                            outcomeMessage = "You finished a thoughtful block of work without multitasking—clean, steady progress!",
                            motivationalBubble = "Focus beats frantic hurry. One meaningful step done calmly is real professional strength.",
                            keepsakeNote = "Completed a calm, single-task focus block at my workspace."
                        ),
                        WorldActivityOption(
                            id = "pro_organize",
                            title = "Set Tomorrow's Humane Priorities",
                            subtitle = "Plan 3 achievable goals (-10 Energy, +12 Calm, +10 Growth)",
                            energyDelta = -10, calmDelta = 12, creativityDelta = 10, connectionDelta = 4,
                            outcomeMessage = "A clear, realistic plan replaces scattered worry with quiet readiness.",
                            motivationalBubble = "Clear boundaries around your time protect both your excellence and your wellbeing.",
                            keepsakeNote = "Mapped out 3 realistic priorities instead of an overwhelming list."
                        )
                    )
                    DreamLifestyle.ACADEMIC_SUCCESS -> listOf(
                        WorldActivityOption(
                            id = "acad_study",
                            title = "25-Minute Curious Study Block",
                            subtitle = "Break a big topic into friendly steps (-14 Energy, +16 Growth, +6 Calm)",
                            energyDelta = -14, calmDelta = 6, creativityDelta = 16, connectionDelta = 2,
                            outcomeMessage = "A tricky idea clicked into place! Taking it one page at a time truly worked.",
                            motivationalBubble = "You don't have to master everything tonight—every small study session builds real confidence.",
                            keepsakeNote = "Completed a calm study block focusing on understanding over memorization."
                        ),
                        WorldActivityOption(
                            id = "acad_practice",
                            title = "Summarize Notes in Your Own Words",
                            subtitle = "Turn confusion into clear understanding (-12 Energy, +14 Growth, +8 Calm)",
                            energyDelta = -12, calmDelta = 8, creativityDelta = 14, connectionDelta = 4,
                            outcomeMessage = "Explaining the concept simply made your knowledge feel steady and yours.",
                            motivationalBubble = "Progress looks quiet while it's happening—trust the foundation you're building!",
                            keepsakeNote = "Wrote friendly summary notes that made the material feel approachable."
                        )
                    )
                    DreamLifestyle.ENTREPRENEUR -> listOf(
                        WorldActivityOption(
                            id = "biz_prototype",
                            title = "Prototype a Helpful Idea",
                            subtitle = "Turn a small spark into a simple draft (-14 Energy, +16 Growth, +6 Calm)",
                            energyDelta = -14, calmDelta = 6, creativityDelta = 16, connectionDelta = 6,
                            outcomeMessage = "Your rough prototype is now on the board—imperfect, tangible, and exciting!",
                            motivationalBubble = "Brave beginnings don't need perfection. Small real steps teach more than endless waiting.",
                            keepsakeNote = "Sketched a tangible first draft of my venture idea at the studio."
                        ),
                        WorldActivityOption(
                            id = "biz_budget",
                            title = "Design a Sustainable Rhythm",
                            subtitle = "Map thoughtful goals that respect your wellbeing (-10 Energy, +10 Calm, +12 Growth)",
                            energyDelta = -10, calmDelta = 10, creativityDelta = 12, connectionDelta = 4,
                            outcomeMessage = "You aligned your vision with a healthy pace you can genuinely sustain.",
                            motivationalBubble = "A healthy founder is a resilient founder—build a business that supports your life too.",
                            keepsakeNote = "Balanced my venture plan with sustainable pacing and clear milestones."
                        )
                    )
                    else -> listOf(
                        WorldActivityOption(
                            id = "studio_paint",
                            title = "Paint a Botanical Canvas",
                            subtitle = "Express colors freely with zero pressure (-14 Energy, +16 Creativity, +8 Calm)",
                            energyDelta = -14, calmDelta = 8, creativityDelta = 16, connectionDelta = 2,
                            outcomeMessage = "Soft sage and sunlit ochre swirl across your easel—a new artwork brightens your space!",
                            motivationalBubble = "Creating without harsh self-judgment unlocks a joyful kind of bravery.",
                            keepsakeNote = "Painted freely at the sunlit desk—focused on curiosity instead of perfection."
                        ),
                        WorldActivityOption(
                            id = "studio_music",
                            title = "Compose a Calm Melody",
                            subtitle = "Layer gentle acoustic chords (-12 Energy, +14 Creativity, +10 Calm)",
                            energyDelta = -12, calmDelta = 10, creativityDelta = 14, connectionDelta = 4,
                            outcomeMessage = "A soothing acoustic loop drifts gently out into the pine breeze.",
                            motivationalBubble = "Your voice and perspective matter—let yourself make things simply because they feel alive.",
                            keepsakeNote = "Composed a soft melody that helped my whole body exhale."
                        )
                    )
                }

                // Bonus unlockable master activity (unlocked after completing 2 activities!)
                val bonusTitle = when (lifestyle) {
                    DreamLifestyle.PROFESSIONAL -> "Lead a Warm Team Workshop"
                    DreamLifestyle.PEACEFUL_LIVING -> "Host a Sunset Sound & Calm Session"
                    DreamLifestyle.ACADEMIC_SUCCESS -> "Present Your Breakthrough Seminar"
                    DreamLifestyle.ENTREPRENEUR -> "Open Your Willowbend Pop-Up Showcase"
                    DreamLifestyle.CREATIVE_CAREER -> "Host an Open Studio Gallery Evening"
                }
                val bonusOption = WorldActivityOption(
                    id = MASTER_ACTIVITY_UNLOCK_ID,
                    title = if (masterUnlocked) "★ $bonusTitle" else "Locked: $bonusTitle",
                    subtitle = if (masterUnlocked) {
                        "Unlocked Milestone Activity! Celebrate your growth (-12 Energy, +18 Growth, +14 Connection)"
                    } else {
                        "Complete any 2 Willowbend activities to unlock this signature milestone!"
                    },
                    energyDelta = -12,
                    calmDelta = 12,
                    creativityDelta = 18,
                    connectionDelta = 14,
                    outcomeMessage = "Neighbors gather warmly to celebrate your milestone—look how far small steady steps carried you!",
                    motivationalBubble = "You unlocked this through steady curiosity and care. Imagine what small consistent habits can open in real life!",
                    keepsakeNote = "Celebrated my unlocked milestone: $bonusTitle.",
                    isUnlockable = !masterUnlocked,
                    unlockRequirementText = "Complete 2 activities to unlock"
                )
                core + bonusOption
            }

            InteractableType.LIBRARY_NOOK -> listOf(
                WorldActivityOption(
                    id = "library_read",
                    title = "Read a Focused Chapter",
                    subtitle = "Learn a practical idea at a relaxed pace (-10 Energy, +10 Calm, +14 Growth)",
                    energyDelta = -10, calmDelta = 10, creativityDelta = 14, connectionDelta = 4,
                    outcomeMessage = "A crisp new perspective clicks—quiet learning feels energizing when nobody is rushing you.",
                    motivationalBubble = "Curiosity is a lifelong compass. Every page you explore widens what feels possible.",
                    keepsakeNote = "Spent peaceful time learning at the Cedar Nook."
                ),
                WorldActivityOption(
                    id = "library_notes",
                    title = "Collect Inspiring Ideas",
                    subtitle = "Jot takeaways for your real-life goals (-10 Energy, +8 Calm, +16 Growth)",
                    energyDelta = -10, calmDelta = 8, creativityDelta = 16, connectionDelta = 2,
                    outcomeMessage = "Your notebook holds a handful of small, realistic ideas you actually feel excited to try.",
                    motivationalBubble = "You don't need a ten-year blueprint today—one encouraging next step is plenty.",
                    keepsakeNote = "Collected gentle ideas I can bring into my real-world routine."
                )
            )

            InteractableType.GARDEN_BED -> listOf(
                WorldActivityOption(
                    id = "garden_water",
                    title = "Water & Tend Wildflowers",
                    subtitle = "Watch new blooms visibly sprout on your map (-10 Energy, +14 Calm, +8 Growth)",
                    energyDelta = -10, calmDelta = 14, creativityDelta = 8, connectionDelta = 4,
                    outcomeMessage = "Cool droplets glisten on the leaves—fresh meadow flowers bloom right before your eyes!",
                    motivationalBubble = "Growth rarely happens all at once. Small daily care turns quiet soil into blossoms.",
                    keepsakeNote = "Tended the garden and watched new wildflowers bloom."
                ),
                WorldActivityOption(
                    id = "garden_walk",
                    title = "Explore the Botanical Trail",
                    subtitle = "Stretch, notice 3 green textures & reset (-8 Energy, +16 Calm, +6 Growth)",
                    energyDelta = -8, calmDelta = 16, creativityDelta = 6, connectionDelta = 4,
                    outcomeMessage = "Fresh pine air clears away mental fog and restores steady perspective.",
                    motivationalBubble = "Stepping away for five gentle minutes isn't falling behind—it's how you stay resilient.",
                    keepsakeNote = "Took a grounding botanical walk to refresh my mind."
                )
            )

            InteractableType.POND_BENCH -> listOf(
                WorldActivityOption(
                    id = "pond_breathe",
                    title = "5 Slow Stillwater Breaths",
                    subtitle = "Settle racing thoughts by the water (-4 Energy, +18 Calm, +4 Growth)",
                    energyDelta = -4, calmDelta = 18, creativityDelta = 4, connectionDelta = 4,
                    outcomeMessage = "With each unhurried exhale, ripples settle and clarity gently returns.",
                    motivationalBubble = "Your calm is always within reach. Even three slow breaths can shift how a tough moment feels.",
                    keepsakeNote = "Practiced stillness by the pond: ${lifestyle.mantra}"
                ),
                WorldActivityOption(
                    id = "pond_vision",
                    title = "Imagine One Real-Life Step",
                    subtitle = "Connect this scenario to an everyday aspiration (-6 Energy, +12 Calm, +10 Growth)",
                    energyDelta = -6, calmDelta = 12, creativityDelta = 10, connectionDelta = 6,
                    outcomeMessage = "You pictured one realistic, kind action you can try in your own week.",
                    motivationalBubble = "Imagining your ideal day isn't daydreaming—it's rehearsal for choosing what matters to you.",
                    keepsakeNote = "Envisioned bringing one gentle Willowbend habit into real life."
                )
            )

            InteractableType.TEA_PATIO -> listOf(
                WorldActivityOption(
                    id = "tea_chamomile",
                    title = "Sip Warm Herbal Tea & Rest",
                    subtitle = "Self-care recharge in the dappled shade (+30 Energy, +12 Calm, +6 Connection)",
                    energyDelta = 30, calmDelta = 12, creativityDelta = 4, connectionDelta = 6,
                    outcomeMessage = "Warm steam curls upward—your energy refills and your shoulders soften.",
                    motivationalBubble = "Taking care of your energy is smart, not selfish. Rest fuels every dream worth building.",
                    keepsakeNote = "Paused at the Tea Patio to recharge my energy with kindness."
                ),
                WorldActivityOption(
                    id = "tea_table",
                    title = "Share Tea & Encouragement",
                    subtitle = "Enjoy warm company at the patio (+20 Energy, +8 Calm, +16 Connection)",
                    energyDelta = 20, calmDelta = 8, creativityDelta = 4, connectionDelta = 16,
                    outcomeMessage = "Easy laughter and friendly stories remind you that you don't walk alone.",
                    motivationalBubble = "Supportive connection turns heavy days lighter and joyful days brighter.",
                    keepsakeNote = "Shared warm conversation and tea at the Willow Patio."
                )
            )

            InteractableType.COZY_BED -> listOf(
                WorldActivityOption(
                    id = "bed_sleep",
                    title = "Rest Overnight & Welcome a New Day",
                    subtitle = "Refreshes Energy to 100% and opens a bright new morning in Willowbend!",
                    energyDelta = 100, calmDelta = 14, creativityDelta = 4, connectionDelta = 4,
                    outcomeMessage = "You wake to golden morning sunlight and 100% refreshed energy!",
                    motivationalBubble = "Every new morning is a gentle fresh page—you carry yesterday's wisdom without its weight."
                )
            )
        }
    }

    fun findNearestInteractable(playerX: Float, playerY: Float, lifestyle: DreamLifestyle): WorldInteractable? {
        return getWorldInteractables(lifestyle)
            .map { it to hypot(playerX - it.x, playerY - it.y) }
            .filter { (obj, dist) -> dist <= obj.radius + 0.04f }
            .minByOrNull { it.second }
            ?.first
    }

    fun findNearestNpc(playerX: Float, playerY: Float): WorldNpc? {
        return worldNpcs
            .map { it to hypot(playerX - it.homeX, playerY - it.homeY) }
            .filter { (_, dist) -> dist <= 0.14f }
            .minByOrNull { it.second }
            ?.first
    }

    /**
     * Real 2D collision detection with axis-separated sliding so the character smoothly slides
     * along building walls, raised garden beds, tables, and the pond shoreline without clipping.
     */
    fun resolveMovementWithCollision(
        currentX: Float,
        currentY: Float,
        desiredX: Float,
        desiredY: Float,
        playerRadius: Float = 0.028f
    ): Pair<Float, Float> {
        val clampedX = desiredX.coerceIn(0.07f, 0.93f)
        val clampedY = desiredY.coerceIn(0.11f, 0.90f)

        // Try X movement first
        val finalX = if (!collidesWithAnyObstacle(clampedX, currentY, playerRadius)) {
            clampedX
        } else {
            currentX
        }

        // Then try Y movement independently (allows smooth wall sliding!)
        val finalY = if (!collidesWithAnyObstacle(finalX, clampedY, playerRadius)) {
            clampedY
        } else {
            currentY
        }

        return finalX to finalY
    }

    fun collidesWithAnyObstacle(x: Float, y: Float, radius: Float = 0.028f): Boolean {
        return worldObstacles.any { obs ->
            val closestX = x.coerceIn(obs.left, obs.right)
            val closestY = y.coerceIn(obs.top, obs.bottom)
            hypot(x - closestX, y - closestY) < radius
        }
    }

    /**
     * Adjusts tap targets that land inside a solid obstacle to the closest walkable edge nearby.
     */
    fun findWalkableTargetNear(rawX: Float, rawY: Float): Pair<Float, Float> {
        var tx = rawX.coerceIn(0.07f, 0.93f)
        var ty = rawY.coerceIn(0.11f, 0.90f)
        if (!collidesWithAnyObstacle(tx, ty)) return tx to ty

        // Push outward to nearest free edge of blocking obstacle
        val blocking = worldObstacles.firstOrNull { obs ->
            val cx = tx.coerceIn(obs.left, obs.right)
            val cy = ty.coerceIn(obs.top, obs.bottom)
            hypot(tx - cx, ty - cy) < 0.03f
        } ?: return tx to ty

        val candidates = listOf(
            tx to (blocking.bottom + 0.038f),
            tx to (blocking.top - 0.038f),
            (blocking.left - 0.038f) to ty,
            (blocking.right + 0.038f) to ty
        ).map { (cx, cy) -> cx.coerceIn(0.07f, 0.93f) to cy.coerceIn(0.11f, 0.90f) }

        return candidates
            .filterNot { (cx, cy) -> collidesWithAnyObstacle(cx, cy) }
            .minByOrNull { (cx, cy) -> hypot(cx - tx, cy - ty) }
            ?: (0.48f to 0.50f)
    }

    fun evaluateMilestones(state: LifeSimState): List<LifestyleMilestone> {
        val base = if (state.milestones.isEmpty()) {
            createMilestonesForLifestyle(state.lifestyle)
        } else {
            state.milestones
        }
        val uniqueNpcsSpoken = state.npcTalkCounts.count { it.value > 0 }
        return base.map { m ->
            val progress = when (m.id) {
                "m_studio" -> state.studioArtworksCount
                "m_garden" -> state.gardenBloomStage
                "m_nora" -> state.npcTalkCounts["npc_nora"] ?: 0
                "m_bram" -> state.npcTalkCounts["npc_bram"] ?: 0
                "m_neighbors" -> uniqueNpcsSpoken
                "m_journal" -> state.pondReflectionsCount + state.journalEntries.count { it.title == "Personal Reflection" }
                "m_tea" -> state.teaBreaksCount
                "m_read" -> state.booksReadCount
                "m_rest" -> (state.dayNumber - 1).coerceAtLeast(0)
                else -> m.currentCount
            }
            m.copy(currentCount = progress.coerceAtMost(m.targetCount))
        }
    }
}
