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
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import wade.owen.watts.base_jetpack.R
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

    LaunchedEffect(uiState.originalDiary) {
        uiState.originalDiary?.let {
            if (richState.toMarkdown().isBlank() && it.content.isNotBlank()) {
                richState.setMarkdown(it.content)
            }
        }
    }

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
                is DiaryDetailEvent.DiaryDetailError -> snackbar.showSnackbar(event.message)
                is DiaryDetailEvent.ValidationError -> snackbar.showSnackbar(event.message)
                is DiaryDetailEvent.SaveSuccess -> snackbar.showSnackbar("Entry saved successfully")
                is DiaryDetailEvent.LocationInserted -> snackbar.showSnackbar("📍 ${event.address}")
                is DiaryDetailEvent.ImagePicked -> {}
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
                        if (result == SnackbarResult.ActionPerformed) viewModel.undoDelete()
                    }
                }
                is DiaryDetailEvent.DiaryDeleted -> navController.popBackStack()
            }
        }
    }

    // ── Image picker ──────────────────────────────────────────────────────────
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> -> uris.forEach { viewModel.addImage(it) } }

    // ── Location permissions ──────────────────────────────────────────────────
    val locationPerms = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    // ── Date string ───────────────────────────────────────────────────────────
    val dateString = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        .format(uiState.originalDiary?.createdDate ?: Date())
        .uppercase(Locale.getDefault())

    val isSaving = uiState.loadStatus == wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus.LOADING

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
            if (uiState.isViewMode) {
                // ── View mode nav: back | (space) | pencil ────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(cs.background)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        tint = cs.onBackground,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { viewModel.checkChangesAndDismiss() }
                            )
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = cs.onBackground,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { viewModel.switchToEditMode() }
                            )
                    )
                }
            } else {
                // ── Edit mode nav: back | "New Entry" centered | "Save" ───────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cs.background)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left),
                            contentDescription = "Back",
                            tint = cs.onBackground,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { viewModel.checkChangesAndDismiss() }
                                )
                        )
                        Text(
                            text = if (isNew) "New Entry" else "Edit Entry",
                            style = ty.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                            ),
                            color = cs.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = cs.onBackground,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Save",
                                style = ty.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                ),
                                color = if (uiState.title.isNotEmpty()) cs.onBackground
                                else cs.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    enabled = uiState.title.isNotEmpty(),
                                    onClick = { viewModel.saveDiary() }
                                )
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (!uiState.isViewMode) {
                DiaryBottomToolbar(
                    wordCount = uiState.wordCount,
                    isSavingDraft = uiState.isSavingDraft,
                    draftSavedIndicator = uiState.draftSavedIndicator,
                    isLoadingLocation = uiState.isLoadingLocation,
                    onImageClick = { imageLauncher.launch("image/*") },
                    onMicClick = {},
                    onTagClick = {},
                    onLocationClick = {
                        if (locationPerms.allPermissionsGranted) {
                            viewModel.fetchAndInsertLocation()
                        } else {
                            locationPerms.launchMultiplePermissionRequest()
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        if (uiState.isViewMode) {
            // ── VIEW MODE ─────────────────────────────────────────────────────
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero image (first image if available)
                if (uiState.imageUris.isNotEmpty()) {
                    AsyncImage(
                        model = uiState.imageUris.first(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                    )
                    Spacer(Modifier.height(20.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date
                    Text(
                        text = dateString,
                        style = ty.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp,
                        ),
                        color = cs.onSurfaceVariant,
                    )

                    // Title
                    Text(
                        text = uiState.title,
                        style = ty.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            lineHeight = (26 * 1.3).sp,
                        ),
                        color = cs.onBackground,
                    )

                    // Body text
                    if (uiState.content.isNotBlank()) {
                        Text(
                            text = uiState.content,
                            style = ty.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = (14 * 1.7).sp,
                            ),
                            color = cs.onBackground,
                        )
                    }

                    // Tags (hashtags extracted from content)
                    val hashtags = "#\\w+".toRegex()
                        .findAll(uiState.content)
                        .map { it.value }
                        .distinct()
                        .toList()
                    if (hashtags.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(hashtags) { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(cs.surfaceVariant)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        style = ty.labelSmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp,
                                        ),
                                        color = cs.onBackground,
                                    )
                                }
                            }
                        }
                    }

                    // Remaining images (index 1+)
                    if (uiState.imageUris.size > 1) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.imageUris.drop(1).forEach { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        } else {
            // ── EDIT MODE ─────────────────────────────────────────────────────
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                ) {
                    // Date label
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = dateString,
                        style = ty.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 1.5.sp,
                        ),
                        color = cs.onSurfaceVariant,
                    )

                    Spacer(Modifier.height(32.dp))

                    // Title input
                    BasicTextField(
                        value = uiState.title,
                        onValueChange = {
                            viewModel.updateTitle(it)
                            viewModel.clearValidationError()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = ty.headlineMedium.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 28.sp,
                            lineHeight = 34.sp,
                            color = cs.onBackground,
                        ),
                        cursorBrush = SolidColor(cs.onBackground),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        decorationBox = { inner ->
                            if (uiState.title.isEmpty()) {
                                Text(
                                    text = "Entry Title",
                                    style = ty.headlineMedium.copy(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 28.sp,
                                        color = cs.outline,
                                    )
                                )
                            }
                            inner()
                        },
                    )

                    if (uiState.validationError != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = uiState.validationError!!,
                            style = ty.labelSmall,
                            color = cs.error,
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                }

                // Rich text editor (body)
                RichTextEditor(
                    state = richState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(320.dp),
                    placeholder = {
                        Text(
                            text = "How was your day?",
                            style = ty.bodyMedium.copy(
                                fontSize = 16.sp,
                                lineHeight = (16 * 1.6).sp,
                                color = cs.outline,
                            ),
                        )
                    },
                    textStyle = ty.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = (16 * 1.6).sp,
                        color = cs.onBackground,
                    ),
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )

                // Image thumbnails (horizontal strip)
                if (uiState.imageUris.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
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

                Spacer(Modifier.height(16.dp))
            }
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

