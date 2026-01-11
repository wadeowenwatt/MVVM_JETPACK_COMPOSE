package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import wade.owen.watts.base_jetpack.domain.models.enums.LoadStatus

data class DiaryDetailUiState(
    val loadStatus: LoadStatus = LoadStatus.INITIAL,
    val title: String = "",
    val content: String = ""
)