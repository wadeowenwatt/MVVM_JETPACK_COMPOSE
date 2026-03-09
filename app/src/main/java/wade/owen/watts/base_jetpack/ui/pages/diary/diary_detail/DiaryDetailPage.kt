@file:OptIn(
    com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
)

package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import wade.owen.watts.base_jetpack.ui.theme.DiaryBackgroundDark
import wade.owen.watts.base_jetpack.ui.theme.DiaryBackgroundLight
import wade.owen.watts.base_jetpack.ui.theme.DiaryBorderDark
import wade.owen.watts.base_jetpack.ui.theme.DiaryBorderLight
import wade.owen.watts.base_jetpack.ui.theme.DiaryPrimary
import wade.owen.watts.base_jetpack.ui.theme.InterFontFamily
import wade.owen.watts.base_jetpack.ui.theme.Slate400
import wade.owen.watts.base_jetpack.ui.theme.Slate500
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Colour helpers (reuse same pattern as DiaryPage) ─────────────────────────

@Composable private fun isDark() = isSystemInDarkTheme()

private fun bgColor(dark: Boolean)      = if (dark) DiaryBackgroundDark else DiaryBackgroundLight
private fun textPrimary(dark: Boolean)  = if (dark) Color(0xFFF1F5F9)   else DiaryPrimary
private fun textMuted(dark: Boolean)    = if (dark) Slate400              else Slate500
private fun borderColor(dark: Boolean)  = if (dark) DiaryBorderDark      else DiaryBorderLight
private fun toolbarIconTint(dark: Boolean) = if (dark) Slate400 else Slate500

// ─── Page ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DiaryDetailPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: DiaryDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val dark     = isDark()
    val isNew    = uiState.diaryId == null

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
                is DiaryDetailEvent.NavigateBack      -> navController.popBackStack()
                is DiaryDetailEvent.DiaryDetailError  -> snackbar.showSnackbar(event.message)
                is DiaryDetailEvent.LocationInserted  -> snackbar.showSnackbar("📍 ${event.address}")
                is DiaryDetailEvent.ImagePicked       -> { /* handled via addImage */ }
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
        containerColor = bgColor(dark),
        snackbarHost   = {
            SnackbarHost(snackbar) { data ->
                Snackbar(
                    snackbarData    = data,
                    containerColor  = if (dark) Color(0xFF1E293B) else Color(0xFF1E293B),
                    contentColor    = Color.White,
                    shape           = RoundedCornerShape(12.dp),
                )
            }
        },
        topBar = {
            DiaryDetailTopBar(
                dark     = dark,
                isNew    = isNew,
                isSaving = uiState.loadStatus == wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus.LOADING,
                onBack   = { viewModel.checkChangesAndDismiss() },
                onSave   = { viewModel.saveDiary() },
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
                text  = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                    .format(Date()).uppercase(Locale.getDefault()),
                style = TextStyle(
                    fontFamily    = InterFontFamily,
                    fontWeight    = FontWeight.Medium,
                    fontSize      = 11.sp,
                    letterSpacing = 1.6.sp,
                ),
                color    = textMuted(dark),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            )

            // ── Title ─────────────────────────────────────────────────────────
            androidx.compose.foundation.text.BasicTextField(
                value         = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textStyle = TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 28.sp,
                    color      = textPrimary(dark),
                    lineHeight  = 36.sp,
                ),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(textPrimary(dark)),
                decorationBox = { inner ->
                    if (uiState.title.isEmpty()) {
                        Text(
                            text  = "Entry Title",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 28.sp,
                                color      = textMuted(dark).copy(alpha = 0.4f),
                            )
                        )
                    }
                    inner()
                },
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                modifier  = Modifier.padding(horizontal = 20.dp),
                color     = borderColor(dark),
                thickness = 1.dp,
            )
            Spacer(Modifier.height(12.dp))

            // ── Rich Text Editor ──────────────────────────────────────────────
            RichTextEditor(
                state         = richState,
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(320.dp),
                placeholder   = {
                    Text(
                        text  = "How was your day?",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontSize   = 16.sp,
                            color      = textMuted(dark).copy(alpha = 0.5f),
                        )
                    )
                },
                textStyle = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize   = 16.sp,
                    lineHeight  = 26.sp,
                    color      = textPrimary(dark),
                ),
                colors = RichTextEditorDefaults.richTextEditorColors(
                    containerColor     = Color.Transparent,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )

            // ── Selected images strip ─────────────────────────────────────────
            if (uiState.imageUris.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    modifier       = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.imageUris) { uri ->
                        DiaryImageThumbnail(
                            uri      = uri,
                            onRemove = { viewModel.removeImage(uri) },
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Bottom toolbar ────────────────────────────────────────────────
            DiaryBottomToolbar(
                dark              = dark,
                wordCount         = uiState.wordCount,
                isLoadingLocation = uiState.isLoadingLocation,
                onImageClick      = { imageLauncher.launch("image/*") },
                onLocationClick   = {
                    if (locationPerms.allPermissionsGranted) {
                        viewModel.fetchAndInsertLocation()
                    } else {
                        locationPerms.launchMultiplePermissionRequest()
                    }
                },
                onTtsClick        = { viewModel.onTtsClick() },
            )
        }
    }

    // ── Discard dialog ────────────────────────────────────────────────────────
    if (uiState.showDiscardDialog) {
        AppAlertDialog(
            title              = "Discard Changes?",
            content            = "Your unsaved changes will be lost. Do you want to continue?",
            onConfirm          = { viewModel.confirmDiscard() },
            onDismissRequest   = { viewModel.dismissDiscardDialog() },
            titleButtonConfirm = "Discard",
            titleButtonDismiss = "Keep Editing",
        )
    }
}

