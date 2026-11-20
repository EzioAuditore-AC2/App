package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.DemoRepository
import com.example.data.HotlineResource
import com.example.ui.components.PrototypeBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.SoftDisclaimerCard
import com.example.ui.theme.CrisisCrimson
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.SageSoft
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EmergencyScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var breathingActive by remember { mutableStateOf(false) }
    var breathPhaseIndex by remember { mutableIntStateOf(0) }
    var breathCountdown by remember { mutableIntStateOf(4) }

    val phases = listOf("Breathe In", "Hold Gently", "Breathe Out", "Rest Softly")

    LaunchedEffect(breathingActive) {
        if (!breathingActive) {
            breathPhaseIndex = 0
            breathCountdown = 4
            return@LaunchedEffect
        }
        while (breathingActive) {
            for (sec in 4 downTo 1) {
                breathCountdown = sec
                delay(1000L)
            }
            breathPhaseIndex = (breathPhaseIndex + 1) % phases.size
        }
    }

    val orbScale by animateFloatAsState(
        targetValue = when {
            !breathingActive -> 0.85f
            breathPhaseIndex == 0 || breathPhaseIndex == 1 -> 1.15f
            else -> 0.78f
        },
        animationSpec = tween(durationMillis = 1200),
        label = "breathing_orb_scale"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Bar
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("emergency_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                        PrototypeBadge(text = "CRISIS & GROUNDING")
                    }
                }

                // Calm Reassuring Hero
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                        color = CrisisCrimson
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.HealthAndSafety,
                                    contentDescription = null,
                                    tint = Color(0xFFFAF6EE),
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "Emergency Support",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color(0xFFFAF6EE)
                                )
                            }
                            Text(
                                text = "You do not have to carry heavy moments by yourself. Real, trained human support is available 24/7.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFAF6EE).copy(alpha = 0.92f)
                            )
                        }
                    }
                }

                // Interactive 4-4-4-4 Grounding Breathing Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = ForestDeep
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Air,
                                    contentDescription = null,
                                    tint = SageSoft,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "60-Second Box Breathing Grounding",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFFFAF6EE)
                                )
                            }

                            Box(
                                modifier = Modifier.size(136.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(112.dp)
                                        .scale(orbScale)
                                        .clip(CircleShape)
                                        .background(SageSoft.copy(alpha = 0.28f))
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFAF6EE),
                                    modifier = Modifier
                                        .size(86.dp)
                                        .scale(0.95f + (orbScale - 0.85f) * 0.25f)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = if (breathingActive) "$breathCountdown" else "4-4-4",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = ForestDeep,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (breathingActive) phases[breathPhaseIndex] else "Ready",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ForestPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = { breathingActive = !breathingActive },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SageSoft,
                                    contentColor = ForestDeep
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("emergency_breathing_toggle")
                            ) {
                                Icon(
                                    imageVector = if (breathingActive) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (breathingActive) "Pause Breathing Guide" else "Start Guided Breathing",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Hotlines Section
                item {
                    SectionHeader(
                        title = "Crisis Hotlines & Resources",
                        subtitle = "Verified standard lines + clearly marked regional prototype placeholders"
                    )
                }

                items(DemoRepository.emergencyHotlines, key = { it.id }) { resource ->
                    HotlineCard(
                        resource = resource,
                        onDial = {
                            if (resource.dialNumber.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${resource.dialNumber}")
                                }
                                runCatching { context.startActivity(intent) }.onFailure {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Dialer unavailable on this device. Number: ${resource.numberDisplay}")
                                    }
                                }
                            } else {
                                clipboard.setText(AnnotatedString(resource.numberDisplay))
                                scope.launch {
                                    snackbarHostState.showSnackbar("Copied ${resource.numberDisplay}")
                                }
                            }
                        },
                        onCopy = {
                            clipboard.setText(AnnotatedString(resource.numberDisplay))
                            scope.launch {
                                snackbarHostState.showSnackbar("Copied ${resource.numberDisplay}")
                            }
                        }
                    )
                }

                // Important Non-Clinical Disclaimer
                item {
                    SoftDisclaimerCard(
                        title = "Important Prototype Disclaimer",
                        message = "NeuroTalks is an early design prototype and does NOT provide crisis counseling, therapy, or emergency dispatch. Always contact local emergency services or a verified crisis line if you need urgent help."
                    )
                }
            }
        }
    }
}

@Composable
private fun HotlineCard(
    resource: HotlineResource,
    onDial: () -> Unit,
    onCopy: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (resource.isPlaceholder) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = resource.region,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (resource.isPlaceholder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = resource.availability,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = resource.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = resource.numberDisplay,
                style = MaterialTheme.typography.headlineSmall,
                color = if (resource.isPlaceholder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDial,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (resource.isPlaceholder) ForestPrimary else CrisisCrimson,
                        contentColor = Color(0xFFFAF6EE)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("hotline_primary_${resource.id}")
                ) {
                    Icon(
                        imageVector = if (resource.dialNumber.isNotBlank()) Icons.Rounded.Call else Icons.Rounded.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(resource.actionLabel, style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("hotline_copy_${resource.id}")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = "Copy resource details",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
