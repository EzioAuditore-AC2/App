package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.GppGood
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ForumCategory
import com.example.data.ForumPost
import com.example.ui.components.PrototypeBadge
import com.example.ui.components.SoftDisclaimerCard
import com.example.ui.theme.ClayWarm
import com.example.ui.theme.ForestDeep
import com.example.ui.theme.ForestPrimary
import com.example.ui.theme.SageSoft
import com.example.viewmodel.NeuroTalksUiState

@Composable
fun ForumScreen(
    uiState: NeuroTalksUiState,
    onSelectCategory: (ForumCategory) -> Unit,
    onToggleExpanded: (String) -> Unit,
    onToggleWarmth: (String) -> Unit,
    onToggleRelate: (String) -> Unit,
    onAddReply: (String, String) -> Unit,
    onCreatePost: (String, String, ForumCategory, String) -> Unit,
    onReportPost: (String) -> Unit,
    onOpenGuidelines: () -> Unit
) {
    var showComposerDialog by remember { mutableStateOf(false) }
    var reportingPostId by remember { mutableStateOf<String?>(null) }

    val visiblePosts = remember(uiState.forumPosts, uiState.selectedForumCategory) {
        uiState.forumPosts.filter { post ->
            !post.isReported && (uiState.selectedForumCategory == ForumCategory.ALL || post.category == uiState.selectedForumCategory)
        }
    }

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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = ForestDeep
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PrototypeBadge(text = "ANONYMOUS DEMO FEED", inverted = true)
                            TextButton(
                                onClick = onOpenGuidelines,
                                colors = ButtonDefaults.textButtonColors(contentColor = SageSoft),
                                modifier = Modifier.testTag("forum_guidelines_link")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.GppGood,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Guidelines", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        Text(
                            text = "Community Forum",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFAF6EE)
                        )
                        Text(
                            text = "Posting locally as ${uiState.userAlias}. Sample entries and your test notes stay strictly on this device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SageSoft
                        )
                        Button(
                            onClick = { showComposerDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFAF6EE),
                                contentColor = ForestDeep
                            ),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("forum_new_post_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share an Anonymous Demo Note",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ForumCategory.entries.forEach { category ->
                        FilterChip(
                            selected = uiState.selectedForumCategory == category,
                            onClick = { onSelectCategory(category) },
                            label = { Text(category.label) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("forum_filter_${category.name.lowercase()}")
                        )
                    }
                }
            }

            // Posts
            items(visiblePosts, key = { it.id }) { post ->
                ForumPostCard(
                    post = post,
                    isExpanded = post.id in uiState.expandedPostIds,
                    onToggleExpanded = { onToggleExpanded(post.id) },
                    onToggleWarmth = { onToggleWarmth(post.id) },
                    onToggleRelate = { onToggleRelate(post.id) },
                    onSubmitReply = { reply -> onAddReply(post.id, reply) },
                    onRequestReport = { reportingPostId = post.id }
                )
            }

            item {
                SoftDisclaimerCard(
                    title = "Prototype Peer Space",
                    message = "No real multi-user accounts or cloud syncing exist yet. Avoid entering real names or sensitive personal health information."
                )
            }
        }
    }

    if (showComposerDialog) {
        CreateNoteDialog(
            alias = uiState.userAlias,
            onDismiss = { showComposerDialog = false },
            onSubmit = { title, body, category, mood ->
                onCreatePost(title, body, category, mood)
                showComposerDialog = false
            }
        )
    }

    if (reportingPostId != null) {
        AlertDialog(
            onDismissRequest = { reportingPostId = null },
            title = { Text("Report this demo post?") },
            text = {
                Text(
                    "Thank you for caring about community safety. In this prototype, reporting hides the post from your local feed immediately."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        reportingPostId?.let { onReportPost(it) }
                        reportingPostId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hide & Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { reportingPostId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ForumPostCard(
    post: ForumPost,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onToggleWarmth: () -> Unit,
    onToggleRelate: () -> Unit,
    onSubmitReply: (String) -> Unit,
    onRequestReport: () -> Unit
) {
    var replyDraft by remember(post.id) { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Author Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = post.authorAlias.take(1).uppercase(),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column {
                        Text(
                            text = post.authorAlias,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${post.timestamp} • ${post.category.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = post.moodTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(
                        onClick = onRequestReport,
                        modifier = Modifier.testTag("forum_report_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Flag,
                            contentDescription = "Report post",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
            )

            // Empathy Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (post.hasSentWarmth) ClayWarm.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onToggleWarmth() }
                        .testTag("forum_warmth_${post.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (post.hasSentWarmth) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Send warmth",
                            tint = if (post.hasSentWarmth) ClayWarm else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Warmth (${post.warmthCount})",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (post.hasSentWarmth) ClayWarm else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (post.hasRelated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onToggleRelate() }
                        .testTag("forum_relate_${post.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VolunteerActivism,
                            contentDescription = "I relate",
                            tint = if (post.hasRelated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Relate (${post.relateCount})",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (post.hasRelated) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Transparent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { onToggleExpanded() }
                        .testTag("forum_replies_toggle_${post.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ChatBubbleOutline,
                            contentDescription = "Toggle replies",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${post.replies.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Expandable Replies Section
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    post.replies.forEach { reply ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = reply.authorAlias,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = reply.timestamp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = reply.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Local Anonymous Reply Composer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = replyDraft,
                            onValueChange = { replyDraft = it },
                            placeholder = { Text("Add a kind anonymous reply…", style = MaterialTheme.typography.bodySmall) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("forum_reply_input_${post.id}")
                        )
                        IconButton(
                            onClick = {
                                if (replyDraft.isNotBlank()) {
                                    onSubmitReply(replyDraft)
                                    replyDraft = ""
                                }
                            },
                            enabled = replyDraft.isNotBlank(),
                            modifier = Modifier.testTag("forum_reply_send_${post.id}")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = "Send reply",
                                tint = if (replyDraft.isNotBlank()) ForestPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateNoteDialog(
    alias: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, ForumCategory, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ForumCategory.SMALL_WINS) }
    var moodTag by remember { mutableStateOf("Reflective") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Share an Anonymous Note", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Posting locally as $alias (demo only)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ForumCategory.entries.filter { it != ForumCategory.ALL }.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Headline") },
                    placeholder = { Text("A gentle thought or small win…") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_post_title_input")
                )
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Your note") },
                    placeholder = { Text("Keep it anonymous—no real names or private details.") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_post_body_input")
                )
                OutlinedTextField(
                    value = moodTag,
                    onValueChange = { moodTag = it },
                    label = { Text("Mood tag") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(title, body, selectedCategory, moodTag) },
                enabled = title.isNotBlank() && body.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                modifier = Modifier.testTag("new_post_submit_button")
            ) {
                Text("Post Locally")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
