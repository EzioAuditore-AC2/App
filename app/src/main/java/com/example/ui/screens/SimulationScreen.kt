package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.simulation.AccessoryStyle
import com.example.simulation.CharacterAppearance
import com.example.simulation.CharacterAvatarPreview
import com.example.simulation.ClothingStyle
import com.example.simulation.DreamLifestyle
import com.example.simulation.GoalsAndJournalDialog
import com.example.simulation.HairStyle
import com.example.simulation.InteractableActivityDialog
import com.example.simulation.LifeSimEngine
import com.example.simulation.LifeSimState
import com.example.simulation.LifeSimWorldCanvas
import com.example.simulation.MotivationalBubble
import com.example.simulation.NewDaySummaryDialog
import com.example.simulation.NpcConversationDialog
import com.example.simulation.ScenarioCompleteReflectionDialog
import com.example.simulation.SimPalette
import com.example.simulation.SimPhase
import com.example.simulation.WorldActivityOption
import com.example.simulation.WorldInteractable
import com.example.simulation.WorldNpc
import com.example.ui.components.PrototypeBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.ClayWarm
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.SageSoft
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun SimulationScreen(
    simState: LifeSimState,
    onSetPhase: (SimPhase) -> Unit,
    onContinueSavedWorld: () -> Unit,
    onSelectLifestyle: (DreamLifestyle) -> Unit,
    onSelectPresetCharacter: (CharacterAppearance) -> Unit,
    onUpdateAppearance: ((CharacterAppearance) -> CharacterAppearance) -> Unit,
    onEnterWorld: () -> Unit,
    onSetWalkTarget: (Float, Float) -> Unit,
    onMoveDirectionStep: (Float, Float) -> Unit,
    onTickMovement: () -> Unit,
    onPerformActivity: (WorldInteractable, WorldActivityOption) -> Unit,
    onTalkToNpc: (WorldNpc, String) -> Unit,
    onAddCustomReflection: (String) -> Unit,
    onDismissChatBubble: () -> Unit,
    onDismissNewDaySummary: () -> Unit,
    onOpenScenarioReflection: () -> Unit,
    onDismissScenarioReflection: () -> Unit
) {
    when (simState.phase) {
        SimPhase.INTRO -> GameIntroView(
            simState = simState,
            onStartExploring = { onSetPhase(SimPhase.CHOOSE_LIFESTYLE) },
            onContinueSaved = onContinueSavedWorld
        )

        SimPhase.CHOOSE_LIFESTYLE -> ChooseIdealLifeView(
            simState = simState,
            onSelectLifestyle = onSelectLifestyle,
            onBack = { onSetPhase(SimPhase.INTRO) },
            onNext = { onSetPhase(SimPhase.CUSTOMIZE_CHARACTER) }
        )

        SimPhase.CUSTOMIZE_CHARACTER -> CharacterCreatorView(
            simState = simState,
            onSelectPreset = onSelectPresetCharacter,
            onUpdateAppearance = onUpdateAppearance,
            onBack = { onSetPhase(SimPhase.CHOOSE_LIFESTYLE) },
            onEnterWorld = onEnterWorld
        )

        SimPhase.PLAYING_WORLD -> PlayableLifeSimWorldView(
            simState = simState,
            onOpenCustomizer = { onSetPhase(SimPhase.CUSTOMIZE_CHARACTER) },
            onSwitchLifestyle = { onSetPhase(SimPhase.CHOOSE_LIFESTYLE) },
            onSetWalkTarget = onSetWalkTarget,
            onMoveDirectionStep = onMoveDirectionStep,
            onTickMovement = onTickMovement,
            onPerformActivity = onPerformActivity,
            onTalkToNpc = onTalkToNpc,
            onAddCustomReflection = onAddCustomReflection,
            onDismissChatBubble = onDismissChatBubble,
            onDismissNewDaySummary = onDismissNewDaySummary,
            onOpenScenarioReflection = onOpenScenarioReflection,
            onDismissScenarioReflection = onDismissScenarioReflection
        )
    }
}

// --- STEP 1: WELCOMING INTRO + START EXPLORING ---

