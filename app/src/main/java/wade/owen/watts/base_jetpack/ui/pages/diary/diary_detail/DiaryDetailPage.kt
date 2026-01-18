package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import wade.owen.watts.base_jetpack.core.designsystem.AppAlertDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: DiaryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is DiaryDetailEvent.NavigateBack -> {
                    navController.popBackStack()
                }

                is DiaryDetailEvent.DiaryDetailError -> {
                    // TODO: Show dialog
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (uiState.diaryId != null) "Edit Entry" else "New Entry") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.checkChangesAndDismiss() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveDiary()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                placeholder = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.titleLarge
            )
            HorizontalDivider()
            TextField(
                value = uiState.content,
                onValueChange = { viewModel.updateContent(it) },
                placeholder = { Text("Write your thoughts...") },
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        }
    }

    if (uiState.showDiscardDialog) {
        AppAlertDialog(
            title = "Unsaved Changes",
            content = "Do you want to discard changes?",
            onConfirm = {
                viewModel.confirmDiscard()
            },
            onDismissRequest = {
                viewModel.dismissDiscardDialog()
            },
            titleButtonConfirm = "Discard",
            titleButtonDismiss = "Keep Editing"
        )
    }
}