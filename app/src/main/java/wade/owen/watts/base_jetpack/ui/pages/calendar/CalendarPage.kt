@file:OptIn(ExperimentalMaterial3Api::class)

package wade.owen.watts.base_jetpack.ui.pages.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import wade.owen.watts.base_jetpack.core.router.RootDestination
import wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ─── Page ─────────────────────────────────────────────────────────────────────

@Composable
fun CalendarPage(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    // Handle events (errors, navigation)
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CalendarUiEvent.CalendarError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is CalendarUiEvent.NavigateToDiaryDetail -> {
                    event.diaryId?.let {
                        navController?.navigate(
                            RootDestination.createDiaryDetailRoute(it)
                        )
                    } ?: run {
                        navController?.navigate(RootDestination.createDiaryDetailRoute())
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Journal",
                        style = ty.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createNewEntry() },
                shape = CircleShape,
                containerColor = cs.primary,
                contentColor = cs.onPrimary,
                modifier = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Entry")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = cs.surface
    ) { innerPadding ->
        when (uiState.loadStatus) {
            LoadStatus.LOADING -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    HorizontalDivider(color = cs.onSurface.copy(alpha = 0.08f))

                    // ── Month Navigation ────────────────────────────────────────────
                    CalendarMonthHeader(
                        month = uiState.currentMonth,
                        year = uiState.currentYear,
                        onPreviousMonth = { viewModel.navigateMonth(-1) },
                        onNextMonth = { viewModel.navigateMonth(1) }
                    )

                    Spacer(Modifier.height(8.dp))

                    // ── Day labels (Sun-Sat) ────────────────────────────────────────
                    CalendarDayLabels()

                    Spacer(Modifier.height(8.dp))

                    // ── Calendar Grid with Entry Indicators ──────────────────────
                    CalendarGrid(
                        state = uiState,
                        onDayClick = { day -> viewModel.selectDate(day) }
                    )

                    Spacer(Modifier.height(24.dp))

                    HorizontalDivider(
                        color = cs.onSurface.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    // ── Entries for Selected Date ───────────────────────────────────
                    CalendarSelectedDateSection(
                        state = uiState,
                        onEntryClick = { diaryId ->
                            viewModel.navigateToDiaryDetail(diaryId)
                        }
                    )

                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
}

// ─── Calendar Month Header ─────────────────────────────────────────────────────

@Composable
private fun CalendarMonthHeader(
    month: Int,
    year: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    val monthNameFmt = SimpleDateFormat("MMMM", Locale.getDefault())
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val monthName = monthNameFmt.format(cal.time)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Month"
            )
        }
        Text(
            text = "$monthName $year",
            style = ty.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        IconButton(onClick = onNextMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Month"
            )
        }
    }
}

// ─── Calendar Day Labels ───────────────────────────────────────────────────────

@Composable
private fun CalendarDayLabels() {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = ty.labelMedium.copy(
                    color = cs.onSurface.copy(alpha = 0.45f),
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Calendar Grid ────────────────────────────────────────────────────────────

@Composable
private fun CalendarGrid(
    state: CalendarUiState,
    onDayClick: (Int) -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography
    val today = Calendar.getInstance()

    val totalCells = state.firstDayOfWeek + state.daysInMonth
    val rows = (totalCells + 6) / 7

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        repeat(rows) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val day = cellIndex - state.firstDayOfWeek + 1
                    val isCurrentMonth = day in 1..state.daysInMonth
                    val isSelected = isCurrentMonth && day == state.selectedDate
                    val isToday = isCurrentMonth &&
                            day == today.get(Calendar.DAY_OF_MONTH) &&
                            state.currentMonth == today.get(Calendar.MONTH) &&
                            state.currentYear == today.get(Calendar.YEAR)
                    val hasEntries = isCurrentMonth && state.hasEntriesForDay(day)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> cs.primary
                                    else -> Color.Transparent
                                }
                            )
                            .then(
                                if (isToday && !isSelected)
                                    Modifier.border(
                                        2.dp,
                                        cs.primary.copy(alpha = 0.5f),
                                        CircleShape
                                    ) else Modifier
                            )
                            .clickable(enabled = isCurrentMonth) {
                                if (isCurrentMonth) onDayClick(day)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCurrentMonth) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = ty.bodyMedium.copy(
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) cs.onPrimary
                                        else cs.onSurface
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                // Entry indicator dot
                                if (hasEntries) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(
                                                color = if (isSelected) cs.onPrimary else cs.primary,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Selected Date Section with Entries ────────────────────────────────────────

@Composable
private fun CalendarSelectedDateSection(
    state: CalendarUiState,
    onEntryClick: (Int) -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    val selectedDate = Calendar.getInstance().apply {
        set(Calendar.YEAR, state.currentYear)
        set(Calendar.MONTH, state.currentMonth)
        set(Calendar.DAY_OF_MONTH, state.selectedDate ?: 1)
    }
    val dateLabel = SimpleDateFormat(
        "EEE, MMM d, yyyy",
        Locale.getDefault()
    ).format(selectedDate.time)

    Text(
        text = "Entries for $dateLabel",
        style = ty.titleSmall.copy(
            fontWeight = FontWeight.SemiBold
        ),
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(Modifier.height(12.dp))

    // Show entries or empty state
    val entries = state.selectedDayEntries
    if (entries.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No entries for this date",
                style = ty.bodyMedium.copy(
                    color = cs.onSurface.copy(alpha = 0.6f)
                )
            )
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { /* TODO: trigger new entry creation */ }) {
                Text("Create one now")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            entries.forEach { diary ->
                CalendarEntryCard(
                    title = diary.title,
                    preview = diary.content,
                    onClick = { onEntryClick(diary.id) }
                )
            }
        }
    }
}

// ─── Entry Card Component ──────────────────────────────────────────────────────

@Composable
private fun CalendarEntryCard(
    title: String,
    preview: String,
    onClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cs.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = ty.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = preview,
                style = ty.bodySmall.copy(
                    color = cs.onSurface.copy(alpha = 0.6f)
                ),
                maxLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Read More →",
                style = ty.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = cs.primary
                )
            )
        }
    }
}