@Composable
private fun GameIntroView(
    simState: LifeSimState,
    onStartExploring: () -> Unit,
    onContinueSaved: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = ForestDeep,
                    shadowElevation = 4.dp
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_simulation_scene_1795175966761),
                                contentDescription = "Sunlit path through Willowbend meadow",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                ForestDeep.copy(alpha = 0.2f),
                                                ForestDeep.copy(alpha = 0.72f),
                                                ForestDeep
                                            )
                                        )
                                    )
                            )
                            PrototypeBadge(
                                text = "PLAYABLE 2D LIFE SIM",
                                inverted = true,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(16.dp)
                            )
                        }
                        Column(
                            modifier = Modifier.padding(start = 22.dp, end = 22.dp, bottom = 22.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Explore Your Ideal Life",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color(0xFFFAF6EE)
                            )
                            Text(
                                text = "Step into a cozy playable 2D village as your own custom character. Try out fulfilling routines, chat with supportive neighbors, and discover small daily habits that inspire your real-life goals.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SageSoft
                            )
                        }
                    }
                }
            }

            // Step 7 Principle Highlight: Motivate, Not Compete
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = ForestPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Motivate, Not Compete",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "No rankings, no leaderboards, and no penalties. Your character and progress save locally on your device so you can explore freely at your own pace.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.88f)
                            )
                        }
                    }
                }
            }

            // Continue Saved Progress Card if available
            if (simState.hasSavedSession) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CharacterAvatarPreview(
                                    appearance = simState.appearance,
                                    modifier = Modifier.size(60.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "SAVED LOCAL PROGRESS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${simState.appearance.name} • Day ${simState.dayNumber}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = simState.lifestyle.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Button(
                                onClick = onContinueSaved,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("sim_continue_saved_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ForestPrimary,
                                    contentColor = Color(0xFFFAF6EE)
                                )
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Continue Saved Journey", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Primary CTA: Start Exploring
            item {
                if (simState.hasSavedSession) {
                    OutlinedButton(
                        onClick = onStartExploring,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("sim_start_exploring_button"),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.5.dp, ForestPrimary)
                    ) {
                        Icon(Icons.Rounded.Explore, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Exploring New Ideal Life",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onStartExploring,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("sim_start_exploring_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForestPrimary,
                            contentColor = Color(0xFFFAF6EE)
                        )
                    ) {
                        Text(
                            text = "Start Exploring",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}

// --- STEP 2: CHOOSE YOUR IDEAL LIFE ---

@Composable
private fun ChooseIdealLifeView(
    simState: LifeSimState,
    onSelectLifestyle: (DreamLifestyle) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("sim_lifestyle_back")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back to intro")
                    }
                    PrototypeBadge(text = "STEP 1 OF 2 • IDEAL LIFE")
                }
            }

            item {
                SectionHeader(
                    title = "Choose Your Ideal Life",
                    subtitle = "Each lifestyle adapts Willowbend's activities, locations, NPC talks, and personal goals"
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DreamLifestyle.entries.forEach { lifestyle ->
                        val selected = simState.lifestyle == lifestyle
                        val completedBefore = lifestyle in simState.completedLifestyles
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(22.dp))
                                .clickable { onSelectLifestyle(lifestyle) }
                                .testTag("sim_lifestyle_${lifestyle.name.lowercase()}"),
                            shape = RoundedCornerShape(22.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = lifestyle.focusTag.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (completedBefore) {
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = ClayWarm.copy(alpha = 0.16f)
                                            ) {
                                                Text(
                                                    text = "Explored",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = ClayWarm,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        if (selected) {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = "Selected lifestyle",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = lifestyle.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = lifestyle.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    lifestyle.featuredActivities.forEach { tag ->
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                        ) {
                                            Text(
                                                text = tag,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("sim_next_to_character_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestPrimary,
                        contentColor = Color(0xFFFAF6EE)
                    )
                ) {
                    Text(
                        text = "Next: Customize Your Character",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

// --- STEP 3: CHOOSE OR CREATE YOUR CHARACTER ---

@Composable
private fun CharacterCreatorView(
    simState: LifeSimState,
    onSelectPreset: (CharacterAppearance) -> Unit,
    onUpdateAppearance: ((CharacterAppearance) -> CharacterAppearance) -> Unit,
    onBack: () -> Unit,
    onEnterWorld: () -> Unit
) {
    val appearance = simState.appearance

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("sim_character_back")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back to lifestyle")
                    }
                    PrototypeBadge(text = "STEP 2 OF 2 • YOUR AVATAR")
                }
            }

            // Live 2D Animated Character Preview Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = ForestDeep,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CharacterAvatarPreview(
                            appearance = appearance,
                            modifier = Modifier.size(106.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = appearance.name.ifBlank { "Your Character" },
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFFFAF6EE)
                            )
                            Text(
                                text = simState.lifestyle.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = SageSoft
                            )
                            Text(
                                text = "Choose a quick preset below or customize hairstyle, clothing, and accessories.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SageSoft.copy(alpha = 0.88f)
                            )
                        }
                    }
                }
            }

            // Quick Character Presets
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Quick Presets", style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SimPalette.presets.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    if (appearance.name == preset.appearance.name && appearance.hairStyle == preset.appearance.hairStyle) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                                    }
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { onSelectPreset(preset.appearance) }
                                    .testTag("sim_${preset.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CharacterAvatarPreview(
                                        appearance = preset.appearance,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Text(
                                        text = preset.label,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Full Appearance Customizer
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = appearance.name,
                            onValueChange = { value ->
                                onUpdateAppearance { it.copy(name = value.take(18)) }
                            },
                            label = { Text("Character Name") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sim_character_name_input")
                        )

                        ColorSwatchRow(
                            title = "Skin Tone",
                            colors = SimPalette.skinTones,
                            selectedIndex = appearance.skinToneIndex,
                            onSelect = { idx -> onUpdateAppearance { it.copy(skinToneIndex = idx) } },
                            tagPrefix = "skin"
                        )

                        ColorSwatchRow(
                            title = "Hair Color",
                            colors = SimPalette.hairColors,
                            selectedIndex = appearance.hairColorIndex,
                            onSelect = { idx -> onUpdateAppearance { it.copy(hairColorIndex = idx) } },
                            tagPrefix = "hair_color"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Hairstyle", style = MaterialTheme.typography.labelLarge)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HairStyle.entries.forEach { style ->
                                    FilterChip(
                                        selected = appearance.hairStyle == style,
                                        onClick = { onUpdateAppearance { it.copy(hairStyle = style) } },
                                        label = { Text(style.label) },
                                        shape = RoundedCornerShape(50),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier.testTag("sim_hair_${style.name.lowercase()}")
                                    )
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Clothing Style", style = MaterialTheme.typography.labelLarge)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ClothingStyle.entries.forEach { cloth ->
                                    FilterChip(
                                        selected = appearance.clothingStyle == cloth,
                                        onClick = { onUpdateAppearance { it.copy(clothingStyle = cloth) } },
                                        label = { Text(cloth.label) },
                                        shape = RoundedCornerShape(50),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier.testTag("sim_clothing_${cloth.name.lowercase()}")
                                    )
                                }
                            }
                        }

                        ColorSwatchRow(
                            title = "Clothing Color",
                            colors = SimPalette.outfitColors,
                            selectedIndex = appearance.outfitColorIndex,
                            onSelect = { idx -> onUpdateAppearance { it.copy(outfitColorIndex = idx) } },
                            tagPrefix = "outfit"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Accessory", style = MaterialTheme.typography.labelLarge)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AccessoryStyle.entries.forEach { acc ->
                                    FilterChip(
                                        selected = appearance.accessory == acc,
                                        onClick = { onUpdateAppearance { it.copy(accessory = acc) } },
                                        label = { Text(acc.label) },
                                        shape = RoundedCornerShape(50),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        modifier = Modifier.testTag("sim_accessory_${acc.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onEnterWorld,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("sim_enter_world_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestPrimary,
                        contentColor = Color(0xFFFAF6EE)
                    )
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter Willowbend World",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- STEP 4, 5, 6, 7: PLAYABLE 2D IDEAL LIFE WORLD ---

@Composable
private fun PlayableLifeSimWorldView(
    simState: LifeSimState,
    onOpenCustomizer: () -> Unit,
    onSwitchLifestyle: () -> Unit,
    onSetWalkTarget: (Float, Float) -> Unit,
    onMoveDirectionStep: (Float, Float) -> Unit,
    onTickMovement: () -> Unit,
    onPerformActivity: (WorldInteractable, WorldActivityOption) -> Unit,
    onTalkToNpc: (WorldNpc, String) -> Unit,
    onAddCustomReflection: (String) -> Unit,
    onDismissChatBubble: () -> Unit,
    onDismissNewDaySummary: () -> Unit,
    onOpenScenarioReflection: () -> Unit,
    onDismissScenarioReflection: () -> Unit
) {
    // Continuous press-and-hold directional state
    var heldDirection by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    LaunchedEffect(heldDirection) {
        val dir = heldDirection ?: return@LaunchedEffect
        while (true) {
            onMoveDirectionStep(dir.first, dir.second)
            delay(20L)
        }
    }

    // Smooth tap-to-walk ticker
    LaunchedEffect(simState.targetX, simState.targetY) {
        if (simState.targetX == null || simState.targetY == null) return@LaunchedEffect
        while (true) {
            onTickMovement()
            delay(16L)
        }
    }

    var activeInteractable by remember { mutableStateOf<WorldInteractable?>(null) }
    var activeNpc by remember { mutableStateOf<WorldNpc?>(null) }
    var showGoalsDialog by remember { mutableStateOf(false) }

    val worldInteractables = remember(simState.lifestyle) {
        LifeSimEngine.getWorldInteractables(simState.lifestyle)
    }
    val nearestInteractable = remember(simState.playerX, simState.playerY, simState.lifestyle) {
        LifeSimEngine.findNearestInteractable(simState.playerX, simState.playerY, simState.lifestyle)
    }
    val nearestNpc = remember(simState.playerX, simState.playerY) {
        LifeSimEngine.findNearestNpc(simState.playerX, simState.playerY)
    }
    val completedMilestones = simState.milestones.count { it.isCompleted }
    val totalMilestones = simState.milestones.size.coerceAtLeast(1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Compact Top Game HUD
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = ForestDeep
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Day ${simState.dayNumber} • ${simState.timeOfDay.label}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFFFAF6EE),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${simState.appearance.name.ifBlank { "Maya" }} • ${simState.lifestyle.title}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SageSoft,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = SageSoft.copy(alpha = 0.24f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { showGoalsDialog = true }
                                        .testTag("sim_goals_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Book,
                                            contentDescription = "Open Goals and Journal",
                                            tint = Color(0xFFFAF6EE),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "$completedMilestones/$totalMilestones",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFAF6EE),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.White.copy(alpha = 0.14f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .clickable { onOpenCustomizer() }
                                        .testTag("sim_edit_avatar_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Edit,
                                            contentDescription = "Customize character",
                                            tint = Color(0xFFFAF6EE),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Avatar",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFAF6EE)
                                        )
                                    }
                                }
                            }
                        }

                        // Non-competitive Personal Growth Meters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MiniStatPill(Icons.Rounded.Bolt, "Energy", simState.energy, Color(0xFFF3CE74), Modifier.weight(1f))
                            MiniStatPill(Icons.Rounded.Spa, "Calm", simState.calm, SageSoft, Modifier.weight(1f))
                            MiniStatPill(Icons.Rounded.Palette, "Growth", simState.creativity, Color(0xFFE7A98B), Modifier.weight(1f))
                            MiniStatPill(Icons.Rounded.Favorite, "Connect", simState.connection, Color(0xFFF6C2C2), Modifier.weight(1f))
                        }
                    }
                }
            }

            // Step 5: Encouraging Motivational Chat Bubble (prominent & readable right above the 2D world!)
            item {
                AnimatedVisibility(
                    visible = simState.activeChatBubble != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    simState.activeChatBubble?.let { bubble ->
                        MotivationalChatBubbleCard(
                            bubble = bubble,
                            onDismiss = onDismissChatBubble
                        )
                    }
                }
            }

            // Step 4: Playable 2D Top-Down World Viewport
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.06f),
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(2.dp, ForestPrimary.copy(alpha = 0.45f)),
                    shadowElevation = 4.dp
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val widthPx = constraints.maxWidth.toFloat()
                        val heightPx = constraints.maxHeight.toFloat()

                        LifeSimWorldCanvas(
                            state = simState,
                            nearestInteractableId = nearestInteractable?.id,
                            nearestNpcId = nearestNpc?.id,
                            isHoldingDpad = heldDirection != null,
                            onWorldTap = { nx, ny ->
                                val tappedNpc = LifeSimEngine.worldNpcs.firstOrNull {
                                    hypot(nx - it.homeX, ny - it.homeY) < 0.085f
                                }
                                val tappedObj = worldInteractables.firstOrNull {
                                    hypot(nx - it.x, ny - it.y) < 0.095f
                                }
                                if (tappedNpc != null && tappedNpc.id == nearestNpc?.id) {
                                    activeNpc = tappedNpc
                                } else if (tappedObj != null && tappedObj.id == nearestInteractable?.id) {
                                    activeInteractable = tappedObj
                                } else {
                                    onSetWalkTarget(nx, ny)
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("sim_2d_world_canvas")
                        )

                        // Lifestyle-adapted map landmark pills
                        val labelCoords = mapOf(
                            "studio_desk" to (0.21f to 0.07f),
                            "library_nook" to (0.50f to 0.05f),
                            "cozy_bed" to (0.80f to 0.07f),
                            "garden_bed" to (0.22f to 0.85f),
                            "pond_bench" to (0.53f to 0.68f),
                            "tea_patio" to (0.80f to 0.84f)
                        )
                        worldInteractables.forEach { spot ->
                            val (lx, ly) = labelCoords[spot.id] ?: (spot.x to spot.y)
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFF1B3B2B).copy(alpha = 0.82f),
                                modifier = Modifier.offset {
                                    IntOffset(
                                        x = (lx * widthPx - 38.dp.toPx()).roundToInt().coerceIn(8, (widthPx - 84.dp.toPx()).roundToInt()),
                                        y = (ly * heightPx).roundToInt().coerceIn(8, (heightPx - 28.dp.toPx()).roundToInt())
                                    )
                                }
                            ) {
                                Text(
                                    text = spot.mapLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFAF6EE),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Contextual Interaction Card + Continuous Hold-to-Move D-Pad
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when {
                                nearestNpc != null -> {
                                    Text(
                                        text = "NEARBY NEIGHBOR",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ClayWarm,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${nearestNpc.name} (${nearestNpc.role})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Button(
                                        onClick = { activeNpc = nearestNpc },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("sim_interact_npc_button")
                                    ) {
                                        Text("Talk to ${nearestNpc.name}", fontWeight = FontWeight.Bold)
                                    }
                                }

                                nearestInteractable != null -> {
                                    Text(
                                        text = "NEARBY LOCATION",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = nearestInteractable.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Button(
                                        onClick = { activeInteractable = nearestInteractable },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("sim_interact_object_button")
                                    ) {
                                        Icon(Icons.Rounded.TouchApp, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(nearestInteractable.shortPrompt, fontWeight = FontWeight.Bold)
                                    }
                                }

                                else -> {
                                    Text(
                                        text = "EXPLORE AT YOUR OWN PACE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = simState.lastBannerMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 3
                                    )
                                }
                            }
                        }

                        // Continuous Hold-or-Tap D-Pad
                        HoldableDPad(
                            onDirectionHeld = { dir -> heldDirection = dir },
                            onTapStep = { dx, dy -> onMoveDirectionStep(dx * 2.2f, dy * 2.2f) }
                        )
                    }
                }
            }

            // Quick Walk Waypoints + Switch Lifestyle Pill
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Walk To Location",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Switch Ideal Life",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSwitchLifestyle() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("sim_world_switch_life_link")
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        worldInteractables.forEach { spot ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { onSetWalkTarget(spot.x, spot.y) }
                                    .testTag("sim_quickwalk_${spot.id}")
                            ) {
                                Text(
                                    text = spot.shortPrompt,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    activeInteractable?.let { obj ->
        InteractableActivityDialog(
            interactable = obj,
            lifestyle = simState.lifestyle,
            currentEnergy = simState.energy,
            unlockedActivityIds = simState.unlockedActivityIds,
            onPerformActivity = { option -> onPerformActivity(obj, option) },
            onSaveCustomReflection = onAddCustomReflection,
            onDismiss = { activeInteractable = null }
        )
    }

    activeNpc?.let { npc ->
        NpcConversationDialog(
            npc = npc,
            lifestyle = simState.lifestyle,
            talkCount = simState.npcTalkCounts[npc.id] ?: 0,
            onWarmReply = { tip -> onTalkToNpc(npc, tip) },
            onDismiss = { activeNpc = null }
        )
    }

    if (showGoalsDialog) {
        GoalsAndJournalDialog(
            state = simState,
            onSwitchLifestyle = onSwitchLifestyle,
            onViewReflection = onOpenScenarioReflection,
            onDismiss = { showGoalsDialog = false }
        )
    }

    if (simState.showNewDaySummary) {
        NewDaySummaryDialog(
            dayNumber = simState.dayNumber,
            characterName = simState.appearance.name.ifBlank { "Maya" },
            highlight = simState.lastDayHighlight,
            lifestyleMantra = simState.lifestyle.mantra,
            onDismiss = onDismissNewDaySummary
        )
    }

    if (simState.showScenarioCompleteReflection) {
        ScenarioCompleteReflectionDialog(
            state = simState,
            onContinueExploring = onDismissScenarioReflection,
            onExploreAnotherLife = {
                onDismissScenarioReflection()
                onSwitchLifestyle()
            }
        )
    }
}

@Composable
private fun MotivationalChatBubbleCard(
    bubble: MotivationalBubble,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sim_motivational_chat_bubble"),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 6.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, ForestPrimary.copy(alpha = 0.55f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = ForestPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bubble.speaker,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = bubble.tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "\"${bubble.message}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Dismiss message",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun HoldableDPad(
    onDirectionHeld: (Pair<Float, Float>?) -> Unit,
    onTapStep: (Float, Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        HoldableDPadButton(Icons.Rounded.KeyboardArrowUp, "Move up", 0f, -1f, onDirectionHeld, onTapStep, "sim_dpad_up")
        Row(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
            HoldableDPadButton(Icons.Rounded.KeyboardArrowLeft, "Move left", -1f, 0f, onDirectionHeld, onTapStep, "sim_dpad_left")
            HoldableDPadButton(Icons.Rounded.KeyboardArrowRight, "Move right", 1f, 0f, onDirectionHeld, onTapStep, "sim_dpad_right")
        }
        HoldableDPadButton(Icons.Rounded.KeyboardArrowDown, "Move down", 0f, 1f, onDirectionHeld, onTapStep, "sim_dpad_down")
    }
}

@Composable
private fun HoldableDPadButton(
    icon: ImageVector,
    description: String,
    dx: Float,
    dy: Float,
    onDirectionHeld: (Pair<Float, Float>?) -> Unit,
    onTapStep: (Float, Float) -> Unit,
    tag: String
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .pointerInput(dx, dy) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    onTapStep(dx, dy)
                    onDirectionHeld(dx to dy)
                    waitForUpOrCancellation()
                    onDirectionHeld(null)
                }
            }
            .testTag(tag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = ForestDeep,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun MiniStatPill(
    icon: ImageVector,
    label: String,
    value: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.11f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accent,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$value",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFFAF6EE),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ColorSwatchRow(
    title: String,
    colors: List<Color>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    tagPrefix: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            colors.forEachIndexed { idx, color ->
                val selected = idx == selectedIndex
                Surface(
                    shape = CircleShape,
                    color = color,
                    border = BorderStroke(
                        width = if (selected) 3.dp else 1.5.dp,
                        color = if (selected) ForestPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .size(if (selected) 44.dp else 40.dp)
                        .clip(CircleShape)
                        .clickable { onSelect(idx) }
                        .testTag("sim_${tagPrefix}_$idx")
                ) {
                    if (selected) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Selected $title",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
