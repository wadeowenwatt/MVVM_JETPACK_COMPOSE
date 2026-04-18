@file:OptIn(
    com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
)

package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import wade.owen.watts.base_jetpack.core.designsystem.AppAlertDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Page ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DiaryDetailPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: DiaryDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    val isNew = uiState.diaryId == null
    val context = LocalContext.current

    // ── RichTextEditor state ──────────────────────────────────────────────────
    val richState = rememberRichTextState()

    // Sync initial content khi load entry cũ (chỉ chạy 1 lần khi content khác rỗng)
    LaunchedEffect(uiState.originalDiary) {
        uiState.originalDiary?.let {
            if (richState.toMarkdown().isBlank() && it.content.isNotBlank()) {
                richState.setMarkdown(it.content)
            }
        }
    }

    // Sync RichEditor → ViewModel mỗi khi text thay đổi
    LaunchedEffect(richState.annotatedString) {
        viewModel.updateContent(richState.toMarkdown())
    }

    // ── Snackbar ──────────────────────────────────────────────────────────────
    val snackbar = remember { SnackbarHostState() }

    // ── Events ────────────────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is DiaryDetailEvent.NavigateBack -> navController.popBackStack()
                is DiaryDetailEvent.DiaryDetailError -> snackbar.showSnackbar(
                    event.message
                )
                is DiaryDetailEvent.ValidationError -> snackbar.showSnackbar(
                    event.message
                )
                is DiaryDetailEvent.SaveSuccess -> snackbar.showSnackbar(
                    "Entry saved successfully"
                )
                is DiaryDetailEvent.LocationInserted -> snackbar.showSnackbar("📍 ${event.address}")
                is DiaryDetailEvent.ImagePicked -> { /* handled via addImage */
                }
                is DiaryDetailEvent.ShareDiary -> {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_SUBJECT, event.title)
                        putExtra(Intent.EXTRA_TEXT, "${event.title}\n\n${event.content}")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(intent, "Share diary entry"))
                }
                is DiaryDetailEvent.ShowUndoSnackbar -> {
                    snackbar.showSnackbar(
                        event.message,
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.undoDelete()
                        }
                    }
                }
                is DiaryDetailEvent.DiaryDeleted -> navController.popBackStack()
            }
        }
    }

    // ── Image picker launcher ─────────────────────────────────────────────────
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { viewModel.addImage(it) }
    }

    // ── Location permissions ──────────────────────────────────────────────────
    val locationPerms = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    // ─────────────────────────────────────────────────────────────────────────
    Scaffold(
        containerColor = cs.background,
        snackbarHost = {
            SnackbarHost(snackbar) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                )
            }
        },
        topBar = {
            DiaryDetailTopBar(
                isNew = isNew,
                isViewMode = uiState.isViewMode,
                isSaving = uiState.loadStatus == wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus.LOADING,
                isDeleting = uiState.isDeletingEntry,
                isSavingDraft = uiState.isSavingDraft,
                draftSavedIndicator = uiState.draftSavedIndicator,
                title = uiState.title,
                content = uiState.content,
                onBack = { viewModel.checkChangesAndDismiss() },
                onSave = { viewModel.saveDiary() },
                onDelete = { viewModel.showDeleteConfirmDialog() },
                onShare = {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_SUBJECT, uiState.title)
                        putExtra(Intent.EXTRA_TEXT, "${uiState.title}\n\n${uiState.content}")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(intent, "Share diary entry"))
                },
                onCopied = {
                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("diary_entry", "${uiState.title}\n\n${uiState.content}")
                    clipboard.setPrimaryClip(clip)
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Date label ────────────────────────────────────────────────────
            Text(
                text = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                    .format(Date()).uppercase(Locale.getDefault()),
                style = ty.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.6.sp,
                ),
                color = cs.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                ),
            )

            // ── Timestamps (shown in view mode) ────────────────────────────────
            val originalDiary = uiState.originalDiary
            if (uiState.isViewMode && originalDiary != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Created",
                            style = ty.labelSmall,
                            color = cs.onSurfaceVariant,
                        )
                        Text(
                            text = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
                                .format(originalDiary.createdDate),
                            style = ty.bodySmall,
                            color = cs.onBackground,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Updated",
                            style = ty.labelSmall,
                            color = cs.onSurfaceVariant,
                        )
                        Text(
                            text = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
                                .format(originalDiary.updatedDate),
                            style = ty.bodySmall,
                            color = cs.onBackground,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Title ─────────────────────────────────────────────────────────
            androidx.compose.foundation.text.BasicTextField(
                value = uiState.title,
                onValueChange = {
                    if (!uiState.isViewMode) {
                        viewModel.updateTitle(it)
                        viewModel.clearValidationError()  // Clear error on edit
                    }
                },
                enabled = !uiState.isViewMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textStyle = ty.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp,
                    color = cs.onBackground,
                ),
                cursorBrush = SolidColor(cs.onBackground),
                decorationBox = { inner ->
                    if (uiState.title.isEmpty()) {
                        Text(
                            text = "Entry Title",
                            style = ty.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = cs.onSurfaceVariant.copy(alpha = 0.4f),
                            )
                        )
                    }
                    inner()
                },
            )

            // ── Validation error message ──────────────────────────────────────
            if (uiState.validationError != null) {
                Text(
                    text = uiState.validationError!!,
                    style = ty.labelSmall,
                    color = cs.error,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = cs.outline,
                thickness = 1.dp,
            )
            Spacer(Modifier.height(12.dp))

            // ── Rich Text Editor ──────────────────────────────────────────────
            if (uiState.isViewMode) {
                // Read-only content display in view mode
                var isExpanded by remember { mutableStateOf(false) }
                val wordCount = uiState.content.split("\\s+".toRegex()).count { it.isNotBlank() }
                val shouldShowReadMore = wordCount > 300

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(cs.surfaceVariant.copy(alpha = 0.3f))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = if (uiState.content.isNotEmpty()) {
                                if (shouldShowReadMore && !isExpanded) {
                                    uiState.content.split("\\s+".toRegex()).take(150).joinToString(" ") + "..."
                                } else {
                                    uiState.content
                                }
                            } else {
                                "No content"
                            },
                            style = ty.bodyLarge.copy(
                                lineHeight = 26.sp,
                                color = if (uiState.content.isNotEmpty()) cs.onBackground else cs.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )

                        if (shouldShowReadMore) {
                            Spacer(Modifier.height(8.dp))
                            TextButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.padding(start = 0.dp)
                            ) {
                                Text(
                                    text = if (isExpanded) "Show less" else "Read more",
                                    style = ty.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = cs.primary
                                )
                            }
                        }
                    }
                }

                // ── Hashtags display ─────────────────────────────────────────
                val hashtagPattern = "#\\w+".toRegex()
                val hashtags = hashtagPattern.findAll(uiState.content).map { it.value }.distinct().toList()
                if (hashtags.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(hashtags) { hashtag ->
                                androidx.compose.material3.OutlinedButton(
                                    onClick = { },
                                    modifier = Modifier.height(32.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        cs.primary
                                    )
                                ) {
                                    Text(
                                        text = hashtag,
                                        style = ty.labelSmall,
                                        color = cs.primary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Editable RichTextEditor in edit mode
                RichTextEditor(
                    state = richState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .height(320.dp),
                    placeholder = {
                        Text(
                            text = "How was your day?",
                            style = ty.displaySmall.copy(
                                lineHeight = 32.sp,
                                color = cs.outline.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 32.dp),
                            textAlign = TextAlign.Center
                        )
                    },
                    textStyle = ty.bodyLarge.copy(
                        lineHeight = 26.sp,
                        color = cs.onBackground,
                    ),
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            }

            // ── Selected images strip ─────────────────────────────────────────
            if (uiState.imageUris.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                if (uiState.isViewMode) {
                    // Full-width images in view mode
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.imageUris.forEach { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                            )
                        }
                    }
                } else {
                    // Horizontal scroll in edit mode
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(uiState.imageUris) { uri ->
                            DiaryImageThumbnail(
                                uri = uri,
                                onRemove = { viewModel.removeImage(uri) },
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Bottom toolbar ────────────────────────────────────────────────
            DiaryBottomToolbar(
                wordCount = uiState.wordCount,
                charCount = uiState.content.length,
                isLoadingLocation = uiState.isLoadingLocation,
                isSavingDraft = uiState.isSavingDraft,
                draftSavedIndicator = uiState.draftSavedIndicator,
                onImageClick = { imageLauncher.launch("image/*") },
                onLocationClick = {
                    if (locationPerms.allPermissionsGranted) {
                        viewModel.fetchAndInsertLocation()
                    } else {
                        locationPerms.launchMultiplePermissionRequest()
                    }
                },
                onTtsClick = { viewModel.onTtsClick() },
            )
        }
    }

    // ── Discard dialog ────────────────────────────────────────────────────────
    if (uiState.showDiscardDialog) {
        AppAlertDialog(
            title = "Discard Changes?",
            content = "Your unsaved changes will be lost. Do you want to continue?",
            onConfirm = { viewModel.confirmDiscard() },
            onDismissRequest = { viewModel.dismissDiscardDialog() },
            titleButtonConfirm = "Discard",
            titleButtonDismiss = "Keep Editing",
        )
    }

    // ── Delete confirmation dialog ────────────────────────────────────────────
    if (uiState.showDeleteConfirmDialog) {
        AppAlertDialog(
            title = "Delete Entry?",
            content = "This action cannot be undone immediately. You'll have 3 seconds to undo.",
            onConfirm = { viewModel.confirmDelete() },
            onDismissRequest = { viewModel.dismissDeleteConfirmDialog() },
            titleButtonConfirm = "Delete",
            titleButtonDismiss = "Cancel",
        )
    }
}

// ─── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DiaryDetailTopBar(
    isNew: Boolean,
    isViewMode: Boolean,
    isSaving: Boolean,
    isDeleting: Boolean,
    isSavingDraft: Boolean,
    draftSavedIndicator: String?,
    title: String,
    content: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onCopied: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Back button
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = cs.onBackground,
                    modifier = Modifier.size(22.dp),
                )
            }

            // Title (centered)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isNew) "New diary" else "Edit diary",
                    style = ty.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = cs.onBackground,
                    textAlign = TextAlign.Center,
                )
                // Show draft saved indicator
                if (draftSavedIndicator != null) {
                    Text(
                        text = draftSavedIndicator,
                        style = ty.labelSmall,
                        color = cs.onSurfaceVariant,
                    )
                }
            }

            // Action buttons (share/copy/delete when viewing, save otherwise)
            if (isViewMode) {
                Row(
                    modifier = Modifier.width(160.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCopied, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Copy",
                            tint = cs.onBackground,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = cs.onBackground,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = cs.onBackground,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = cs.error,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            } else if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = cs.onBackground,
                    strokeWidth = 2.dp,
                )
            } else {
                TextButton(onClick = onSave, enabled = title.isNotEmpty()) {
                    Text(
                        text = "Save",
                        style = ty.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        ),
                        color = if (title.isNotEmpty()) cs.onBackground else cs.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                }
            }
        }

        HorizontalDivider(color = cs.outline, thickness = 1.dp)
    }
}

