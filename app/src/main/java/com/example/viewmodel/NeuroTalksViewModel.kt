package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ActivityEntry
import com.example.data.DemoRepository
import com.example.data.DemoSettings
import com.example.data.ForumCategory
import com.example.data.ForumPost
import com.example.data.ForumReply
import com.example.data.MoodOption
import com.example.data.PodcastEpisode
import com.example.data.PodcastStyle
import com.example.simulation.CharacterAppearance
import com.example.simulation.DreamLifestyle
import com.example.simulation.InteractableType
import com.example.simulation.LifeSimEngine
import com.example.simulation.LifeSimState
import com.example.simulation.LifeSimStorage
import com.example.simulation.MotivationalBubble
import com.example.simulation.SimJournalEntry
import com.example.simulation.SimPhase
import com.example.simulation.TimeOfDay
import com.example.simulation.WorldActivityOption
import com.example.simulation.WorldInteractable
import com.example.simulation.WorldNpc
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.hypot

data class NeuroTalksUiState(
    val userAlias: String = DemoRepository.anonymousAliases.first(),
    val guidelinesAccepted: Boolean = false,
    val selectedMood: MoodOption? = null,
    val checkInsCompleted: Int = 1,
    // Podcast state
    val selectedPodcastTopic: String = "All Topics",
    val selectedPodcastStyle: PodcastStyle = PodcastStyle.ALL,
    val currentEpisodeId: String = DemoRepository.podcastEpisodes.first().id,
    val isPlayingAudio: Boolean = false,
    val playbackSeconds: Int = 92,
    val playbackSpeed: Float = 1.0f,
    val bookmarkedEpisodeIds: Set<String> = setOf("ep_1"),
    val completedEpisodeIds: Set<String> = emptySet(),
    // Playable 2D Ideal Life Simulation state
    val lifeSim: LifeSimState = LifeSimState(
        milestones = LifeSimEngine.createMilestonesForLifestyle(DreamLifestyle.CREATIVE_CAREER)
    ),
    val simulationsCompletedCount: Int = 1,
    // Forum state
    val selectedForumCategory: ForumCategory = ForumCategory.ALL,
    val forumPosts: List<ForumPost> = DemoRepository.initialForumPosts,
    val expandedPostIds: Set<String> = setOf("post_1"),
    val notesSharedCount: Int = 0,
    // Profile & settings
    val activities: List<ActivityEntry> = DemoRepository.initialActivities,
    val settings: DemoSettings = DemoSettings()
) {
    val currentEpisode: PodcastEpisode
        get() = DemoRepository.podcastEpisodes.firstOrNull { it.id == currentEpisodeId }
            ?: DemoRepository.podcastEpisodes.first()
}

class NeuroTalksViewModel(application: Application) : AndroidViewModel(application) {

    private val lifeSimStorage = LifeSimStorage(application.applicationContext)

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<NeuroTalksUiState> = _uiState.asStateFlow()

    private var audioTimerJob: Job? = null

    private fun createInitialState(): NeuroTalksUiState {
        val savedSim = lifeSimStorage.loadState()
        return if (savedSim != null) {
            NeuroTalksUiState(
                lifeSim = savedSim,
                simulationsCompletedCount = (1 + savedSim.completedLifestyles.size)
            )
        } else {
            NeuroTalksUiState()
        }
    }

    private fun persistLifeSim(sim: LifeSimState) {
        lifeSimStorage.saveState(sim)
    }

    fun setGuidelinesAccepted(accepted: Boolean) {
        _uiState.update { it.copy(guidelinesAccepted = accepted) }
    }

    fun selectDailyMood(mood: MoodOption) {
        _uiState.update { state ->
            val isNew = state.selectedMood == null
            state.copy(
                selectedMood = mood,
                checkInsCompleted = if (isNew) state.checkInsCompleted + 1 else state.checkInsCompleted,
                activities = prependActivity(
                    state.activities,
                    title = "Daily Check-in: ${mood.label}",
                    detail = mood.tagline,
                    category = "Check-in"
                )
            )
        }
    }

    // --- Podcast Actions ---

    fun selectPodcastTopic(topic: String) {
        _uiState.update { it.copy(selectedPodcastTopic = topic) }
    }

    fun selectPodcastStyle(style: PodcastStyle) {
        _uiState.update { it.copy(selectedPodcastStyle = style) }
    }

