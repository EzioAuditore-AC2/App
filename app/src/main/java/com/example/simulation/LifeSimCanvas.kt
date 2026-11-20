package com.example.simulation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LifeSimWorldCanvas(
    state: LifeSimState,
    nearestInteractableId: String?,
    nearestNpcId: String?,
    isHoldingDpad: Boolean,
    onWorldTap: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val ambientTransition = rememberInfiniteTransition(label = "world_ambient")
    val ambientPhase by ambientTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (size.width > 0 && size.height > 0) {
                        val nx = (offset.x / size.width.toFloat()).coerceIn(0.05f, 0.95f)
                        val ny = (offset.y / size.height.toFloat()).coerceIn(0.08f, 0.92f)
                        onWorldTap(nx, ny)
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height

        // 1. Lush botanical meadow background gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFCFE4C7),
                    Color(0xFFBAD6B2),
                    Color(0xFFA9C9A2)
                )
            ),
            size = size
        )

        // Subtle sunlit clearing oval
        drawOval(
            color = Color(0xFFE7F0DC).copy(alpha = 0.45f),
            topLeft = Offset(w * 0.14f, h * 0.28f),
            size = Size(w * 0.72f, h * 0.42f)
        )

        // 2. Winding cream stepping-stone pathways connecting all 6 landmarks
        drawSteppingStonePaths(w, h)

        // 3. Decorative forest border trees
        drawTreeCluster(Offset(w * 0.05f, h * 0.10f), w * 0.048f, Color(0xFF234B36))
        drawTreeCluster(Offset(w * 0.95f, h * 0.11f), w * 0.050f, Color(0xFF1B3B2B))
        drawTreeCluster(Offset(w * 0.05f, h * 0.50f), w * 0.046f, Color(0xFF2E5C43))
        drawTreeCluster(Offset(w * 0.95f, h * 0.51f), w * 0.048f, Color(0xFF234B36))
        drawTreeCluster(Offset(w * 0.06f, h * 0.90f), w * 0.048f, Color(0xFF1B3B2B))
        drawTreeCluster(Offset(w * 0.94f, h * 0.89f), w * 0.048f, Color(0xFF2E5C43))

        // 4. Draw all 6 interactive lifestyle-adapted landmarks
        drawStudioStation(
            center = Offset(w * 0.21f, h * 0.21f),
            scale = w * 0.115f,
            lifestyle = state.lifestyle,
            artworksCount = state.studioArtworksCount,
            highlighted = nearestInteractableId == "studio_desk",
            isEvening = state.timeOfDay == TimeOfDay.EVENING
        )
        drawLibraryNook(
            center = Offset(w * 0.50f, h * 0.17f),
            scale = w * 0.108f,
            booksRead = state.booksReadCount,
            highlighted = nearestInteractableId == "library_nook"
        )
        drawCozyCottage(
            center = Offset(w * 0.80f, h * 0.20f),
            scale = w * 0.12f,
            highlighted = nearestInteractableId == "cozy_bed",
            isEvening = state.timeOfDay == TimeOfDay.EVENING
        )
        drawBotanicalGarden(
            center = Offset(w * 0.22f, h * 0.75f),
            scale = w * 0.12f,
            bloomStage = state.gardenBloomStage,
            ambientPhase = ambientPhase,
            highlighted = nearestInteractableId == "garden_bed"
        )
        drawStillwaterPondAndBench(
            center = Offset(w * 0.53f, h * 0.82f),
            scale = w * 0.135f,
            ambientPhase = ambientPhase,
            highlighted = nearestInteractableId == "pond_bench"
        )
        drawTeaPatio(
            center = Offset(w * 0.80f, h * 0.72f),
            scale = w * 0.115f,
            ambientPhase = ambientPhase,
            highlighted = nearestInteractableId == "tea_patio",
            isEvening = state.timeOfDay == TimeOfDay.EVENING
        )

        // 5. Tap-to-walk destination waypoint ring
        if (state.targetX != null && state.targetY != null) {
            val tx = state.targetX * w
            val ty = state.targetY * h
            val pulse = 0.65f + 0.35f * sin(ambientPhase * 2f)
            drawCircle(
                color = Color(0xFFFAF6EE).copy(alpha = 0.8f),
                radius = w * 0.024f * pulse,
                center = Offset(tx, ty),
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = Color(0xFF234B36).copy(alpha = 0.5f),
                radius = w * 0.009f,
                center = Offset(tx, ty)
            )
        }

        // 6. Depth-sorted characters (NPCs + customizable Player character)
        data class DrawActor(val y: Float, val draw: () -> Unit)
        val actors = mutableListOf<DrawActor>()

        LifeSimEngine.worldNpcs.forEachIndexed { idx, npc ->
            val bob = sin(ambientPhase + idx * 1.7f) * 3f
            val nx = npc.homeX * w
            val ny = npc.homeY * h
            actors.add(
                DrawActor(npc.homeY) {
                    drawNpcCharacter(
                        center = Offset(nx, ny + bob * 0.45f),
                        unit = w * 0.036f,
                        npc = npc,
                        highlighted = nearestNpcId == npc.id,
                        ambientPhase = ambientPhase
                    )
                }
            )
        }

        val px = state.playerX * w
        val py = state.playerY * h
        val isMoving = isHoldingDpad || (state.targetX != null && state.targetY != null)
        val walkBounce = if (isMoving) abs(sin(state.walkPhase * 4f)) * 5.5f else sin(ambientPhase) * 1.6f

        actors.add(
            DrawActor(state.playerY) {
                drawCustomizableCharacter(
                    center = Offset(px, py - walkBounce),
                    shadowCenter = Offset(px, py + w * 0.034f),
                    unit = w * 0.042f,
                    appearance = state.appearance,
                    walkPhase = if (isMoving) state.walkPhase else 0f,
                    facingX = state.facingX
                )
            }
        )

        actors.sortedBy { it.y }.forEach { it.draw() }

        // 7. Time-of-day atmospheric lighting & evening fireflies
        if (state.timeOfDay.overlayAlpha > 0f) {
            drawRect(
                color = state.timeOfDay.skyTint.copy(alpha = state.timeOfDay.overlayAlpha),
                size = size
            )
        }

        if (state.timeOfDay == TimeOfDay.EVENING) {
            val fireflySeeds = listOf(
                0.18f to 0.42f, 0.34f to 0.56f, 0.62f to 0.44f,
                0.76f to 0.58f, 0.44f to 0.32f, 0.28f to 0.84f
            )
            fireflySeeds.forEachIndexed { i, (fx, fy) ->
                val driftX = cos(ambientPhase + i * 1.3f) * w * 0.018f
                val driftY = sin(ambientPhase * 1.2f + i * 0.9f) * h * 0.018f
                val glow = 0.45f + 0.55f * ((sin(ambientPhase * 2f + i) + 1f) / 2f)
                val pos = Offset(fx * w + driftX, fy * h + driftY)
                drawCircle(
                    color = Color(0xFFFFE58F).copy(alpha = glow * 0.45f),
                    radius = w * 0.018f,
                    center = pos
                )
                drawCircle(
                    color = Color(0xFFFFF8D6).copy(alpha = glow),
                    radius = w * 0.006f,
                    center = pos
                )
            }
        }
    }
}

