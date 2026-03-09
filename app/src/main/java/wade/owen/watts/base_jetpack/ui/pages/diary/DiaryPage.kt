package wade.owen.watts.base_jetpack.ui.pages.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import wade.owen.watts.base_jetpack.core.designsystem.AppAlertDialog
import wade.owen.watts.base_jetpack.core.router.RootDestination
import wade.owen.watts.base_jetpack.domain.entities.Diary
import wade.owen.watts.base_jetpack.ui.theme.DiaryBackgroundDark
import wade.owen.watts.base_jetpack.ui.theme.DiaryBackgroundLight
import wade.owen.watts.base_jetpack.ui.theme.DiaryBorderDark
import wade.owen.watts.base_jetpack.ui.theme.DiaryBorderLight
import wade.owen.watts.base_jetpack.ui.theme.DiaryCardDark
import wade.owen.watts.base_jetpack.ui.theme.DiaryCardLight
import wade.owen.watts.base_jetpack.ui.theme.DiaryPrimary
import wade.owen.watts.base_jetpack.ui.theme.InterFontFamily
import wade.owen.watts.base_jetpack.ui.theme.Slate400
import wade.owen.watts.base_jetpack.ui.theme.Slate500
import wade.owen.watts.base_jetpack.ui.theme.Slate600
import wade.owen.watts.base_jetpack.ui.theme.Slate800
import java.text.SimpleDateFormat
import java.util.Locale

// ─── Helpers ──────────────────────────────────────────────────────────────────

/** True khi hệ thống đang ở dark mode */
@Composable
private fun isDark(): Boolean = isSystemInDarkTheme()

private fun bgColor(dark: Boolean)     = if (dark) DiaryBackgroundDark  else DiaryBackgroundLight
private fun cardColor(dark: Boolean)   = if (dark) DiaryCardDark         else DiaryCardLight
private fun borderColor(dark: Boolean) = if (dark) DiaryBorderDark       else DiaryBorderLight
private fun textPrimary(dark: Boolean) = if (dark) Color(0xFFF1F5F9)     else DiaryPrimary
private fun textMuted(dark: Boolean)   = if (dark) Slate400               else Slate500
private fun textBody(dark: Boolean)    = if (dark) Slate400               else Slate600
private fun searchBg(dark: Boolean)    = if (dark) Slate800               else Color(0xFFF1F5F9)
private fun searchIconColor(dark: Boolean) = if (dark) Color(0xFFCBD5E1)  else Slate600

// ─── DiaryPage ─────────────────────────────────────────────────────────────────

@Composable
fun DiaryPage(
    navController: NavHostController,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val dark = isDark()
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = bgColor(dark),
        topBar = {
            DiaryTopBar(
                dark         = dark,
                isSearchActive = uiState.isSearchActive,
                searchQuery  = uiState.searchQuery,
                onSearchToggle = { viewModel.toggleSearch() },
                onQueryChange  = { viewModel.updateSearchQuery(it) },
                onSearchDone   = { keyboard?.hide() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(RootDestination.createDiaryDetailRoute()) },
                shape = CircleShape,
                containerColor = if (dark) Color.White else DiaryPrimary,
                contentColor   = if (dark) DiaryPrimary else Color.White,
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                modifier = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Entry",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val list = uiState.filteredDiaries

            if (list.isEmpty()) {
                DiaryEmptyState(
                    dark       = dark,
                    isFiltered = uiState.isSearchActive && uiState.searchQuery.isNotBlank(),
                    modifier   = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(list, key = { it.id }) { diary ->
                        DiaryCard(
                            diary      = diary,
                            dark       = dark,
                            onEditClick = {
                                navController.navigate(
                                    RootDestination.createDiaryDetailRoute(diary.id)
                                )
                            },
                            onDeleteClick = { viewModel.showDeleteDialog(diary) }
                        )
                    }
                    // Khoảng trống cuối tránh bị FAB che
                    item { Spacer(Modifier.height(88.dp)) }
                }
            }
        }
    }

    // ── Delete confirmation dialog ──
    if (uiState.diaryPendingToDelete != null) {
        AppAlertDialog(
            title             = "Delete Entry",
            content           = "Are you sure you want to delete \"${uiState.diaryPendingToDelete!!.title}\"? This action cannot be undone.",
            onConfirm         = { viewModel.deleteDiary() },
            onDismissRequest  = { viewModel.dismissDeleteDialog() },
            titleButtonDismiss = "Cancel",
            titleButtonConfirm = "Delete",
        )
    }
}

