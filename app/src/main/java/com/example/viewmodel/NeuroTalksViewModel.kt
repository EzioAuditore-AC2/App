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
import com.example.podcast.Media3PodcastPlayer
import com.example.podcast.PodcastCatalog
import com.example.podcast.PodcastCategory
import com.example.podcast.PodcastFeedState
import com.example.podcast.PodcastRssFetcher
import com.example.podcast.RealPodcastEpisode
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
    // Real Podcast & Media3 ExoPlayer state
    val podcastFeed: PodcastFeedState = PodcastFeedState(),
    val isPlayingAudio: Boolean = false,
    val bookmarkedEpisodeIds: Set<String> = setOf(PodcastCatalog.curatedEpisodes.first().id),
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
)

class NeuroTalksViewModel(application: Application) : AndroidViewModel(application) {

    private val lifeSimStorage = LifeSimStorage(application.applicationContext)

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<NeuroTalksUiState> = _uiState.asStateFlow()

    private var media3PollJob: Job? = null
    private var feedRefreshJob: Job? = null

    private val media3Player: Media3PodcastPlayer by lazy {
        Media3PodcastPlayer(application.applicationContext) { isPlaying, isBuffering, posMs, durMs, error ->
            _uiState.update { state ->
                val currentPlayer = state.podcastFeed.player
                state.copy(
                    isPlayingAudio = isPlaying,
                    podcastFeed = state.podcastFeed.copy(
                        player = currentPlayer.copy(
                            isPlaying = isPlaying,
                            isBuffering = isBuffering,
                            positionMs = posMs,
                            durationMs = if (durMs > 0L) durMs else currentPlayer.durationMs,
                            errorMessage = error
                        )
                    )
                )
            }
            if (isPlaying || isBuffering) {
                startMedia3ProgressPolling()
            } else {
                stopMedia3ProgressPolling()
            }
        }
    }

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

    // --- Real Podcast & Media3 Actions ---

