package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.simulation.AccessoryStyle
import com.example.simulation.ClothingStyle
import com.example.simulation.DreamLifestyle
import com.example.simulation.HairStyle
import com.example.simulation.InteractableType
import com.example.simulation.LifeSimEngine
import com.example.simulation.SimPhase
import com.example.viewmodel.NeuroTalksViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LifeSimVerificationTest {

    private lateinit var app: Application

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        // Ensure clean local storage before each test
        NeuroTalksViewModel(app).resetDemoData()
    }

    @Test
    fun `character movement and obstacle collision detection work smoothly`() {
        val vm = NeuroTalksViewModel(app)
        vm.enterLifeSimWorld()

        val startX = vm.uiState.value.lifeSim.playerX
        val startY = vm.uiState.value.lifeSim.playerY

        // Move left in open clearing
        vm.moveLifeSimByDirection(-1f, 0f)
        assertTrue(vm.uiState.value.lifeSim.playerX < startX)
        assertTrue(vm.uiState.value.lifeSim.walkPhase > 0f)

        // Verify solid pond collision blocks walking into water center (0.53, 0.86)
        assertTrue(LifeSimEngine.collidesWithAnyObstacle(0.53f, 0.86f))
        val (resolvedX, resolvedY) = LifeSimEngine.resolveMovementWithCollision(
            currentX = 0.53f,
            currentY = 0.74f,
            desiredX = 0.53f,
            desiredY = 0.86f
        )
        assertFalse(LifeSimEngine.collidesWithAnyObstacle(resolvedX, resolvedY))
        assertEquals(0.74f, resolvedY, 0.001f)

        // Tap-to-walk navigates cleanly and clears target upon arrival
        vm.setLifeSimWalkTarget(0.45f, 0.50f)
        repeat(40) { vm.tickLifeSimMovement() }
        assertEquals(null, vm.uiState.value.lifeSim.targetX)
        assertEquals(0.45f, vm.uiState.value.lifeSim.playerX, 0.02f)
        assertEquals(0.50f, vm.uiState.value.lifeSim.playerY, 0.02f)
        assertTrue(startY > 0f)
    }

    @Test
    fun `lifestyle adapts world locations and unlocks milestone activity with motivational chat bubbles`() {
        val vm = NeuroTalksViewModel(app)
        vm.selectLifeSimLifestyle(DreamLifestyle.ACADEMIC_SUCCESS)
        vm.enterLifeSimWorld()

        val locations = LifeSimEngine.getWorldInteractables(DreamLifestyle.ACADEMIC_SUCCESS)
        val studyDesk = locations.first { it.type == InteractableType.STUDIO_DESK }
        assertEquals("Study Desk", studyDesk.mapLabel)

        val initialActivities = LifeSimEngine.getActivitiesForInteractable(
            InteractableType.STUDIO_DESK,
            DreamLifestyle.ACADEMIC_SUCCESS,
            emptySet()
        )
        assertTrue(initialActivities.any { it.id == LifeSimEngine.MASTER_ACTIVITY_UNLOCK_ID && it.isUnlockable })

        // Perform 2 study activities -> should unlock master activity & display motivational chat bubble
        val studyOption = initialActivities.first { !it.isUnlockable }
        vm.performLifeSimActivity(studyDesk, studyOption)
        assertNotNull(vm.uiState.value.lifeSim.activeChatBubble)
        vm.performLifeSimActivity(studyDesk, studyOption)

        val sim = vm.uiState.value.lifeSim
        assertTrue(LifeSimEngine.MASTER_ACTIVITY_UNLOCK_ID in sim.unlockedActivityIds)
        assertEquals("Milestone Unlocked!", sim.activeChatBubble?.speaker)
    }

    @Test
    fun `npc interaction, scenario completion reflection, and local SharedPreferences persistence succeed`() {
        val vm = NeuroTalksViewModel(app)
        vm.setLifeSimPhase(SimPhase.CHOOSE_LIFESTYLE)
        vm.selectLifeSimLifestyle(DreamLifestyle.PEACEFUL_LIVING)
        vm.updateLifeSimAppearance {
            it.copy(
                name = "Kira",
                hairStyle = HairStyle.COZY_BUN,
                clothingStyle = ClothingStyle.LINEN_TUNIC,
                accessory = AccessoryStyle.LEAF_SPROUT
            )
        }
        vm.enterLifeSimWorld()

        val interactables = LifeSimEngine.getWorldInteractables(DreamLifestyle.PEACEFUL_LIVING)
        val garden = interactables.first { it.type == InteractableType.GARDEN_BED }
        val pond = interactables.first { it.type == InteractableType.POND_BENCH }
        val tea = interactables.first { it.type == InteractableType.TEA_PATIO }
        val bed = interactables.first { it.type == InteractableType.COZY_BED }

        // Complete all Peaceful Living milestones
        val waterOpt = LifeSimEngine.getActivitiesForInteractable(InteractableType.GARDEN_BED, DreamLifestyle.PEACEFUL_LIVING, emptySet()).first()
        vm.performLifeSimActivity(garden, waterOpt)
        vm.performLifeSimActivity(garden, waterOpt)

        vm.addLifeSimJournalReflection("Slow mornings make me feel grounded.")
        vm.addLifeSimJournalReflection("Taking real breaks helps my creativity.")

        val teaOpt = LifeSimEngine.getActivitiesForInteractable(InteractableType.TEA_PATIO, DreamLifestyle.PEACEFUL_LIVING, emptySet()).first()
        vm.performLifeSimActivity(tea, teaOpt)

        val bram = LifeSimEngine.worldNpcs.first { it.id == "npc_bram" }
        vm.talkToLifeSimNpc(bram, "Gentle care compounds beautifully.")

        val sleepOpt = LifeSimEngine.getActivitiesForInteractable(InteractableType.COZY_BED, DreamLifestyle.PEACEFUL_LIVING, emptySet()).first()
        vm.performLifeSimActivity(bed, sleepOpt)
        assertTrue(vm.uiState.value.lifeSim.showNewDaySummary)

        // Dismiss morning summary -> should trigger Scenario Completion Reflection!
        vm.dismissNewDaySummary()
        assertTrue(vm.uiState.value.lifeSim.showScenarioCompleteReflection)
        assertTrue(DreamLifestyle.PEACEFUL_LIVING in vm.uiState.value.lifeSim.completedLifestyles)

        // Verify local persistence across fresh ViewModel recreation
        val restoredVm = NeuroTalksViewModel(app)
        val restored = restoredVm.uiState.value.lifeSim
        assertTrue(restored.hasSavedSession)
        assertEquals("Kira", restored.appearance.name)
        assertEquals(HairStyle.COZY_BUN, restored.appearance.hairStyle)
        assertEquals(ClothingStyle.LINEN_TUNIC, restored.appearance.clothingStyle)
        assertEquals(AccessoryStyle.LEAF_SPROUT, restored.appearance.accessory)
        assertEquals(DreamLifestyle.PEACEFUL_LIVING, restored.lifestyle)
        assertEquals(2, restored.dayNumber)
        assertEquals(2, restored.gardenBloomStage)
        assertTrue(DreamLifestyle.PEACEFUL_LIVING in restored.completedLifestyles)
        assertTrue(restored.journalEntries.any { it.note.contains("Slow mornings") })
        assertNotNull(pond)
    }
}
