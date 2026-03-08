package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import wade.owen.watts.base_jetpack.core.designsystem.AppAlertDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: DiaryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val colorScheme = MaterialTheme.colorScheme
    val isNewEntry = uiState.diaryId == null

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is DiaryDetailEvent.NavigateBack -> navController.popBackStack()
                is DiaryDetailEvent.DiaryDetailError -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isNewEntry) "New Entry" else "Edit Entry",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.checkChangesAndDismiss() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveDiary() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.primary
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.08f))

            // Date label
            Text(
                text = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                    .format(Date()).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = colorScheme.onSurface.copy(alpha = 0.45f),
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            // Title field
            TextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                placeholder = {
                    Text(
                        "Entry Title",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = colorScheme.onSurface.copy(alpha = 0.3f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                singleLine = false,
                maxLines = 3
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = colorScheme.onSurface.copy(alpha = 0.08f)
            )

            Spacer(Modifier.height(4.dp))

            // Content field
            TextField(
                value = uiState.content,
                onValueChange = { viewModel.updateContent(it) },
                placeholder = {
                    Text(
                        "How was your day?",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 28.sp
                )
            )
        }
    }

    if (uiState.showDiscardDialog) {
        AppAlertDialog(
            title = "Discard Changes?",
            content = "Your unsaved changes will be lost. Do you want to continue?",
            onConfirm = { viewModel.confirmDiscard() },
            onDismissRequest = { viewModel.dismissDiscardDialog() },
            titleButtonConfirm = "Discard",
            titleButtonDismiss = "Keep Editing"
        )
    }
}