// ─── Bottom Toolbar ───────────────────────────────────────────────────────────

@Composable
private fun DiaryBottomToolbar(
    wordCount: Int,
    charCount: Int,
    isLoadingLocation: Boolean,
    isSavingDraft: Boolean,
    draftSavedIndicator: String?,
    onImageClick: () -> Unit,
    onLocationClick: () -> Unit,
    onTtsClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    val contentCharWarning = 5000
    val showCharWarning = charCount > contentCharWarning

    Column {
        // Show char count warning if needed
        if (showCharWarning) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cs.errorContainer)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Character limit warning",
                    style = ty.labelSmall,
                    color = cs.onErrorContainer,
                )
                Text(
                    text = "$charCount / $contentCharWarning",
                    style = ty.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = cs.onErrorContainer,
                )
            }
        }

        HorizontalDivider(
            color = cs.outline,
            thickness = 1.dp,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cs.background)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Image picker icon ──────────────────────────────────────────
            IconButton(onClick = onImageClick) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_gallery),
                    contentDescription = "Add image",
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }

            // ── Voice icon (placeholder for future implementation) ────────────
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_btn_speak_now),
                    contentDescription = "Record voice note",
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }

            // ── Link icon (placeholder for future implementation) ──────────────
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Insert link",
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }

            // ── Location icon ──────────────────────────────────────────────
            IconButton(onClick = onLocationClick) {
                if (isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = cs.onSurfaceVariant,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Insert location",
                        tint = cs.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Draft saving indicator ──────────────────────────────────────
            if (isSavingDraft) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = cs.onSurfaceVariant,
                        strokeWidth = 1.5.dp,
                    )
                    Text(
                        text = "Saving draft...",
                        style = ty.labelSmall,
                        color = cs.onSurfaceVariant,
                    )
                }
            }

            // ── Word count ─────────────────────────────────────────────────
            Text(
                text = "$wordCount words",
                style = ty.labelMedium,
                color = cs.onSurfaceVariant,
            )
        }
    }
}

// ─── Image Thumbnail ──────────────────────────────────────────────────────────

@Composable
private fun DiaryImageThumbnail(
    uri: Uri,
    onRemove: () -> Unit,
) {
    Box(modifier = Modifier.size(96.dp)) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp)),
        )
        // Remove button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0x99000000))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove image",
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}
