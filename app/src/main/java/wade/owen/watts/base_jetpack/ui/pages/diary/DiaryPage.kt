package wade.owen.watts.base_jetpack.ui.pages.diary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryPage(
    navController: NavHostController,
    viewModel: DiaryViewModel = hiltViewModel<DiaryViewModel>()
) {
    val uiState = viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {}

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Diary",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                                .format(java.util.Date()),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(RootDestination.createDiaryDetailRoute())
                },
                shape = CircleShape,
                containerColor = colorScheme.onSurface,
                contentColor = colorScheme.surface,
                modifier = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Entry")
            }
        },
        containerColor = colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.08f))

            if (uiState.value.diaries.isEmpty()) {
                EmptyDiaryState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.value.diaries, key = { it.id }) { diary ->
                        DiaryCard(
                            diary = diary,
                            onEditClick = {
                                navController.navigate(
                                    RootDestination.createDiaryDetailRoute(diary.id)
                                )
                            },
                            onDeleteClick = {
                                viewModel.showDeleteDialog(diary)
                            }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (uiState.value.diaryPendingToDelete != null) {
        AppAlertDialog(
            title = "Delete Entry",
            content = "Are you sure you want to delete this diary entry? This action cannot be undone.",
            onConfirm = { viewModel.deleteDiary() },
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            titleButtonDismiss = "Cancel",
            titleButtonConfirm = "Delete",
        )
    }
}

@Composable
private fun EmptyDiaryState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "✍️",
                fontSize = 56.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No entries yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tap + to write your first diary entry",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun DiaryCard(
    diary: Diary,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val dateLabel = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        .format(diary.createdDate).uppercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = colorScheme.onSurface.copy(alpha = 0.45f),
                        letterSpacing = 0.8.sp
                    )
                )
                Row {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp),
                            tint = colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp),
                            tint = colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = diary.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (diary.content.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = diary.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryPagePreview() {
    DiaryPage(navController = rememberNavController())
}