    fun selectEpisode(episodeId: String, autoPlay: Boolean = true) {
        val episode = DemoRepository.podcastEpisodes.firstOrNull { it.id == episodeId } ?: return
        _uiState.update { state ->
            val changed = state.currentEpisodeId != episodeId
            state.copy(
                currentEpisodeId = episodeId,
                playbackSeconds = if (changed) 0 else state.playbackSeconds,
                isPlayingAudio = autoPlay,
                activities = if (changed) {
                    prependActivity(
                        state.activities,
                        title = "Listening to Podcast",
                        detail = episode.title,
                        category = "Podcast"
                    )
                } else state.activities
            )
        }
        if (autoPlay) startAudioTicker() else stopAudioTicker()
    }

    fun togglePlayPause() {
        val willPlay = !_uiState.value.isPlayingAudio
        _uiState.update { it.copy(isPlayingAudio = willPlay) }
        if (willPlay) startAudioTicker() else stopAudioTicker()
    }

    fun seekAudio(seconds: Int) {
        val maxSec = _uiState.value.currentEpisode.durationSeconds
        _uiState.update { it.copy(playbackSeconds = seconds.coerceIn(0, maxSec)) }
    }

    fun skipAudio(deltaSeconds: Int) {
        seekAudio(_uiState.value.playbackSeconds + deltaSeconds)
    }

    fun cyclePlaybackSpeed() {
        val next = when (_uiState.value.playbackSpeed) {
            1.0f -> 1.25f
            1.25f -> 1.5f
            else -> 1.0f
        }
        _uiState.update { it.copy(playbackSpeed = next) }
    }

    fun toggleBookmark(episodeId: String) {
        val episode = DemoRepository.podcastEpisodes.firstOrNull { it.id == episodeId } ?: return
        _uiState.update { state ->
            val exists = episodeId in state.bookmarkedEpisodeIds
            val updated = if (exists) state.bookmarkedEpisodeIds - episodeId else state.bookmarkedEpisodeIds + episodeId
            state.copy(
                bookmarkedEpisodeIds = updated,
                activities = if (!exists) {
                    prependActivity(
                        state.activities,
                        title = "Saved Podcast Episode",
                        detail = episode.title,
                        category = "Podcast"
                    )
                } else state.activities
            )
        }
    }

    private fun startAudioTicker() {
        audioTimerJob?.cancel()
        audioTimerJob = viewModelScope.launch {
            while (_uiState.value.isPlayingAudio) {
                delay(1000L)
                _uiState.update { state ->
                    val maxSec = state.currentEpisode.durationSeconds
                    val step = if (state.playbackSpeed >= 1.5f) 2 else 1
                    val nextSec = (state.playbackSeconds + step).coerceAtMost(maxSec)
                    if (nextSec >= maxSec) {
                        state.copy(
                            playbackSeconds = maxSec,
                            isPlayingAudio = false,
                            completedEpisodeIds = state.completedEpisodeIds + state.currentEpisodeId
                        )
                    } else {
                        state.copy(playbackSeconds = nextSec)
                    }
                }
            }
        }
    }

    private fun stopAudioTicker() {
        audioTimerJob?.cancel()
        audioTimerJob = null
    }

    // --- Playable 2D Ideal Life Simulation Actions ---

    fun setLifeSimPhase(phase: SimPhase) {
        _uiState.update { state ->
            state.copy(
                lifeSim = state.lifeSim.copy(
                    phase = phase,
                    targetX = null,
                    targetY = null
                )
            )
        }
    }

    fun continueSavedLifeSim() {
        val now = System.currentTimeMillis()
        _uiState.update { state ->
            val sim = state.lifeSim
            val name = sim.appearance.name.ifBlank { "Maya" }
            val nextSim = sim.copy(
                phase = SimPhase.PLAYING_WORLD,
                hasSavedSession = true,
                activeChatBubble = MotivationalBubble(
                    id = now,
                    speaker = "Willowbend Guide",
                    tag = "Welcome Back",
                    message = "Welcome back, $name! Every small step you take here celebrates your own pace and growth."
                )
            )
            state.copy(lifeSim = nextSim)
        }
    }