@Composable
fun CharacterAvatarPreview(
    appearance: CharacterAppearance,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "preview_idle")
    val idleBounce by transition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_bounce"
    )

    Canvas(modifier = modifier) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.55f
        val unit = size.minDimension * 0.22f

        drawCircle(
            color = Color(0xFFE0EFE3),
            radius = size.minDimension * 0.44f,
            center = Offset(cx, size.height * 0.52f)
        )
        drawCircle(
            color = Color(0xFFFAF6EE),
            radius = size.minDimension * 0.36f,
            center = Offset(cx, size.height * 0.52f)
        )

        drawCustomizableCharacter(
            center = Offset(cx, cy + idleBounce),
            shadowCenter = Offset(cx, cy + unit * 0.92f),
            unit = unit,
            appearance = appearance,
            walkPhase = 0f,
            facingX = 0f
        )
    }
}

private fun DrawScope.drawSteppingStonePaths(w: Float, h: Float) {
    val stoneColor = Color(0xFFF3EBD8)
    val stoneEdge = Color(0xFFD5C9B0)
    val stones = listOf(
        0.24f to 0.48f, 0.32f to 0.49f, 0.40f to 0.50f, 0.48f to 0.50f,
        0.56f to 0.50f, 0.64f to 0.49f, 0.72f to 0.48f,
        0.22f to 0.40f, 0.21f to 0.33f,
        0.49f to 0.40f, 0.50f to 0.31f,
        0.78f to 0.40f, 0.79f to 0.33f,
        0.23f to 0.58f, 0.22f to 0.64f,
        0.51f to 0.59f, 0.52f to 0.67f,
        0.77f to 0.57f, 0.79f to 0.63f
    )
    stones.forEachIndexed { i, (sx, sy) ->
        val rx = w * (if (i % 2 == 0) 0.025f else 0.021f)
        val ry = h * (if (i % 2 == 0) 0.016f else 0.014f)
        val topLeft = Offset(sx * w - rx, sy * h - ry)
        drawOval(color = stoneEdge, topLeft = topLeft + Offset(0f, 2f), size = Size(rx * 2f, ry * 2f))
        drawOval(color = stoneColor, topLeft = topLeft, size = Size(rx * 2f, ry * 2f))
    }
}

