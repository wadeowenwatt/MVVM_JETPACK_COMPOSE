package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import android.net.Uri
import wade.owen.watts.base_jetpack.core.viewmodel.UiEvent

sealed class DiaryDetailEvent : UiEvent {
    object NavigateBack : DiaryDetailEvent()
    data class DiaryDetailError(val message: String) : DiaryDetailEvent()
    data class LocationInserted(val address: String) : DiaryDetailEvent()
    data class ImagePicked(val uri: Uri) : DiaryDetailEvent()
    data class ShareDiary(val title: String, val content: String) : DiaryDetailEvent()
    object DiaryDeleted : DiaryDetailEvent()
    data class ShowUndoSnackbar(val message: String) : DiaryDetailEvent()
}