    fun selectLifeSimLifestyle(lifestyle: DreamLifestyle) {
        _uiState.update { state ->
            val switchedLifestyle = state.lifeSim.lifestyle != lifestyle
            val nextSim = state.lifeSim.copy(
                lifestyle = lifestyle,
                milestones = LifeSimEngine.createMilestonesForLifestyle(lifestyle),
                hasShownCompletionForCurrentRun = if (switchedLifestyle) false else state.lifeSim.hasShownCompletionForCurrentRun
            )
            val evaluated = nextSim.copy(milestones = LifeSimEngine.evaluateMilestones(nextSim))
            persistLifeSim(evaluated)
            state.copy(lifeSim = evaluated)
        }
    }

    fun selectPresetCharacter(appearance: CharacterAppearance) {
        _uiState.update { state ->
            val updated = state.lifeSim.copy(appearance = appearance)
            persistLifeSim(updated)
            state.copy(lifeSim = updated)
        }
    }

    fun updateLifeSimAppearance(transform: (CharacterAppearance) -> CharacterAppearance) {
        _uiState.update { state ->
            val updated = state.lifeSim.copy(appearance = transform(state.lifeSim.appearance))
            persistLifeSim(updated)
            state.copy(lifeSim = updated)
        }
    }

    fun enterLifeSimWorld() {
        val now = System.currentTimeMillis()
        _uiState.update { state ->
            val sim = state.lifeSim
            val name = sim.appearance.name.ifBlank { "Maya" }
            val freshDraft = sim.copy(
                phase = SimPhase.PLAYING_WORLD,
                hasSavedSession = true,
                appearance = sim.appearance.copy(name = name),
                lastBannerMessage = "Tap anywhere on the map or hold the arrows to explore Willowbend!",
                activeChatBubble = MotivationalBubble(
                    id = now,
                    speaker = "Willowbend Guide",
                    tag = "Self-Discovery",
                    message = "Welcome to ${sim.lifestyle.title}, $name! \"${sim.lifestyle.mantra}\""
                )
            )
            val evaluated = freshDraft.copy(milestones = LifeSimEngine.evaluateMilestones(freshDraft))
            persistLifeSim(evaluated)
            state.copy(
                lifeSim = evaluated,
                activities = prependActivity(
                    state.activities,
                    title = "Explored Ideal Life World",
                    detail = "$name • ${sim.lifestyle.title}",
                    category = "Life Sim"
                )
            )
        }
    }

    fun setLifeSimWalkTarget(worldX: Float, worldY: Float) {
        val (safeX, safeY) = LifeSimEngine.findWalkableTargetNear(worldX, worldY)
        _uiState.update { state ->
            state.copy(
                lifeSim = state.lifeSim.copy(
                    targetX = safeX,
                    targetY = safeY
                )
            )
        }
    }

    /**
     * Smooth directional step with real solid obstacle collision detection & sliding.
     */
    fun moveLifeSimByDirection(dx: Float, dy: Float) {
        val speed = 0.014f
        _uiState.update { state ->
            val sim = state.lifeSim
            val (nextX, nextY) = LifeSimEngine.resolveMovementWithCollision(
                currentX = sim.playerX,
                currentY = sim.playerY,
                desiredX = sim.playerX + dx * speed,
                desiredY = sim.playerY + dy * speed
            )
            state.copy(
                lifeSim = sim.copy(
                    playerX = nextX,
                    playerY = nextY,
                    targetX = null,
                    targetY = null,
                    facingX = dx,
                    facingY = dy,
                    walkPhase = sim.walkPhase + 0.25f
                )
            )
        }
    }

    fun tickLifeSimMovement() {
        _uiState.update { state ->
            val sim = state.lifeSim
            val tx = sim.targetX ?: return@update state
            val ty = sim.targetY ?: return@update state
            val dx = tx - sim.playerX
            val dy = ty - sim.playerY
            val dist = hypot(dx, dy)
            val speed = 0.012f
            if (dist <= speed) {
                val (cx, cy) = LifeSimEngine.resolveMovementWithCollision(sim.playerX, sim.playerY, tx, ty)
                val stopped = sim.copy(
                    playerX = cx,
                    playerY = cy,
                    targetX = null,
                    targetY = null
                )
                persistLifeSim(stopped)
                state.copy(lifeSim = stopped)
            } else {
                val stepX = (dx / dist) * speed
                val stepY = (dy / dist) * speed
                val (cx, cy) = LifeSimEngine.resolveMovementWithCollision(
                    sim.playerX,
                    sim.playerY,
                    sim.playerX + stepX,
                    sim.playerY + stepY
                )
                val movedDist = hypot(cx - sim.playerX, cy - sim.playerY)
                if (movedDist < 0.001f) {
                    // Reached an obstacle boundary; stop cleanly without vibrating
                    val stopped = sim.copy(targetX = null, targetY = null)
                    persistLifeSim(stopped)
                    state.copy(lifeSim = stopped)
                } else {
                    state.copy(
                        lifeSim = sim.copy(
                            playerX = cx,
                            playerY = cy,
                            facingX = dx / dist,
                            facingY = dy / dist,
                            walkPhase = sim.walkPhase + 0.24f
                        )
                    )
                }
            }
        }
    }

