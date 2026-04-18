package wade.owen.watts.base_jetpack.ui.pages.calendar

import wade.owen.watts.base_jetpack.core.viewmodel.UiState
import wade.owen.watts.base_jetpack.domain.entities.Diary
import wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus
import java.util.Calendar

data class CalendarUiState(
    val loadStatus: LoadStatus = LoadStatus.INITIAL,
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedDate: Int? = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    val allEntries: List<Diary> = emptyList(),
) : UiState {
    // ── Computed Properties ──────────────────────────────────────────────────

    /**
     * Days in the currently displayed month.
     */
    val daysInMonth: Int
        get() {
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, currentYear)
                set(Calendar.MONTH, currentMonth)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

    /**
     * First day of week for the currently displayed month (0=Sunday, 1=Monday, ..., 6=Saturday).
     */
    val firstDayOfWeek: Int
        get() {
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, currentYear)
                set(Calendar.MONTH, currentMonth)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            return cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday
        }

    /**
     * Get entries for a specific day in the current month.
     */
    fun getEntriesForDay(day: Int): List<Diary> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val dayStart = cal.timeInMillis

        cal.add(Calendar.DAY_OF_MONTH, 1)
        val dayEnd = cal.timeInMillis

        return allEntries.filter {
            val entryTime = it.createdDate.time
            entryTime in dayStart until dayEnd
        }
    }

    /**
     * Get entries for the currently selected date.
     */
    val selectedDayEntries: List<Diary>
        get() = if (selectedDate != null) getEntriesForDay(selectedDate) else emptyList()

    /**
     * Get the count of entries for a specific day.
     */
    fun getEntryCountForDay(day: Int): Int = getEntriesForDay(day).size

    /**
     * Check if a day has any entries.
     */
    fun hasEntriesForDay(day: Int): Boolean = getEntryCountForDay(day) > 0
}