    fun refreshPublicPodcastFeeds(force: Boolean = false) {
        val currentFeed = _uiState.value.podcastFeed
        if (!force && (currentFeed.isLoadingFeeds || currentFeed.liveFeedEpisodes.isNotEmpty())) return

        feedRefreshJob?.cancel()
        feedRefreshJob = viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    podcastFeed = state.podcastFeed.copy(
                        isLoadingFeeds = true,
                        feedStatusNote = "Loading public podcast streams…"
                    )
                )
            }
            val liveEpisodes = runCatching {
                PodcastRssFetcher.fetchPublicStreamableEpisodes(maxPerFeed = 4)
            }.getOrDefault(emptyList())

            _uiState.update { state ->
                val status = if (liveEpisodes.isNotEmpty()) {
                    "${liveEpisodes.size} direct-stream episodes ready from NPR Life Kit & TED Talks Daily."
                } else {
                    "Offline or public feeds unavailable—verified Spotify, YouTube & official website links remain ready."
                }
                state.copy(
                    podcastFeed = state.podcastFeed.copy(
                        liveFeedEpisodes = liveEpisodes,
                        isLoadingFeeds = false,
                        feedStatusNote = status
                    )
                )
            }
        }
    }

    fun selectPodcastCategory(category: PodcastCategory) {
        _uiState.update { state ->
            state.copy(
                podcastFeed = state.podcastFeed.copy(selectedCategory = category)
            )
        }
    }

    fun toggleStreamableOnlyFilter() {
        _uiState.update { state ->
            state.copy(
                podcastFeed = state.podcastFeed.copy(
                    showStreamableOnly = !state.podcastFeed.showStreamableOnly
                )
            )
        }
    }

    fun selectPodcastEpisode(episodeId: String) {
        _uiState.update { state ->
            state.copy(
                podcastFeed = state.podcastFeed.copy(selectedEpisodeId = episodeId)
            )
        }
    }

    /**
     * Plays a podcast directly inside the app via Android Media3 ExoPlayer.
     * Strictly gated to episodes that provide a legitimate direct audio stream URL
     * from official public RSS feeds. External links (Spotify/YouTube/web) are never streamed here.
     */
    fun playStreamableEpisode(episode: RealPodcastEpisode) {
        val streamUrl = episode.directAudioStreamUrl
        if (streamUrl.isNullOrBlank()) return

        val speed = _uiState.value.podcastFeed.player.playbackSpeed
        _uiState.update { state ->
            state.copy(
                podcastFeed = state.podcastFeed.copy(
                    selectedEpisodeId = episode.id,
                    player = state.podcastFeed.player.copy(
                        activeStreamEpisodeId = episode.id,
                        activeStreamTitle = episode.title,
                        activeStreamCreator = "${episode.showTitle} • ${episode.creator}",
                        activeStreamArtworkUrl = episode.artworkUrl,
                        isBuffering = true,
                        positionMs = if (state.podcastFeed.player.activeStreamEpisodeId == episode.id) {
                            state.podcastFeed.player.positionMs
                        } else 0L,
                        durationMs = episode.durationMinutes * 60_000L,
                        errorMessage = null
                    )
                ),
                activities = prependActivity(
                    state.activities,
                    title = "Streaming Podcast In-App",
                    detail = episode.title,
                    category = "Podcast"
                )
            )
        }
        media3Player.playDirectStream(streamUrl, speed)
    }

    fun toggleMedia3PlayPause() {
        if (_uiState.value.podcastFeed.player.activeStreamEpisodeId == null) return
        media3Player.togglePlayPause()
    }

    fun seekMedia3To(positionMs: Long) {
        if (_uiState.value.podcastFeed.player.activeStreamEpisodeId == null) return
        media3Player.seekTo(positionMs)
    }

    fun skipMedia3By(deltaMs: Long) {
        if (_uiState.value.podcastFeed.player.activeStreamEpisodeId == null) return
        media3Player.skipBy(deltaMs)
    }

    fun cycleMedia3Speed() {
        val currentSpeed = _uiState.value.podcastFeed.player.playbackSpeed
        val next = when (currentSpeed) {
            1.0f -> 1.25f
            1.25f -> 1.5f
            else -> 1.0f
        }
        _uiState.update { state ->
            state.copy(
                podcastFeed = state.podcastFeed.copy(
                    player = state.podcastFeed.player.copy(playbackSpeed = next)
                )
            )
        }
        if (_uiState.value.podcastFeed.player.activeStreamEpisodeId != null) {
            media3Player.setSpeed(next)
        }
    }

    fun recordExternalPodcastOpen(episode: RealPodcastEpisode, platformName: String) {
        _uiState.update { state ->
            state.copy(
                podcastFeed = state.podcastFeed.copy(selectedEpisodeId = episode.id),
                activities = prependActivity(
                    state.activities,
                    title = "Opened on $platformName",
                    detail = "${episode.showTitle}: ${episode.title}",
                    category = "Podcast"
                )
            )
        }
    }

    fun toggleRealPodcastBookmark(episodeId: String) {
        val episode = _uiState.value.podcastFeed.allEpisodes.firstOrNull { it.id == episodeId } ?: return
        _uiState.update { state ->
            val exists = episodeId in state.podcastFeed.bookmarkedIds
            val updated = if (exists) {
                state.podcastFeed.bookmarkedIds - episodeId
            } else {
                state.podcastFeed.bookmarkedIds + episodeId
            }
            state.copy(
                bookmarkedEpisodeIds = updated,
                podcastFeed = state.podcastFeed.copy(bookmarkedIds = updated),
                activities = if (!exists) {
                    prependActivity(
                        state.activities,
                        title = "Saved Podcast",
                        detail = episode.title,
                        category = "Podcast"
                    )
                } else state.activities
            )
        }
    }

    private fun startMedia3ProgressPolling() {
        if (media3PollJob?.isActive == true) return
        media3PollJob = viewModelScope.launch {
            while (_uiState.value.podcastFeed.player.isPlaying || _uiState.value.podcastFeed.player.isBuffering) {
                delay(500L)
                media3Player.pollProgress()
            }
        }
    }

    private fun stopMedia3ProgressPolling() {
        media3PollJob?.cancel()
        media3PollJob = null
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
        stopMedia3ProgressPolling()
        if (_uiState.value.podcastFeed.player.activeStreamEpisodeId != null) {
            media3Player.stop()
        }
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
        stopMedia3ProgressPolling()
        feedRefreshJob?.cancel()
        if (_uiState.value.podcastFeed.player.activeStreamEpisodeId != null) {
            media3Player.release()
        }
    }
}