    fun performLifeSimActivity(interactable: WorldInteractable, option: WorldActivityOption) {
        if (option.isUnlockable) return
        val now = System.currentTimeMillis()
        _uiState.update { state ->
            val sim = state.lifeSim
            val isSleep = interactable.type == InteractableType.COZY_BED

            val nextDay = if (isSleep) sim.dayNumber + 1 else sim.dayNumber
            val nextTime = if (isSleep) TimeOfDay.MORNING else sim.timeOfDay.next()
            val nextEnergy = if (isSleep) 100 else (sim.energy + option.energyDelta).coerceIn(10, 100)
            val nextCalm = (sim.calm + option.calmDelta).coerceIn(10, 100)
            val nextCreativity = (sim.creativity + option.creativityDelta).coerceIn(10, 100)
            val nextConnection = (sim.connection + option.connectionDelta).coerceIn(10, 100)

            val nextGarden = if (interactable.type == InteractableType.GARDEN_BED) {
                (sim.gardenBloomStage + 1).coerceAtMost(4)
            } else sim.gardenBloomStage

            val nextStudio = if (interactable.type == InteractableType.STUDIO_DESK) {
                sim.studioArtworksCount + 1
            } else sim.studioArtworksCount

            val nextBooks = if (interactable.type == InteractableType.LIBRARY_NOOK) {
                sim.booksReadCount + 1
            } else sim.booksReadCount

            val nextTea = if (interactable.type == InteractableType.TEA_PATIO) {
                sim.teaBreaksCount + 1
            } else sim.teaBreaksCount

            val nextPond = if (interactable.type == InteractableType.POND_BENCH) {
                sim.pondReflectionsCount + 1
            } else sim.pondReflectionsCount

            val nextTotalActivities = sim.totalActivitiesDone + 1
            val justUnlockedMaster =
                nextTotalActivities >= 2 && LifeSimEngine.MASTER_ACTIVITY_UNLOCK_ID !in sim.unlockedActivityIds
            val nextUnlocked = if (nextTotalActivities >= 2) {
                sim.unlockedActivityIds + LifeSimEngine.MASTER_ACTIVITY_UNLOCK_ID
            } else sim.unlockedActivityIds

            val updatedJournal = if (!option.keepsakeNote.isNullOrBlank()) {
                listOf(
                    SimJournalEntry(
                        id = "j_$now",
                        dayNumber = sim.dayNumber,
                        timeLabel = sim.timeOfDay.label,
                        title = option.title,
                        note = option.keepsakeNote
                    )
                ) + sim.journalEntries
            } else sim.journalEntries

            val highlight = if (isSleep) {
                "Yesterday you completed $nextTotalActivities thoughtful activities and gathered ${updatedJournal.size} reflections."
            } else sim.lastDayHighlight

            // Build encouraging motivational chat bubble (self-care challenge, unlock celebration, or activity boost)
            val bubble = when {
                justUnlockedMaster -> MotivationalBubble(
                    id = now,
                    speaker = "Milestone Unlocked!",
                    tag = "Personal Growth",
                    message = "You unlocked your signature milestone activity at ${interactable.name}! Small steady steps open new doors."
                )
                nextEnergy <= 22 && !isSleep -> MotivationalBubble(
                    id = now,
                    speaker = "Gentle Self-Care",
                    tag = "Resilience",
                    message = "Your energy dipped to $nextEnergy%—and that's okay! Listening when you're tired and pausing for tea or rest is true self-care."
                )
                else -> MotivationalBubble(
                    id = now,
                    speaker = interactable.name,
                    tag = if (isSleep) "Fresh Start" else "Encouragement",
                    message = option.motivationalBubble
                )
            }

            val draftSim = sim.copy(
                hasSavedSession = true,
                dayNumber = nextDay,
                timeOfDay = nextTime,
                energy = nextEnergy,
                calm = nextCalm,
                creativity = nextCreativity,
                connection = nextConnection,
                gardenBloomStage = nextGarden,
                studioArtworksCount = nextStudio,
                booksReadCount = nextBooks,
                teaBreaksCount = nextTea,
                pondReflectionsCount = nextPond,
                totalActivitiesDone = nextTotalActivities,
                unlockedActivityIds = nextUnlocked,
                journalEntries = updatedJournal.take(20),
                lastBannerMessage = option.outcomeMessage,
                activeChatBubble = bubble,
                showNewDaySummary = isSleep,
                lastDayHighlight = highlight
            )

            val prevCompleted = sim.milestones.count { it.isCompleted }
            val evaluatedMilestones = LifeSimEngine.evaluateMilestones(draftSim)
            val newCompleted = evaluatedMilestones.count { it.isCompleted }
            val allMilestonesDone = evaluatedMilestones.isNotEmpty() && evaluatedMilestones.all { it.isCompleted }

            val triggerCompletionModal = allMilestonesDone && !sim.hasShownCompletionForCurrentRun && !isSleep
            val updatedCompletedLifestyles = if (allMilestonesDone) {
                sim.completedLifestyles + sim.lifestyle
            } else sim.completedLifestyles

            val finalSim = draftSim.copy(
                milestones = evaluatedMilestones,
                completedLifestyles = updatedCompletedLifestyles,
                showScenarioCompleteReflection = triggerCompletionModal,
                hasShownCompletionForCurrentRun = sim.hasShownCompletionForCurrentRun || triggerCompletionModal
            )
            persistLifeSim(finalSim)

            state.copy(
                lifeSim = finalSim,
                simulationsCompletedCount = state.simulationsCompletedCount + if (newCompleted > prevCompleted) 1 else 0,
                activities = prependActivity(
                    state.activities,
                    title = "Willowbend: ${option.title}",
                    detail = option.outcomeMessage.take(54) + if (option.outcomeMessage.length > 54) "…" else "",
                    category = "Life Sim"
                )
            )
        }
    }

