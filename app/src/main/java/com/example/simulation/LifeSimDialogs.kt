package com.example.simulation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ClayWarm
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.SageSoft

@Composable
fun InteractableActivityDialog(
    interactable: WorldInteractable,
    lifestyle: DreamLifestyle,
    currentEnergy: Int,
    unlockedActivityIds: Set<String>,
    onPerformActivity: (WorldActivityOption) -> Unit,
    onSaveCustomReflection: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = remember(interactable.type, lifestyle, unlockedActivityIds) {
        LifeSimEngine.getActivitiesForInteractable(interactable.type, lifestyle, unlockedActivityIds)
    }
    var reflectionDraft by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = interactable.shortPrompt.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = interactable.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (currentEnergy < 20 && interactable.type != InteractableType.TEA_PATIO && interactable.type != InteractableType.COZY_BED) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = "Self-Care Reminder: Your energy is low ($currentEnergy%). You can still explore, or pause at the Tea Patio / Cozy Cottage anytime to recharge!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                options.forEach { option ->
                    val locked = option.isUnlockable
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(enabled = !locked) {
                                onPerformActivity(option)
                                onDismiss()
                            }
                            .testTag("sim_activity_${option.id}"),
                        shape = RoundedCornerShape(18.dp),
                        color = when {
                            locked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                            option.id == LifeSimEngine.MASTER_ACTIVITY_UNLOCK_ID -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
                            else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.58f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (locked) MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (locked) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Locked milestone activity",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (option.id == LifeSimEngine.MASTER_ACTIVITY_UNLOCK_ID) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = "Unlocked milestone activity",
                                    tint = ClayWarm,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (locked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = option.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (interactable.type == InteractableType.POND_BENCH) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "Write a personal reflection for your Dream Journal (saved locally):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = reflectionDraft,
                        onValueChange = { reflectionDraft = it },
                        placeholder = { Text("One real-life goal or gentle hope…", style = MaterialTheme.typography.bodySmall) },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sim_pond_journal_input")
                    )
                    Button(
                        onClick = {
                            if (reflectionDraft.isNotBlank()) {
                                onSaveCustomReflection(reflectionDraft)
                                onDismiss()
                            }
                        },
                        enabled = reflectionDraft.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sim_pond_journal_save")
                    ) {
                        Text("Save Reflection to Journal")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Step Back")
            }
        }
    )
}

@Composable
fun NpcConversationDialog(
    npc: WorldNpc,
    lifestyle: DreamLifestyle,
    talkCount: Int,
    onWarmReply: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tips = npc.lifestyleTips[lifestyle].orEmpty()
    val currentTip = if (tips.isNotEmpty()) tips[talkCount % tips.size] else npc.greeting

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = npc.outfitColor,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = npc.name.take(1),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFFAF6EE),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column {
                    Text(npc.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "${npc.role} • Encouraging neighbor",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "\"${npc.greeting}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentTip,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = ClayWarm,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Warm conversations shared: $talkCount",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onWarmReply(currentTip)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("npc_chat_confirm_${npc.id}")
            ) {
                Text("Share Warm Moment")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(14.dp)) {
                Text("Wave Goodbye")
            }
        }
    )
}

@Composable
fun GoalsAndJournalDialog(
    state: LifeSimState,
    onSwitchLifestyle: () -> Unit,
    onViewReflection: () -> Unit,
    onDismiss: () -> Unit
) {
    val completedCount = state.milestones.count { it.isCompleted }
    val totalCount = state.milestones.size.coerceAtLeast(1)
    val allGoalsDone = completedCount >= totalCount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Personal Growth & Journal", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Motivate, not compete • Saved locally on device",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(shape = RoundedCornerShape(16.dp), color = ForestDeep) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = state.lifestyle.title,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFFFAF6EE)
                            )
                            Text(
                                text = "$completedCount / $totalCount",
                                style = MaterialTheme.typography.labelLarge,
                                color = SageSoft,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        LinearProgressIndicator(
                            progress = { completedCount.toFloat() / totalCount.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
                            color = SageSoft,
                            trackColor = Color.White.copy(alpha = 0.2f),
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "\"${state.lifestyle.mantra}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = SageSoft
                        )
                    }
                }

                if (allGoalsDone) {
                    Button(
                        onClick = {
                            onDismiss()
                            onViewReflection()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ClayWarm),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goals_view_reflection_button")
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Read Scenario Completion Reflection")
                    }
                }

                state.milestones.forEach { milestone ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (milestone.isCompleted) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (milestone.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (milestone.isCompleted) ForestPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(milestone.title, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    text = milestone.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${milestone.currentCount}/${milestone.targetCount}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (state.completedLifestyles.isNotEmpty()) {
                    Text(
                        text = "Scenarios Completed: ${state.completedLifestyles.joinToString(", ") { it.title }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Dream Life Journal (${state.journalEntries.size})",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                if (state.journalEntries.isEmpty()) {
                    Text(
                        text = "Explore locations, chat with neighbors, or sit at the Stillwater Bench to gather personal reflections here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    state.journalEntries.take(8).forEach { entry ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Day ${entry.dayNumber} • ${entry.timeLabel} • ${entry.title}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = entry.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Back to World")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onSwitchLifestyle()
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("goals_switch_lifestyle_button")
            ) {
                Text("Try Another Life")
            }
        }
    )
}

@Composable
fun NewDaySummaryDialog(
    dayNumber: Int,
    characterName: String,
    highlight: String,
    lifestyleMantra: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.WbSunny,
                contentDescription = null,
                tint = Color(0xFFD69F4C),
                modifier = Modifier.size(34.dp)
            )
        },
        title = { Text("Good Morning, Day $dayNumber!", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "$characterName wakes up refreshed with 100% energy as golden sunlight warms Willowbend.",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (highlight.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = highlight,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                Text(
                    text = "Today's gentle reminder: \"$lifestyleMantra\"",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("sim_new_day_continue")
            ) {
                Text("Step Into Day $dayNumber")
            }
        }
    )
}

@Composable
fun ScenarioCompleteReflectionDialog(
    state: LifeSimState,
    onContinueExploring: () -> Unit,
    onExploreAnotherLife: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onContinueExploring,
        icon = {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = ClayWarm,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Personal Milestone Reached!",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${state.appearance.name.ifBlank { "You" }} completed all personal goals for ${state.lifestyle.title}!",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ForestDeep
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "HOW SMALL STEPS GROW REAL-LIFE ASPIRATIONS",
                            style = MaterialTheme.typography.labelSmall,
                            color = SageSoft,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.lifestyle.realLifeBridge,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFAF6EE)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Motivate, Not Compete",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "There are no scores or leaderboards here—only proof that small, kind actions add up. Pick one tiny habit from this session to carry into your week.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onExploreAnotherLife,
                colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("sim_reflection_new_life_button")
            ) {
                Icon(Icons.Rounded.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Try Another Ideal Life")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onContinueExploring,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("sim_reflection_continue_button")
            ) {
                Text("Keep Exploring")
            }
        }
    )
}
