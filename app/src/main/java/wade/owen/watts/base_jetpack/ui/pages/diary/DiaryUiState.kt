package wade.owen.watts.base_jetpack.ui.pages.diary

import wade.owen.watts.base_jetpack.core.viewmodel.UiState
import wade.owen.watts.base_jetpack.domain.models.enums.LoadStatus


import wade.owen.watts.base_jetpack.domain.models.Diary

data class DiaryUiState(
    val loadStatus: LoadStatus = LoadStatus.INITIAL,
    val diaries: List<Diary> = emptyList(),
    val diaryPendingToDelete: Diary? = null,
): UiState