    fun talkToLifeSimNpc(npc: WorldNpc, insightText: String) {
        val now = System.currentTimeMillis()
        _uiState.update { state ->
            val sim = state.lifeSim
            val nextCounts = sim.npcTalkCounts.toMutableMap().apply {
                this[npc.id] = (this[npc.id] ?: 0) + 1
            }
            val nextJournal = listOf(
                SimJournalEntry(
                    id = "j_npc_$now",
                    dayNumber = sim.dayNumber,
                    timeLabel = sim.timeOfDay.label,
                    title = "Chat with ${npc.name}",
                    note = insightText
                )
            ) + sim.journalEntries

            val draftSim = sim.copy(
                hasSavedSession = true,
                connection = (sim.connection + 12).coerceIn(10, 100),
                calm = (sim.calm + 6).coerceIn(10, 100),
                npcTalkCounts = nextCounts,
                journalEntries = nextJournal.take(20),
                lastBannerMessage = "${npc.name} shared warm encouragement with you.",
                activeChatBubble = MotivationalBubble(
                    id = now,
                    speaker = npc.name,
                    tag = "Connection",
                    message = insightText
                )
            )

            val prevCompleted = sim.milestones.count { it.isCompleted }
            val evaluated = LifeSimEngine.evaluateMilestones(draftSim)
            val newCompleted = evaluated.count { it.isCompleted }
            val allDone = evaluated.isNotEmpty() && evaluated.all { it.isCompleted }
            val triggerCompletion = allDone && !sim.hasShownCompletionForCurrentRun

            val finalSim = draftSim.copy(
                milestones = evaluated,
                completedLifestyles = if (allDone) sim.completedLifestyles + sim.lifestyle else sim.completedLifestyles,
                showScenarioCompleteReflection = triggerCompletion,
                hasShownCompletionForCurrentRun = sim.hasShownCompletionForCurrentRun || triggerCompletion
            )
            persistLifeSim(finalSim)

            state.copy(
                lifeSim = finalSim,
                simulationsCompletedCount = state.simulationsCompletedCount + if (newCompleted > prevCompleted) 1 else 0,
                activities = prependActivity(
                    state.activities,
                    title = "Chatted with ${npc.name}",
                    detail = "${npc.role} in Willowbend Sanctuary",
                    category = "Life Sim"
                )
            )
        }
    }

