package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import wade.owen.watts.base_jetpack.core.viewmodel.UiState
import wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus

import wade.owen.watts.base_jetpack.domain.entities.Diary

data class DiaryDetailUiState(
    val loadStatus: LoadStatus = LoadStatus.INITIAL,
    val title: String = "",
    val content: String = "",
    val diaryId: Int? = null,
    val showDiscardDialog: Boolean = false,
    val originalDiary: Diary? = null
) : UiState