private fun DrawScope.drawTreeCluster(center: Offset, r: Float, foliage: Color) {
    drawOval(
        color = Color(0xFF1A3325).copy(alpha = 0.2f),
        topLeft = Offset(center.x - r * 0.9f, center.y + r * 0.5f),
        size = Size(r * 1.8f, r * 0.7f)
    )
    drawRect(
        color = Color(0xFF7B573C),
        topLeft = Offset(center.x - r * 0.18f, center.y + r * 0.1f),
        size = Size(r * 0.36f, r * 0.72f)
    )
    drawCircle(color = foliage, radius = r, center = center)
    drawCircle(color = foliage.copy(alpha = 0.9f), radius = r * 0.74f, center = Offset(center.x - r * 0.45f, center.y + r * 0.18f))
    drawCircle(color = Color(0xFF4F8264), radius = r * 0.55f, center = Offset(center.x + r * 0.32f, center.y - r * 0.22f))
}

private fun DrawScope.drawHighlightHalo(center: Offset, radius: Float) {
    drawCircle(
        color = Color(0xFFFAF6EE).copy(alpha = 0.44f),
        radius = radius * 1.15f,
        center = center
    )
    drawCircle(
        color = Color(0xFF234B36),
        radius = radius * 1.15f,
        center = center,
        style = Stroke(width = 4f)
    )
}