    fun addLifeSimJournalReflection(note: String) {
        val clean = note.trim()
        if (clean.isEmpty()) return
        val now = System.currentTimeMillis()
        _uiState.update { state ->
            val sim = state.lifeSim
            val entry = SimJournalEntry(
                id = "j_custom_$now",
                dayNumber = sim.dayNumber,
                timeLabel = sim.timeOfDay.label,
                title = "Personal Reflection",
                note = clean
            )
            val draftSim = sim.copy(
                hasSavedSession = true,
                calm = (sim.calm + 10).coerceIn(10, 100),
                pondReflectionsCount = sim.pondReflectionsCount + 1,
                journalEntries = (listOf(entry) + sim.journalEntries).take(20),
                lastBannerMessage = "Saved your reflection to your Dream Life Journal.",
                activeChatBubble = MotivationalBubble(
                    id = now,
                    speaker = "Reflection Saved",
                    tag = "Self-Discovery",
                    message = "Naming what matters to you is a quiet superpower. Your reflection is safely saved locally."
                )
            )
            val evaluated = LifeSimEngine.evaluateMilestones(draftSim)
            val allDone = evaluated.isNotEmpty() && evaluated.all { it.isCompleted }
            val triggerCompletion = allDone && !sim.hasShownCompletionForCurrentRun
            val finalSim = draftSim.copy(
                milestones = evaluated,
                completedLifestyles = if (allDone) sim.completedLifestyles + sim.lifestyle else sim.completedLifestyles,
                showScenarioCompleteReflection = triggerCompletion,
                hasShownCompletionForCurrentRun = sim.hasShownCompletionForCurrentRun || triggerCompletion
            )
            persistLifeSim(finalSim)

            state.copy(
                lifeSim = finalSim,
                activities = prependActivity(
                    state.activities,
                    title = "Wrote Dream Life Reflection",
                    detail = clean.take(48) + if (clean.length > 48) "…" else "",
                    category = "Life Sim"
                )
            )
        }
    }

    fun dismissLifeSimChatBubble() {
        _uiState.update { state ->
            state.copy(lifeSim = state.lifeSim.copy(activeChatBubble = null))
        }
    }

    fun dismissNewDaySummary() {
        _uiState.update { state ->
            val sim = state.lifeSim
            val allDone = sim.milestones.isNotEmpty() && sim.milestones.all { it.isCompleted }
            val triggerCompletion = allDone && !sim.hasShownCompletionForCurrentRun
            val finalSim = sim.copy(
                showNewDaySummary = false,
                showScenarioCompleteReflection = triggerCompletion,
                hasShownCompletionForCurrentRun = sim.hasShownCompletionForCurrentRun || triggerCompletion
            )
            persistLifeSim(finalSim)
            state.copy(lifeSim = finalSim)
        }
    }

    fun openScenarioReflection() {
        _uiState.update { state ->
            state.copy(lifeSim = state.lifeSim.copy(showScenarioCompleteReflection = true))
        }
    }

    fun dismissScenarioReflection() {
        _uiState.update { state ->
            state.copy(lifeSim = state.lifeSim.copy(showScenarioCompleteReflection = false))
        }
    }

    // --- Community Forum Actions ---

    fun selectForumCategory(category: ForumCategory) {
        _uiState.update { it.copy(selectedForumCategory = category) }
    }

    fun togglePostExpanded(postId: String) {
        _uiState.update { state ->
            val next = if (postId in state.expandedPostIds) {
                state.expandedPostIds - postId
            } else {
                state.expandedPostIds + postId
            }
            state.copy(expandedPostIds = next)
        }
    }

    fun toggleWarmth(postId: String) {
        _uiState.update { state ->
            state.copy(
                forumPosts = state.forumPosts.map { post ->
                    if (post.id == postId) {
                        val togglingOn = !post.hasSentWarmth
                        post.copy(
                            hasSentWarmth = togglingOn,
                            warmthCount = post.warmthCount + if (togglingOn) 1 else -1
                        )
                    } else post
                }
            )
        }
    }

