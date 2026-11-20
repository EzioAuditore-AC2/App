package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.DemoRepository
import com.example.data.GuidelineItem
import com.example.ui.components.PrototypeBadge
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.SageSoft

@Composable
fun GuidelinesScreen(
    guidelinesAccepted: Boolean,
    onAcceptedChange: (Boolean) -> Unit,
    onContinueToHome: () -> Unit,
    onOpenEmergency: () -> Unit,
    onBack: () -> Unit
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
                .widthIn(max = 680.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("guidelines_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                    PrototypeBadge(text = "SAFE SPACE CHARTER")
                }
            }

            // Header Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = ForestDeep
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Community Guidelines",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color(0xFFFAF6EE)
                        )
                        Text(
                            text = "Before stepping into your space, here are the shared promises that keep NeuroTalks warm, respectful, and emotionally safe.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SageSoft
                        )
                    }
                }
            }

            // 6 Core Guidelines Cards
            items(DemoRepository.guidelines, key = { it.id }) { item ->
                GuidelineCard(
                    item = item,
                    onEmergencyClick = if (item.id == "emergency") onOpenEmergency else null
                )
            }

            // Interactive Agreement Checkbox Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onAcceptedChange(!guidelinesAccepted) }
                        .testTag("guidelines_agree_card"),
                    shape = RoundedCornerShape(20.dp),
                    color = if (guidelinesAccepted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    border = BorderStroke(
                        1.5.dp,
                        if (guidelinesAccepted) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Checkbox(
                            checked = guidelinesAccepted,
                            onCheckedChange = onAcceptedChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = ForestPrimary,
                                checkmarkColor = Color(0xFFFAF6EE)
                            ),
                            modifier = Modifier.testTag("guidelines_agree_checkbox")
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "I understand these community values",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "I also understand this is a local demo prototype and not clinical counseling.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (guidelinesAccepted) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Acknowledged",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Bottom Actions
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (!guidelinesAccepted) onAcceptedChange(true)
                            onContinueToHome()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("guidelines_continue_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForestPrimary,
                            contentColor = Color(0xFFFAF6EE)
                        )
                    ) {
                        Text(
                            text = if (guidelinesAccepted) "Enter NeuroTalks Home" else "Agree & Enter Home",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenEmergency,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("guidelines_emergency_button"),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.55f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.HealthAndSafety,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Emergency & Crisis Support")
                    }
                }
            }
        }
    }
}

@Composable
private fun GuidelineCard(
    item: GuidelineItem,
    onEmergencyClick: (() -> Unit)?
) {
    val isEmergency = item.id == "emergency"
    val containerColor = if (isEmergency) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val iconTint = if (isEmergency) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = containerColor,
        border = BorderStroke(
            1.dp,
            if (isEmergency) MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isEmergency) MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = guidelineIcon(item.iconKey),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
            )
            if (onEmergencyClick != null) {
                OutlinedButton(
                    onClick = onEmergencyClick,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Open Crisis Resources", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

private fun guidelineIcon(key: String): ImageVector = when (key) {
    "favorite" -> Icons.Rounded.Favorite
    "shield" -> Icons.Rounded.Shield
    "handshake" -> Icons.Rounded.Handshake
    "lock" -> Icons.Rounded.Lock
    "flag" -> Icons.Rounded.Flag
    "emergency" -> Icons.Rounded.HealthAndSafety
    else -> Icons.Rounded.Shield
}
