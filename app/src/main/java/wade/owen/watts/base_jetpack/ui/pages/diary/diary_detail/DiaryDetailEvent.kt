package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import wade.owen.watts.base_jetpack.core.viewmodel.UiEvent

sealed class DiaryDetailEvent : UiEvent {
    object NavigateBack : DiaryDetailEvent()
    data class DiaryDetailError(val message: String) : DiaryDetailEvent()
}