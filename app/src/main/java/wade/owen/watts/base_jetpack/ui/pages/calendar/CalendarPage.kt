package wade.owen.watts.base_jetpack.ui.pages.calendar

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.core.router.RootDestination
import wade.owen.watts.base_jetpack.domain.entities.Diary
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

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CalendarUiEvent.CalendarError ->
                    snackbarHostState.showSnackbar(event.message)
                is CalendarUiEvent.NavigateToDiaryDetail -> {
                    event.diaryId?.let {
                        navController?.navigate(RootDestination.createDiaryDetailRoute(it))
                    } ?: navController?.navigate(RootDestination.createDiaryDetailRoute())
                }
                else -> {}
            }
        }
    }

    Scaffold(
        containerColor = cs.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createNewEntry() },
                shape = CircleShape,
                containerColor = cs.onBackground,
                contentColor = cs.background,
                modifier = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Entry",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                val selectedDayNumber = uiState.selectedDate
                val dateLabel = selectedDayNumber?.let {
                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, uiState.currentYear)
                        set(Calendar.MONTH, uiState.currentMonth)
                        set(Calendar.DAY_OF_MONTH, it)
                    }
                    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(cal.time)
                }

                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // ── Header ─────────────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "My Journal",
                            style = ty.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = cs.onBackground
                        )
                    }

                    // ── Content area (gap 24dp between all items) ──────────────
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // ── Calendar Card ───────────────────────────────────────
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cs.surface),
                            border = BorderStroke(1.dp, cs.outline),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 20.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CalendarMonthHeader(
                                    month = uiState.currentMonth,
                                    year = uiState.currentYear,
                                    onPreviousMonth = { viewModel.navigateMonth(-1) },
                                    onNextMonth = { viewModel.navigateMonth(1) }
                                )
                                CalendarDayLabels()
                                CalendarGrid(
                                    state = uiState,
                                    onDayClick = { day -> viewModel.selectDate(day) }
                                )
                            }
                        }

                        // ── Entries section ─────────────────────────────────────
                        if (dateLabel != null) {
                            Text(
                                text = "Entries for $dateLabel",
                                style = ty.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = cs.onBackground
                            )

                            val entries = uiState.selectedDayEntries
                            if (entries.isEmpty()) {
                                Text(
                                    text = "No entries for this date",
                                    style = ty.bodyMedium,
                                    color = cs.onSurfaceVariant
                                )
                            } else {
                                entries.forEach { diary ->
                                    CalendarEntryCard(
                                        diary = diary,
                                        onClick = { viewModel.navigateToDiaryDetail(diary.id) }
                                    )
                                }
                            }
                        }
                    }

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

    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = "Previous Month",
                tint = cs.onBackground,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = "$monthName $year",
            style = ty.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            color = cs.onBackground
        )
        IconButton(onClick = onNextMonth) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = "Next Month",
                tint = cs.onBackground,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── Calendar Day Labels ───────────────────────────────────────────────────────

@Composable
private fun CalendarDayLabels() {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = ty.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = cs.onSurfaceVariant,
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

    Column {
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
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
                                if (isSelected) cs.onBackground else Color.Transparent
                            )
                            .then(
                                if (isToday && !isSelected)
                                    Modifier.border(2.dp, cs.onBackground, CircleShape)
                                else Modifier
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
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold
                                                     else FontWeight.Normal
                                    ),
                                    color = if (isSelected) cs.background else cs.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                if (hasEntries) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(
                                                color = if (isSelected) cs.background
                                                        else cs.onBackground,
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

// ─── Entry Card ───────────────────────────────────────────────────────────────

@Composable
private fun CalendarEntryCard(
    diary: Diary,
    onClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    val dateStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(diary.createdDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, cs.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateStr,
                        style = ty.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = cs.onSurfaceVariant
                    )
                    Text(
                        text = diary.title,
                        style = ty.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = cs.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (diary.content.isNotBlank()) {
                    Text(
                        text = diary.content,
                        style = ty.bodySmall.copy(
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        ),
                        color = cs.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = cs.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
