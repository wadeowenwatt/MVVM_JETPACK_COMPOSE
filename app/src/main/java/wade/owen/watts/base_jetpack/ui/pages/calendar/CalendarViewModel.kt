package wade.owen.watts.base_jetpack.ui.pages.calendar

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.core.viewmodel.BaseViewModel
import wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus
import wade.owen.watts.base_jetpack.domain.repository.DiaryRepository
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for the Calendar screen.
 *
 * Responsibilities:
 * - Load all diary entries on init
 * - Handle month navigation (previous/next)
 * - Handle day selection and entry filtering
 * - Manage loading/error states
 *
 * Architecture:
 * - Extends BaseViewModel<CalendarUiState, CalendarUiEvent>
 * - Reactive repository pattern: getDiaries() returns Flow<List<Diary>>
 * - State updates via setState{} reducer pattern
 * - Events dispatched via sendEvent() for navigation/errors
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : BaseViewModel<CalendarUiState, CalendarUiEvent>(
    initialState = CalendarUiState()
) {
    init {
        observeAllDiaries()
    }

    /**
     * Observe all diary entries from the repository.
     * Called on init to load the initial data.
     */
    private fun observeAllDiaries() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loadStatus = LoadStatus.LOADING) }

            diaryRepository.getDiaries(limit = 100, offset = 0)
                .catch { exception ->
                    Log.e("CalendarViewModel", "Error loading diary data", exception)
                    setState { copy(loadStatus = LoadStatus.FAILURE) }
                    sendEvent(CalendarUiEvent.CalendarError("Error loading diary entries"))
                }
                .collect { diaries ->
                    setState {
                        copy(
                            loadStatus = LoadStatus.SUCCESS,
                            allEntries = diaries
                        )
                    }
                }
        }
    }

    /**
     * Navigate to a different month.
     * @param offset Number of months to navigate (positive = next, negative = previous)
     */
    fun navigateMonth(offset: Int) {
        val currentState = state.value
        var newMonth = currentState.currentMonth + offset
        var newYear = currentState.currentYear

        // Handle month wraparound
        when {
            newMonth < 0 -> {
                newMonth += 12
                newYear--
            }
            newMonth > 11 -> {
                newMonth -= 12
                newYear++
            }
        }

        setState {
            copy(
                currentMonth = newMonth,
                currentYear = newYear,
                selectedDate = 1  // Reset to first day when navigating months
            )
        }
    }

    /**
     * Select a specific day in the current month.
     * @param day Day of month (1-31)
     */
    fun selectDate(day: Int) {
        setState { copy(selectedDate = day) }
    }

    /**
     * Navigate to the current month (today).
     */
    fun goToToday() {
        val today = Calendar.getInstance()
        setState {
            copy(
                currentMonth = today.get(Calendar.MONTH),
                currentYear = today.get(Calendar.YEAR),
                selectedDate = today.get(Calendar.DAY_OF_MONTH)
            )
        }
    }

    /**
     * Navigate to DiaryDetail screen to create a new entry.
     */
    fun createNewEntry() {
        sendEvent(CalendarUiEvent.NavigateToDiaryDetail(diaryId = null))
    }

    /**
     * Navigate to DiaryDetail screen to view/edit an entry.
     * @param diaryId ID of the diary entry to open
     */
    fun navigateToDiaryDetail(diaryId: Int) {
        sendEvent(CalendarUiEvent.NavigateToDiaryDetail(diaryId = diaryId))
    }
}