// ─── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DiaryTopBar(
    dark           : Boolean,
    isSearchActive : Boolean,
    searchQuery    : String,
    onSearchToggle : () -> Unit,
    onQueryChange  : (String) -> Unit,
    onSearchDone   : () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor(dark))
    ) {
        // Title row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text  = "Diary",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 24.sp,
                        letterSpacing = (-0.3).sp
                    ),
                    color = textPrimary(dark)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text  = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                        .format(java.util.Date()),
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize   = 13.sp
                    ),
                    color = textMuted(dark)
                )
            }

            // Search toggle button (circle)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(searchBg(dark))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSearchToggle
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Default.Close
                    else Icons.Outlined.Search,
                    contentDescription = if (isSearchActive) "Close search" else "Search",
                    tint = searchIconColor(dark),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Animated search bar
        AnimatedVisibility(
            visible = isSearchActive,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(searchBg(dark))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = textMuted(dark),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.size(8.dp))
                BasicTextField(
                    value        = searchQuery,
                    onValueChange = onQueryChange,
                    modifier     = Modifier.weight(1f),
                    singleLine   = true,
                    textStyle    = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize   = 14.sp,
                        color      = textPrimary(dark)
                    ),
                    cursorBrush  = SolidColor(textPrimary(dark)),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchDone() }),
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text  = "Search entries...",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize   = 14.sp,
                                    color      = textMuted(dark)
                                )
                            )
                        }
                        inner()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    Spacer(Modifier.size(4.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = textMuted(dark),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onQueryChange("") }
                    )
                }
            }
        }

        HorizontalDivider(
            color     = borderColor(dark),
            thickness = 1.dp
        )
    }
}

// ─── Diary Card ────────────────────────────────────────────────────────────────

@Composable
fun DiaryCard(
    diary       : Diary,
    dark        : Boolean = false,
    onEditClick  : () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateLabel = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        .format(diary.createdDate)
        .uppercase(Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick),
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor(dark)),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = borderColor(dark)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation   = 1.dp,
            pressedElevation   = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // ── Row: [date + title column] | [edit + delete] ──
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: date label then title
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = dateLabel,
                        style = TextStyle(
                            fontFamily    = InterFontFamily,
                            fontWeight    = FontWeight.Bold,
                            fontSize      = 10.sp,
                            letterSpacing = 1.1.sp
                        ),
                        color = textMuted(dark)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = diary.title,
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 17.sp,
                            lineHeight  = 24.sp
                        ),
                        color    = textPrimary(dark),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Right: edit / delete icons
                Row(modifier = Modifier.padding(start = 8.dp)) {
                    IconButton(
                        onClick  = onEditClick,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint               = textMuted(dark),
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick  = onDeleteClick,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint               = Color(0xFFEF4444).copy(alpha = 0.8f),
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ── Content preview (3 lines max) ──
            if (diary.content.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text  = diary.content,
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize   = 13.sp,
                        lineHeight  = 20.sp
                    ),
                    color    = textBody(dark),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ─── Empty State ───────────────────────────────────────────────────────────────

@Composable
private fun DiaryEmptyState(
    dark       : Boolean,
    isFiltered : Boolean,
    modifier   : Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text     = if (isFiltered) "🔍" else "✍️",
                fontSize = 56.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text  = if (isFiltered) "No results found" else "No entries yet",
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 16.sp
                ),
                color = textPrimary(dark)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text  = if (isFiltered) "Try a different keyword"
                else "Tap + to write your first diary entry",
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 13.sp
                ),
                color = textMuted(dark)
            )
        }
    }
}

// ─── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun DiaryPagePreview() {
    DiaryPage(navController = rememberNavController())
}
