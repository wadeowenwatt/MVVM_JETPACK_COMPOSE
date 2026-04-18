package wade.owen.watts.base_jetpack.ui.pages.calendar

import wade.owen.watts.base_jetpack.core.viewmodel.UiEvent

sealed class CalendarUiEvent : UiEvent {
    data class SelectDate(val day: Int) : CalendarUiEvent()
    data class NavigateMonth(val offset: Int) : CalendarUiEvent()
    data class CalendarError(val message: String) : CalendarUiEvent()
    data class NavigateToDiaryDetail(val diaryId: Int? = null) : CalendarUiEvent()
}