    fun toggleRelate(postId: String) {
        _uiState.update { state ->
            state.copy(
                forumPosts = state.forumPosts.map { post ->
                    if (post.id == postId) {
                        val togglingOn = !post.hasRelated
                        post.copy(
                            hasRelated = togglingOn,
                            relateCount = post.relateCount + if (togglingOn) 1 else -1
                        )
                    } else post
                }
            )
        }
    }

    fun addForumReply(postId: String, replyText: String) {
        val clean = replyText.trim()
        if (clean.isEmpty()) return
        _uiState.update { state ->
            val newReply = ForumReply(
                id = "reply_${System.currentTimeMillis()}",
                authorAlias = "${state.userAlias} (You)",
                timestamp = "Just now • Local",
                content = clean
            )
            state.copy(
                forumPosts = state.forumPosts.map { post ->
                    if (post.id == postId) post.copy(replies = post.replies + newReply) else post
                },
                notesSharedCount = state.notesSharedCount + 1,
                activities = prependActivity(
                    state.activities,
                    title = "Replied in Community Forum",
                    detail = clean.take(48) + if (clean.length > 48) "…" else "",
                    category = "Forum"
                )
            )
        }
    }

    fun createDemoPost(title: String, body: String, category: ForumCategory, moodTag: String) {
        val cleanTitle = title.trim()
        val cleanBody = body.trim()
        if (cleanTitle.isEmpty() || cleanBody.isEmpty()) return
        _uiState.update { state ->
            val newPost = ForumPost(
                id = "post_${System.currentTimeMillis()}",
                authorAlias = "${state.userAlias} (You)",
                timestamp = "Just now • Local Demo",
                category = if (category == ForumCategory.ALL) ForumCategory.SMALL_WINS else category,
                moodTag = moodTag.ifBlank { "Reflective" },
                title = cleanTitle,
                body = cleanBody,
                warmthCount = 1,
                relateCount = 0,
                hasSentWarmth = true,
                replies = emptyList()
            )
            state.copy(
                forumPosts = listOf(newPost) + state.forumPosts,
                expandedPostIds = state.expandedPostIds + newPost.id,
                notesSharedCount = state.notesSharedCount + 1,
                activities = prependActivity(
                    state.activities,
                    title = "Shared Anonymous Note",
                    detail = cleanTitle,
                    category = "Forum"
                )
            )
        }
    }

    fun reportPost(postId: String) {
        _uiState.update { state ->
            state.copy(
                forumPosts = state.forumPosts.map { post ->
                    if (post.id == postId) post.copy(isReported = true) else post
                },
                activities = prependActivity(
                    state.activities,
                    title = "Flagged Demo Post for Safety",
                    detail = "Hidden locally in prototype preview",
                    category = "Safety"
                )
            )
        }
    }

    // --- Profile & Settings Actions ---

    fun cycleAnonymousAlias() {
        _uiState.update { state ->
            val list = DemoRepository.anonymousAliases
            val nextIdx = (list.indexOf(state.userAlias) + 1) % list.size
            val nextAlias = list[nextIdx]
            state.copy(
                userAlias = nextAlias,
                activities = prependActivity(
                    state.activities,
                    title = "Switched Anonymous Alias",
                    detail = "Now browsing locally as $nextAlias",
                    category = "Profile"
                )
            )
        }
    }

    fun updateSettings(transform: (DemoSettings) -> DemoSettings) {
        _uiState.update { it.copy(settings = transform(it.settings)) }
    }

    fun resetDemoData() {
        stopAudioTicker()
        lifeSimStorage.clearSavedProgress()
        _uiState.value = NeuroTalksUiState(guidelinesAccepted = _uiState.value.guidelinesAccepted)
    }

    private fun prependActivity(
        existing: List<ActivityEntry>,
        title: String,
        detail: String,
        category: String
    ): List<ActivityEntry> {
        val entry = ActivityEntry(
            id = "act_${System.currentTimeMillis()}_${existing.size}",
            title = title,
            detail = detail,
            timestamp = "Just now",
            category = category
        )
        return (listOf(entry) + existing).take(12)
    }

    override fun onCleared() {
        super.onCleared()
        stopAudioTicker()
    }
}