private fun DrawScope.drawStudioStation(
    center: Offset,
    scale: Float,
    lifestyle: DreamLifestyle,
    artworksCount: Int,
    highlighted: Boolean,
    isEvening: Boolean
) {
    if (highlighted) drawHighlightHalo(center, scale * 0.85f)
    // Cozy warm studio / workspace deck
    drawRoundRect(
        color = Color(0xFFEAD9BC),
        topLeft = Offset(center.x - scale * 0.78f, center.y - scale * 0.56f),
        size = Size(scale * 1.56f, scale * 1.12f),
        cornerRadius = CornerRadius(18f, 18f)
    )
    // Rug tinted subtly by lifestyle
    val rugColor = when (lifestyle) {
        DreamLifestyle.PROFESSIONAL -> Color(0xFFC8DCE2)
        DreamLifestyle.ACADEMIC_SUCCESS -> Color(0xFFDCD8EB)
        DreamLifestyle.ENTREPRENEUR -> Color(0xFFF0DFC8)
        else -> Color(0xFFD5E5D6)
    }
    drawRoundRect(
        color = rugColor,
        topLeft = Offset(center.x - scale * 0.56f, center.y - scale * 0.36f),
        size = Size(scale * 1.12f, scale * 0.72f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    // Desk / Easel base
    drawRoundRect(
        color = Color(0xFF8A5D3B),
        topLeft = Offset(center.x - scale * 0.44f, center.y - scale * 0.30f),
        size = Size(scale * 0.88f, scale * 0.34f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    // Board / Canvas / Laptop screen adapting to lifestyle
    val boardColor = if (lifestyle == DreamLifestyle.PROFESSIONAL || lifestyle == DreamLifestyle.ACADEMIC_SUCCESS) {
        Color(0xFF234B36)
    } else {
        Color(0xFFFFFDF8)
    }
    drawRoundRect(
        color = boardColor,
        topLeft = Offset(center.x - scale * 0.32f, center.y - scale * 0.44f),
        size = Size(scale * 0.64f, scale * 0.40f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    if (artworksCount >= 1) {
        drawCircle(color = Color(0xFFD69F4C), radius = scale * 0.09f, center = Offset(center.x - scale * 0.12f, center.y - scale * 0.27f))
    }
    if (artworksCount >= 2) {
        drawCircle(color = Color(0xFF6A9A78), radius = scale * 0.11f, center = Offset(center.x + scale * 0.09f, center.y - scale * 0.22f))
    }
    if (artworksCount >= 3) {
        drawCircle(color = Color(0xFFC47A53), radius = scale * 0.08f, center = Offset(center.x + scale * 0.18f, center.y - scale * 0.30f))
    }
    if (isEvening) {
        drawCircle(
            color = Color(0xFFFFE599).copy(alpha = 0.45f),
            radius = scale * 0.45f,
            center = Offset(center.x + scale * 0.45f, center.y - scale * 0.32f)
        )
    }
}

private fun DrawScope.drawLibraryNook(
    center: Offset,
    scale: Float,
    booksRead: Int,
    highlighted: Boolean
) {
    if (highlighted) drawHighlightHalo(center, scale * 0.82f)
    drawRoundRect(
        color = Color(0xFF9C714F),
        topLeft = Offset(center.x - scale * 0.68f, center.y - scale * 0.48f),
        size = Size(scale * 1.36f, scale * 0.92f),
        cornerRadius = CornerRadius(14f, 14f)
    )
    val bookColors = listOf(Color(0xFF234B36), Color(0xFFC47A53), Color(0xFFD69F4C), Color(0xFF4C728A), Color(0xFF6A9A78))
    for (i in 0..4) {
        drawRoundRect(
            color = bookColors[i % bookColors.size],
            topLeft = Offset(center.x - scale * 0.50f + i * scale * 0.21f, center.y - scale * 0.32f),
            size = Size(scale * 0.16f, scale * 0.34f),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }
    drawRoundRect(
        color = Color(0xFFFAF6EE),
        topLeft = Offset(center.x - scale * 0.24f, center.y + scale * 0.10f),
        size = Size(scale * 0.48f, scale * 0.24f),
        cornerRadius = CornerRadius(5f, 5f)
    )
    if (booksRead > 0) {
        drawCircle(color = Color(0xFFD69F4C), radius = scale * 0.08f, center = Offset(center.x + scale * 0.42f, center.y + scale * 0.20f))
    }
}

private fun DrawScope.drawCozyCottage(
    center: Offset,
    scale: Float,
    highlighted: Boolean,
    isEvening: Boolean
) {
    if (highlighted) drawHighlightHalo(center, scale * 0.86f)
    drawRoundRect(
        color = Color(0xFFFAF2E3),
        topLeft = Offset(center.x - scale * 0.64f, center.y - scale * 0.18f),
        size = Size(scale * 1.28f, scale * 0.72f),
        cornerRadius = CornerRadius(12f, 12f)
    )
    val roofPath = Path().apply {
        moveTo(center.x, center.y - scale * 0.70f)
        lineTo(center.x + scale * 0.78f, center.y - scale * 0.12f)
        lineTo(center.x - scale * 0.78f, center.y - scale * 0.12f)
        close()
    }
    drawPath(roofPath, color = Color(0xFF234B36))
    drawRoundRect(
        color = Color(0xFF8C5B39),
        topLeft = Offset(center.x - scale * 0.14f, center.y + scale * 0.10f),
        size = Size(scale * 0.28f, scale * 0.44f),
        cornerRadius = CornerRadius(8f, 8f)
    )
    val windowColor = if (isEvening) Color(0xFFFFDF80) else Color(0xFFA7CDE0)
    drawRoundRect(
        color = windowColor,
        topLeft = Offset(center.x + scale * 0.24f, center.y + scale * 0.04f),
        size = Size(scale * 0.24f, scale * 0.24f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    if (isEvening) {
        drawCircle(
            color = Color(0xFFFFDF80).copy(alpha = 0.35f),
            radius = scale * 0.42f,
            center = Offset(center.x + scale * 0.36f, center.y + scale * 0.16f)
        )
    }
}

private fun DrawScope.drawBotanicalGarden(
    center: Offset,
    scale: Float,
    bloomStage: Int,
    ambientPhase: Float,
    highlighted: Boolean
) {
    if (highlighted) drawHighlightHalo(center, scale * 0.84f)
    drawRoundRect(
        color = Color(0xFF8E6342),
        topLeft = Offset(center.x - scale * 0.74f, center.y - scale * 0.48f),
        size = Size(scale * 1.48f, scale * 0.96f),
        cornerRadius = CornerRadius(14f, 14f)
    )
    drawRoundRect(
        color = Color(0xFF5D402C),
        topLeft = Offset(center.x - scale * 0.62f, center.y - scale * 0.36f),
        size = Size(scale * 1.24f, scale * 0.72f),
        cornerRadius = CornerRadius(10f, 10f)
    )
    val spots = listOf(
        -0.38f to -0.14f, 0.0f to -0.16f, 0.38f to -0.14f,
        -0.32f to 0.16f, 0.04f to 0.18f, 0.36f to 0.15f
    )
    val sway = sin(ambientPhase) * 2.5f
    val flowerColors = listOf(Color(0xFFF6D56F), Color(0xFFE79573), Color(0xFFFAF6EE), Color(0xFFD7A6D4))
    spots.forEachIndexed { idx, (ox, oy) ->
        val pos = Offset(center.x + ox * scale + sway * 0.5f, center.y + oy * scale)
        drawCircle(color = Color(0xFF79AE7A), radius = scale * 0.10f, center = pos)
        if (bloomStage > 0 && idx < bloomStage * 2) {
            drawCircle(
                color = flowerColors[idx % flowerColors.size],
                radius = scale * 0.09f,
                center = Offset(pos.x, pos.y - scale * 0.06f)
            )
            drawCircle(
                color = Color(0xFF234B36),
                radius = scale * 0.03f,
                center = Offset(pos.x, pos.y - scale * 0.06f)
            )
        }
    }
}

private fun DrawScope.drawStillwaterPondAndBench(
    center: Offset,
    scale: Float,
    ambientPhase: Float,
    highlighted: Boolean
) {
    if (highlighted) drawHighlightHalo(Offset(center.x, center.y - scale * 0.22f), scale * 0.74f)
    val pondCenter = Offset(center.x, center.y + scale * 0.25f)
    drawOval(
        color = Color(0xFF77B1AE),
        topLeft = Offset(pondCenter.x - scale * 0.85f, pondCenter.y - scale * 0.45f),
        size = Size(scale * 1.70f, scale * 0.90f)
    )
    val ripple = 0.7f + 0.3f * ((sin(ambientPhase) + 1f) / 2f)
    drawOval(
        color = Color(0xFFFAF6EE).copy(alpha = 0.4f),
        topLeft = Offset(pondCenter.x - scale * 0.45f * ripple, pondCenter.y - scale * 0.20f * ripple),
        size = Size(scale * 0.90f * ripple, scale * 0.40f * ripple),
        style = Stroke(width = 2.5f)
    )
    drawCircle(color = Color(0xFF4F8264), radius = scale * 0.13f, center = Offset(pondCenter.x - scale * 0.42f, pondCenter.y + scale * 0.06f))
    drawCircle(color = Color(0xFF5D9672), radius = scale * 0.11f, center = Offset(pondCenter.x + scale * 0.46f, pondCenter.y - scale * 0.05f))

    drawRoundRect(
        color = Color(0xFF9C6D47),
        topLeft = Offset(center.x - scale * 0.42f, center.y - scale * 0.48f),
        size = Size(scale * 0.84f, scale * 0.26f),
        cornerRadius = CornerRadius(8f, 8f)
    )
}

private fun DrawScope.drawTeaPatio(
    center: Offset,
    scale: Float,
    ambientPhase: Float,
    highlighted: Boolean,
    isEvening: Boolean
) {
    if (highlighted) drawHighlightHalo(center, scale * 0.84f)
    drawCircle(color = Color(0xFFEDE2CE), radius = scale * 0.72f, center = center)
    drawCircle(color = Color(0xFFA67954), radius = scale * 0.34f, center = center)
    drawCircle(color = Color(0xFFFAF6EE), radius = scale * 0.09f, center = Offset(center.x - scale * 0.12f, center.y - scale * 0.05f))
    drawCircle(color = Color(0xFFCDE2D2), radius = scale * 0.09f, center = Offset(center.x + scale * 0.12f, center.y + scale * 0.06f))
    val steamY = sin(ambientPhase * 1.5f) * 3f
    drawCircle(
        color = Color.White.copy(alpha = 0.55f),
        radius = scale * 0.05f,
        center = Offset(center.x - scale * 0.12f, center.y - scale * 0.18f + steamY)
    )
    if (isEvening) {
        drawCircle(
            color = Color(0xFFFFDF80).copy(alpha = 0.45f),
            radius = scale * 0.42f,
            center = Offset(center.x, center.y - scale * 0.45f)
        )
    }
}

private fun DrawScope.drawNpcCharacter(
    center: Offset,
    unit: Float,
    npc: WorldNpc,
    highlighted: Boolean,
    ambientPhase: Float
) {
    drawOval(
        color = Color(0xFF173022).copy(alpha = 0.24f),
        topLeft = Offset(center.x - unit * 0.75f, center.y + unit * 0.65f),
        size = Size(unit * 1.5f, unit * 0.48f)
    )
    drawRoundRect(
        color = npc.outfitColor,
        topLeft = Offset(center.x - unit * 0.55f, center.y + unit * 0.05f),
        size = Size(unit * 1.1f, unit * 0.78f),
        cornerRadius = CornerRadius(unit * 0.4f, unit * 0.4f)
    )
    drawCircle(color = npc.skinColor, radius = unit * 0.58f, center = Offset(center.x, center.y - unit * 0.34f))
    drawArc(
        color = npc.hairColor,
        startAngle = 185f,
        sweepAngle = 170f,
        useCenter = true,
        topLeft = Offset(center.x - unit * 0.62f, center.y - unit * 0.96f),
        size = Size(unit * 1.24f, unit * 1.05f)
    )
    drawCircle(color = Color(0xFF1C2C22), radius = unit * 0.08f, center = Offset(center.x - unit * 0.18f, center.y - unit * 0.30f))
    drawCircle(color = Color(0xFF1C2C22), radius = unit * 0.08f, center = Offset(center.x + unit * 0.18f, center.y - unit * 0.30f))

    if (highlighted) {
        val lift = sin(ambientPhase * 2f) * 4f
        val bubbleCenter = Offset(center.x, center.y - unit * 1.38f + lift)
        drawRoundRect(
            color = Color(0xFFFAF6EE),
            topLeft = Offset(bubbleCenter.x - unit * 0.56f, bubbleCenter.y - unit * 0.34f),
            size = Size(unit * 1.12f, unit * 0.62f),
            cornerRadius = CornerRadius(14f, 14f)
        )
        drawRoundRect(
            color = Color(0xFF234B36),
            topLeft = Offset(bubbleCenter.x - unit * 0.56f, bubbleCenter.y - unit * 0.34f),
            size = Size(unit * 1.12f, unit * 0.62f),
            cornerRadius = CornerRadius(14f, 14f),
            style = Stroke(width = 2.5f)
        )
        for (dot in -1..1) {
            drawCircle(
                color = Color(0xFF234B36),
                radius = unit * 0.08f,
                center = Offset(bubbleCenter.x + dot * unit * 0.25f, bubbleCenter.y - unit * 0.03f)
            )
        }
    }
}

private fun DrawScope.drawCustomizableCharacter(
    center: Offset,
    shadowCenter: Offset,
    unit: Float,
    appearance: CharacterAppearance,
    walkPhase: Float,
    facingX: Float
) {
    val skinColor = SimPalette.skinTones.getOrElse(appearance.skinToneIndex) { SimPalette.skinTones[1] }
    val hairColor = SimPalette.hairColors.getOrElse(appearance.hairColorIndex) { SimPalette.hairColors[1] }
    val outfitColor = SimPalette.outfitColors.getOrElse(appearance.outfitColorIndex) { SimPalette.outfitColors[0] }

    drawOval(
        color = Color(0xFF173022).copy(alpha = 0.28f),
        topLeft = Offset(shadowCenter.x - unit * 0.80f, shadowCenter.y - unit * 0.24f),
        size = Size(unit * 1.60f, unit * 0.48f)
    )

    val legSwing = sin(walkPhase * 4f) * unit * 0.22f
    drawCircle(
        color = Color(0xFF2B211E),
        radius = unit * 0.18f,
        center = Offset(center.x - unit * 0.26f, center.y + unit * 0.80f + legSwing)
    )
    drawCircle(
        color = Color(0xFF2B211E),
        radius = unit * 0.18f,
        center = Offset(center.x + unit * 0.26f, center.y + unit * 0.80f - legSwing)
    )

    // Outfit body
    drawRoundRect(
        color = outfitColor,
        topLeft = Offset(center.x - unit * 0.60f, center.y + unit * 0.02f),
        size = Size(unit * 1.20f, unit * 0.82f),
        cornerRadius = CornerRadius(unit * 0.42f, unit * 0.42f)
    )

    // Clothing style details
    when (appearance.clothingStyle) {
        ClothingStyle.COZY_SWEATER -> {
            drawArc(
                color = Color(0xFFFAF6EE),
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(center.x - unit * 0.28f, center.y - unit * 0.02f),
                size = Size(unit * 0.56f, unit * 0.26f),
                style = Stroke(width = unit * 0.11f, cap = StrokeCap.Round)
            )
        }
        ClothingStyle.CRISP_BLAZER -> {
            // Crisp cream shirt V-neck lapel
            val vPath = Path().apply {
                moveTo(center.x - unit * 0.22f, center.y + unit * 0.04f)
                lineTo(center.x, center.y + unit * 0.42f)
                lineTo(center.x + unit * 0.22f, center.y + unit * 0.04f)
                close()
            }
            drawPath(vPath, color = Color(0xFFFAF6EE))
        }
        ClothingStyle.LINEN_TUNIC -> {
            // Soft cream waist sash
            drawLine(
                color = Color(0xFFFAF6EE).copy(alpha = 0.85f),
                start = Offset(center.x - unit * 0.56f, center.y + unit * 0.52f),
                end = Offset(center.x + unit * 0.56f, center.y + unit * 0.52f),
                strokeWidth = unit * 0.11f,
                cap = StrokeCap.Round
            )
        }
        ClothingStyle.CREATIVE_APRON -> {
            // Cream studio apron overlay & pocket
            drawRoundRect(
                color = Color(0xFFFAF6EE).copy(alpha = 0.92f),
                topLeft = Offset(center.x - unit * 0.36f, center.y + unit * 0.24f),
                size = Size(unit * 0.72f, unit * 0.56f),
                cornerRadius = CornerRadius(unit * 0.14f, unit * 0.14f)
            )
            drawRoundRect(
                color = outfitColor.copy(alpha = 0.45f),
                topLeft = Offset(center.x - unit * 0.18f, center.y + unit * 0.48f),
                size = Size(unit * 0.36f, unit * 0.20f),
                cornerRadius = CornerRadius(unit * 0.06f, unit * 0.06f)
            )
        }
    }

    // Head
    val headCenter = Offset(center.x, center.y - unit * 0.36f)
    drawCircle(color = skinColor, radius = unit * 0.62f, center = headCenter)

    // Hair style
    when (appearance.hairStyle) {
        HairStyle.SOFT_BOB -> {
            drawArc(
                color = hairColor,
                startAngle = 170f,
                sweepAngle = 200f,
                useCenter = true,
                topLeft = Offset(headCenter.x - unit * 0.68f, headCenter.y - unit * 0.68f),
                size = Size(unit * 1.36f, unit * 1.25f)
            )
        }
        HairStyle.CURLY_PUFF -> {
            drawCircle(color = hairColor, radius = unit * 0.44f, center = Offset(headCenter.x - unit * 0.42f, headCenter.y - unit * 0.44f))
            drawCircle(color = hairColor, radius = unit * 0.44f, center = Offset(headCenter.x + unit * 0.42f, headCenter.y - unit * 0.44f))
            drawCircle(color = hairColor, radius = unit * 0.46f, center = Offset(headCenter.x, headCenter.y - unit * 0.56f))
        }
        HairStyle.COZY_BUN -> {
            drawCircle(color = hairColor, radius = unit * 0.34f, center = Offset(headCenter.x, headCenter.y - unit * 0.86f))
            drawArc(
                color = hairColor,
                startAngle = 185f,
                sweepAngle = 170f,
                useCenter = true,
                topLeft = Offset(headCenter.x - unit * 0.66f, headCenter.y - unit * 0.66f),
                size = Size(unit * 1.32f, unit * 1.04f)
            )
        }
        HairStyle.WAVY_CROP -> {
            drawArc(
                color = hairColor,
                startAngle = 190f,
                sweepAngle = 165f,
                useCenter = true,
                topLeft = Offset(headCenter.x - unit * 0.66f, headCenter.y - unit * 0.70f),
                size = Size(unit * 1.32f, unit * 0.96f)
            )
            drawCircle(color = hairColor, radius = unit * 0.24f, center = Offset(headCenter.x - unit * 0.44f, headCenter.y - unit * 0.42f))
        }
    }

    val lookOffset = facingX.coerceIn(-1f, 1f) * unit * 0.08f
    drawCircle(color = Color(0xFF1A2C21), radius = unit * 0.085f, center = Offset(headCenter.x - unit * 0.20f + lookOffset, headCenter.y + unit * 0.02f))
    drawCircle(color = Color(0xFF1A2C21), radius = unit * 0.085f, center = Offset(headCenter.x + unit * 0.20f + lookOffset, headCenter.y + unit * 0.02f))
    drawCircle(color = Color(0xFFE79573).copy(alpha = 0.42f), radius = unit * 0.11f, center = Offset(headCenter.x - unit * 0.34f + lookOffset, headCenter.y + unit * 0.14f))
    drawCircle(color = Color(0xFFE79573).copy(alpha = 0.42f), radius = unit * 0.11f, center = Offset(headCenter.x + unit * 0.34f + lookOffset, headCenter.y + unit * 0.14f))
    drawArc(
        color = Color(0xFF1A2C21),
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(headCenter.x - unit * 0.16f + lookOffset, headCenter.y + unit * 0.12f),
        size = Size(unit * 0.32f, unit * 0.18f),
        style = Stroke(width = unit * 0.07f, cap = StrokeCap.Round)
    )

    when (appearance.accessory) {
        AccessoryStyle.HEADPHONES -> {
            drawArc(
                color = Color(0xFFFAF6EE),
                startAngle = 185f,
                sweepAngle = 170f,
                useCenter = false,
                topLeft = Offset(headCenter.x - unit * 0.72f, headCenter.y - unit * 0.72f),
                size = Size(unit * 1.44f, unit * 1.36f),
                style = Stroke(width = unit * 0.15f, cap = StrokeCap.Round)
            )
            drawRoundRect(
                color = Color(0xFFA4C6AE),
                topLeft = Offset(headCenter.x - unit * 0.82f, headCenter.y - unit * 0.18f),
                size = Size(unit * 0.24f, unit * 0.42f),
                cornerRadius = CornerRadius(unit * 0.12f, unit * 0.12f)
            )
            drawRoundRect(
                color = Color(0xFFA4C6AE),
                topLeft = Offset(headCenter.x + unit * 0.58f, headCenter.y - unit * 0.18f),
                size = Size(unit * 0.24f, unit * 0.42f),
                cornerRadius = CornerRadius(unit * 0.12f, unit * 0.12f)
            )
        }
        AccessoryStyle.ROUND_GLASSES -> {
            drawCircle(
                color = Color(0xFF234B36),
                radius = unit * 0.18f,
                center = Offset(headCenter.x - unit * 0.21f + lookOffset, headCenter.y + unit * 0.02f),
                style = Stroke(width = unit * 0.06f)
            )
            drawCircle(
                color = Color(0xFF234B36),
                radius = unit * 0.18f,
                center = Offset(headCenter.x + unit * 0.21f + lookOffset, headCenter.y + unit * 0.02f),
                style = Stroke(width = unit * 0.06f)
            )
            drawLine(
                color = Color(0xFF234B36),
                start = Offset(headCenter.x - unit * 0.03f + lookOffset, headCenter.y + unit * 0.02f),
                end = Offset(headCenter.x + unit * 0.03f + lookOffset, headCenter.y + unit * 0.02f),
                strokeWidth = unit * 0.06f
            )
        }
        AccessoryStyle.LEAF_SPROUT -> {
            drawLine(
                color = Color(0xFF36664B),
                start = Offset(headCenter.x, headCenter.y - unit * 0.62f),
                end = Offset(headCenter.x, headCenter.y - unit * 0.96f),
                strokeWidth = unit * 0.08f,
                cap = StrokeCap.Round
            )
            drawOval(
                color = Color(0xFF6A9A78),
                topLeft = Offset(headCenter.x - unit * 0.30f, headCenter.y - unit * 1.06f),
                size = Size(unit * 0.32f, unit * 0.18f)
            )
            drawOval(
                color = Color(0xFF4F8264),
                topLeft = Offset(headCenter.x, headCenter.y - unit * 1.04f),
                size = Size(unit * 0.30f, unit * 0.18f)
            )
        }
        AccessoryStyle.SUN_HAT -> {
            drawOval(
                color = Color(0xFFE7CA8C),
                topLeft = Offset(headCenter.x - unit * 0.88f, headCenter.y - unit * 0.72f),
                size = Size(unit * 1.76f, unit * 0.42f)
            )
            drawArc(
                color = Color(0xFFD6B26B),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(headCenter.x - unit * 0.52f, headCenter.y - unit * 1.02f),
                size = Size(unit * 1.04f, unit * 0.68f)
            )
        }
    }
}
