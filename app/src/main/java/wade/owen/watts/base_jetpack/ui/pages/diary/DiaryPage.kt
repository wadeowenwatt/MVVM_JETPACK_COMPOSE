package wade.owen.watts.base_jetpack.ui.pages.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ─── DiaryPage ─────────────────────────────────────────────────────────────────

@Composable
fun DiaryPage(
    navController: NavHostController,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                DiaryTopBar(
                    isSearchActive = uiState.isSearchActive,
                    searchQuery = uiState.searchQuery,
                    onSearchToggle = { viewModel.toggleSearch() },
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearchDone = { keyboard?.hide() }
                )
                DiarySortBar(
                    sortOrder = uiState.sortOrder,
                    onSortOrderChange = { viewModel.setSortOrder(it) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(RootDestination.createDiaryDetailRoute()) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
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
                    isFiltered = uiState.isSearchActive && uiState.searchQuery.isNotBlank(),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Group entries by date
                val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                val groupedEntries = list.groupBy { diary ->
                    dateFormat.format(diary.createdDate)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groupedEntries.forEach { (date, entries) ->
                        item {
                            DiaryDateHeader(dateString = date)
                        }
                        items(entries, key = { it.id }) { diary ->
                            DiaryCard(
                                diary = diary,
                                onEditClick = {
                                    navController.navigate(
                                        RootDestination.createDiaryDetailRoute(diary.id)
                                    )
                                },
                                onDeleteClick = { viewModel.showDeleteDialog(diary) }
                            )
                        }
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
            title = "Delete Entry",
            content = "Are you sure you want to delete \"${uiState.diaryPendingToDelete!!.title}\"? This action cannot be undone.",
            onConfirm = { viewModel.deleteDiary() },
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            titleButtonDismiss = "Cancel",
            titleButtonConfirm = "Delete",
        )
    }
}

// ─── Top Bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DiaryTopBar(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchToggle: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchDone: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.background)
    ) {
        // Title row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Diary",
                    style = ty.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = cs.onBackground
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                        .format(java.util.Date()),
                    style = ty.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = cs.onSurfaceVariant
                )
            }

            // Search toggle button (circle)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(cs.surfaceVariant)
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
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Animated search bar
        AnimatedVisibility(
            visible = isSearchActive,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cs.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.size(8.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = ty.bodyMedium.copy(color = cs.onBackground),
                    cursorBrush = SolidColor(cs.onBackground),
                    keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchDone() }),
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search entries...",
                                style = ty.bodyMedium.copy(color = cs.onSurfaceVariant)
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
                        tint = cs.onSurfaceVariant,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onQueryChange("") }
                    )
                }
            }
        }

        HorizontalDivider(
            color = cs.outline,
            thickness = 1.dp
        )
    }
}

// ─── Sort Options Bar ──────────────────────────────────────────────────────────

@Composable
private fun DiarySortBar(
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    var isDropdownOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sort",
                style = ty.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = cs.onSurfaceVariant
            )

            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(cs.surfaceVariant)
                        .clickable { isDropdownOpen = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (sortOrder) {
                            SortOrder.NEWEST_FIRST -> "Newest First"
                            SortOrder.OLDEST_FIRST -> "Oldest First"
                            SortOrder.ALPHABETICAL -> "A - Z"
                        },
                        style = ty.labelSmall,
                        color = cs.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = isDropdownOpen,
                    onDismissRequest = { isDropdownOpen = false },
                    modifier = Modifier.background(cs.surface)
                ) {
                    SortOrder.values().forEach { order ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = when (order) {
                                        SortOrder.NEWEST_FIRST -> "Newest First"
                                        SortOrder.OLDEST_FIRST -> "Oldest First"
                                        SortOrder.ALPHABETICAL -> "A - Z"
                                    },
                                    style = ty.labelSmall
                                )
                            },
                            onClick = {
                                onSortOrderChange(order)
                                isDropdownOpen = false
                            }
                        )
                    }
                }
            }
        }
    }
}

// ─── Diary Card ────────────────────────────────────────────────────────────────

@Composable
private fun DiaryDateHeader(
    dateString: String,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Text(
        text = dateString.uppercase(Locale.getDefault()),
        style = ty.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        ),
        color = cs.onSurface,
        modifier = modifier.padding(
            top = 16.dp,
            bottom = 8.dp,
            start = 0.dp,
            end = 0.dp
        )
    )
}

// ─── Diary Card ────────────────────────────────────────────────────────────────

@Composable
fun DiaryCard(
    diary: Diary,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    val dateLabel = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        .format(diary.createdDate)
        .uppercase(Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = cs.outline
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // ── Row: [date + title column] | [edit + delete] ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: date label then title
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dateLabel,
                        style = ty.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        ),
                        color = cs.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = diary.title,
                        style = ty.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            lineHeight = 24.sp
                        ),
                        color = cs.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Right: edit / delete icons
                Row(modifier = Modifier.padding(start = 8.dp)) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = cs.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ── Content preview (3 lines max) ──
            if (diary.content.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = diary.content,
                    style = ty.bodySmall.copy(lineHeight = 20.sp),
                    color = cs.secondary,
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
    isFiltered: Boolean,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isFiltered) "🔍" else "✍️",
                fontSize = 56.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isFiltered) "No results found" else "No entries yet",
                style = ty.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = cs.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (isFiltered) "Try a different keyword"
                else "Tap + to write your first diary entry",
                style = ty.bodySmall,
                color = cs.onSurfaceVariant
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
