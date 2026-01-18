package wade.owen.watts.base_jetpack.ui.pages.diary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.core.designsystem.AppAlertDialog
import wade.owen.watts.base_jetpack.core.designsystem.AppIconButton
import wade.owen.watts.base_jetpack.domain.models.Diary
import wade.owen.watts.base_jetpack.router.RootDestination
import wade.owen.watts.base_jetpack.ui.commons.AppHeader
import wade.owen.watts.base_jetpack.utils.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DiaryPage(
    navController: NavHostController,
    viewModel: DiaryViewModel = hiltViewModel<DiaryViewModel>()
) {
    val uiState = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {

    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(RootDestination.DIARY_DETAIL)
                },
                shape = CircleShape,
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "Add",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        },
    ) { innerPadding ->
        Column {
            AppHeader(
                Modifier.padding(innerPadding)
            )
            LazyColumn() {
                items(uiState.value.diaries.size) { index ->
                    val diary = uiState.value.diaries[index]

                    DiaryItem(
                        Modifier.padding(16.dp),
                        diary = diary,
                        onEditClick = {

                        },
                        onDeleteClick = {
                            // Show dialog confirm delete
                            viewModel.showDeleteDialog(diary)
                        },
                    )
                }
            }
        }
    }

    if (uiState.value.diaryPendingToDelete != null) {
        AppAlertDialog(
            title = "Delete Diary",
            content = "Are you sure you want to delete this diary?",
            onConfirm = {
                viewModel.deleteDiary()
            },
            onDismissRequest = {
                viewModel.dismissDeleteDialog()
            }
        )
    }

}

@Composable
fun DiaryItem(
    modifier: Modifier = Modifier,
    diary: Diary,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val colorTheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = colorTheme.secondary.copy(alpha = 0.1f)
                ),
            )
            .background(
                colorTheme.primary, shape = RoundedCornerShape(16.dp)
            )
            .fillMaxSize()
            .height(140.dp)
    ) {
        Column(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(diary.title)
                    Text(
                        SimpleDateFormat(
                            DateTimeFormat.PATTERN_MONTH_DATE_YEAR,
                            Locale.getDefault()
                        ).format(diary.createdDate)
                    )
                }
                Row {
                    ActionButtonDiaryItem(
                        Modifier.padding(8.dp),
                        painterResource = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit Button",
                        onClick = onEditClick
                    )
                    ActionButtonDiaryItem(
                        Modifier.padding(8.dp),
                        painterResource = painterResource(R.drawable.ic_recycle_bin),
                        contentDescription = "Delete Button",
                        onClick = onDeleteClick
                    )
                }
            }
            Text(
                diary.content,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ActionButtonDiaryItem(
    modifier: Modifier = Modifier,
    painterResource: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) {
    AppIconButton(
        modifier = modifier
            .width(16.dp)
            .height(16.dp),
        painterResource = painterResource,
        contentDescription = contentDescription,
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
fun DiaryPagePreview() {
    DiaryPage(navController = rememberNavController())
}