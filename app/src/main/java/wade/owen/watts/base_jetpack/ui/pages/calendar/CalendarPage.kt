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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import wade.owen.watts.base_jetpack.core.router.RootDestination
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val today = Calendar.getInstance()
    var displayedMonth by remember { mutableStateOf(today.get(Calendar.MONTH)) }
    var displayedYear by remember { mutableStateOf(today.get(Calendar.YEAR)) }
    var selectedDay by remember { mutableStateOf(today.get(Calendar.DAY_OF_MONTH)) }

    val monthNameFmt = java.text.SimpleDateFormat("MMMM", java.util.Locale.getDefault())
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, displayedYear)
        set(Calendar.MONTH, displayedMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val monthName = monthNameFmt.format(cal.time)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Journal",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController?.navigate(RootDestination.createDiaryDetailRoute()) },
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
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.08f))

            // Month Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (displayedMonth == 0) {
                        displayedMonth = 11; displayedYear--
                    } else displayedMonth--
                    selectedDay = 1
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month"
                    )
                }
                Text(
                    text = "$monthName $displayedYear",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                IconButton(onClick = {
                    if (displayedMonth == 11) {
                        displayedMonth = 0; displayedYear++
                    } else displayedMonth++
                    selectedDay = 1
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month"
                    )
                }
            }

            // Day labels
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
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colorScheme.onSurface.copy(alpha = 0.45f),
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Calendar grid
            val totalCells = firstDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                repeat(rows) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(7) { col ->
                            val cellIndex = row * 7 + col
                            val day = cellIndex - firstDayOfWeek + 1
                            val isCurrentMonth = day in 1..daysInMonth
                            val isSelected = isCurrentMonth && day == selectedDay
                            val isToday = isCurrentMonth &&
                                    day == today.get(Calendar.DAY_OF_MONTH) &&
                                    displayedMonth == today.get(Calendar.MONTH) &&
                                    displayedYear == today.get(Calendar.YEAR)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(3.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> colorScheme.onSurface
                                            else -> Color.Transparent
                                        }
                                    )
                                    .then(
                                        if (isToday && !isSelected)
                                            Modifier.border(
                                                1.dp,
                                                colorScheme.onSurface.copy(alpha = 0.3f),
                                                CircleShape
                                            ) else Modifier
                                    )
                                    .clickable(enabled = isCurrentMonth) {
                                        if (isCurrentMonth) selectedDay = day
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCurrentMonth) {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) colorScheme.surface
                                            else colorScheme.onSurface
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Divider before entry section
            HorizontalDivider(
                color = colorScheme.onSurface.copy(alpha = 0.08f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Entries for selected date
            val selectedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, displayedYear)
                set(Calendar.MONTH, displayedMonth)
                set(Calendar.DAY_OF_MONTH, selectedDay)
            }
            val dateLabel = java.text.SimpleDateFormat(
                "EEE, MMM d, yyyy",
                java.util.Locale.getDefault()
            ).format(selectedDate.time)

            Text(
                text = "Entries for $dateLabel",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Placeholder entry cards for selected date
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CalendarEntryCard(
                    title = "Morning Reflection",
                    preview = "Spent some quiet time this morning thinking about my goals..."
                )
                CalendarEntryCard(
                    title = "Evening Walk",
                    preview = "A beautiful sunset stroll through the park, catching the last bits..."
                )
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun CalendarEntryCard(title: String, preview: String) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = preview,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                maxLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Read More →",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.primary
                )
            )
        }
    }
}