// ─── Bottom Toolbar ───────────────────────────────────────────────────────────

@Composable
private fun DiaryBottomToolbar(
    wordCount: Int,
    isSavingDraft: Boolean,
    draftSavedIndicator: String?,
    isLoadingLocation: Boolean,
    onImageClick: () -> Unit,
    onMicClick: () -> Unit,
    onTagClick: () -> Unit,
    onLocationClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Column {
        HorizontalDivider(color = cs.outline, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(cs.background)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Left: action icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_image),
                    contentDescription = "Add image",
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onImageClick,
                        )
                )
                Icon(
                    painter = painterResource(R.drawable.ic_mic),
                    contentDescription = "Record voice",
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onMicClick,
                        )
                )
                Icon(
                    painter = painterResource(R.drawable.ic_tag),
                    contentDescription = "Add tag",
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onTagClick,
                        )
                )
                if (isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = cs.onSurfaceVariant,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_map_pin),
                        contentDescription = "Insert location",
                        tint = cs.onSurfaceVariant,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onLocationClick,
                            )
                    )
                }
            }

            // Right: draft status / word count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (isSavingDraft) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = cs.onSurfaceVariant,
                        strokeWidth = 1.5.dp,
                    )
                }
                Text(
                    text = when {
                        isSavingDraft -> "Saving..."
                        draftSavedIndicator != null -> draftSavedIndicator
                        else -> "$wordCount words"
                    },
                    style = ty.labelSmall.copy(fontSize = 13.sp),
                    color = cs.onSurfaceVariant,
                )
            }
        }
    }
}

// ─── Image Thumbnail ──────────────────────────────────────────────────────────

@Composable
private fun DiaryImageThumbnail(
    uri: Uri,
    onRemove: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme

    Box(modifier = Modifier.size(96.dp)) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp)),
        )
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
                painter = painterResource(R.drawable.ic_x),
                contentDescription = "Remove image",
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}