// ─── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DiaryDetailTopBar(
    dark    : Boolean,
    isNew   : Boolean,
    isSaving: Boolean,
    onBack  : () -> Unit,
    onSave  : () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor(dark))
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
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint               = textPrimary(dark),
                    modifier           = Modifier.size(22.dp),
                )
            }

            // Title (centered)
            Text(
                text     = if (isNew) "New diary" else "Edit diary",
                style    = TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 17.sp,
                ),
                color    = textPrimary(dark),
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            // Save button
            Box(modifier = Modifier.width(64.dp), contentAlignment = Alignment.CenterEnd) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(20.dp),
                        color     = textPrimary(dark),
                        strokeWidth = 2.dp,
                    )
                } else {
                    TextButton(onClick = onSave) {
                        Text(
                            text  = "Save",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 15.sp,
                            ),
                            color = textPrimary(dark),
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = borderColor(dark), thickness = 1.dp)
    }
}

// ─── Bottom Toolbar ───────────────────────────────────────────────────────────

@Composable
private fun DiaryBottomToolbar(
    dark             : Boolean,
    wordCount        : Int,
    isLoadingLocation: Boolean,
    onImageClick     : () -> Unit,
    onLocationClick  : () -> Unit,
    onTtsClick       : () -> Unit,
) {
    Column {
        HorizontalDivider(
            color     = borderColor(dark),
            thickness = 1.dp,
            modifier  = Modifier.padding(horizontal = 0.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor(dark))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Image picker icon ──────────────────────────────────────────
            IconButton(onClick = onImageClick) {
                Icon(
                    painter            = painterResource(android.R.drawable.ic_menu_gallery),
                    contentDescription = "Add image",
                    tint               = toolbarIconTint(dark),
                    modifier           = Modifier.size(24.dp),
                )
            }

            // ── TTS icon (disabled) ────────────────────────────────────────
            IconButton(
                onClick  = onTtsClick,
                enabled  = false,           // implement later
            ) {
                Icon(
                    imageVector        = Icons.Default.PlayArrow,
                    contentDescription = "Text to speech (coming soon)",
                    tint               = toolbarIconTint(dark).copy(alpha = 0.3f),
                    modifier           = Modifier.size(24.dp),
                )
            }

            // ── Location icon ──────────────────────────────────────────────
            IconButton(onClick = onLocationClick) {
                if (isLoadingLocation) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(20.dp),
                        color     = toolbarIconTint(dark),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Default.LocationOn,
                        contentDescription = "Insert location",
                        tint               = toolbarIconTint(dark),
                        modifier           = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Word count ─────────────────────────────────────────────────
            Text(
                text  = "$wordCount word${if (wordCount == 1) "" else "s"}",
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 12.sp,
                ),
                color = textMuted(dark),
            )
        }
    }
}

// ─── Image Thumbnail ──────────────────────────────────────────────────────────

@Composable
private fun DiaryImageThumbnail(
    uri     : Uri,
    onRemove: () -> Unit,
) {
    Box(modifier = Modifier.size(96.dp)) {
        AsyncImage(
            model             = uri,
            contentDescription = null,
            contentScale      = ContentScale.Crop,
            modifier          = Modifier
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
                imageVector        = Icons.Default.Close,
                contentDescription = "Remove image",
                tint               = Color.White,
                modifier           = Modifier.size(12.dp),
            )
        }
    }
}
