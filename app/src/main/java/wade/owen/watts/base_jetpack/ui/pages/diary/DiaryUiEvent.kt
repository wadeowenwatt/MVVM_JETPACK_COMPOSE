package wade.owen.watts.base_jetpack.ui.pages.diary

import wade.owen.watts.base_jetpack.core.viewmodel.UiEvent

sealed class DiaryUiEvent: UiEvent {
    data class DiaryError(val message: String): DiaryUiEvent()
}