package wade.owen.watts.base_jetpack.ui.pages.diary

import wade.owen.watts.base_jetpack.data.models.enums.LoadStatus


data class DiaryUiState(
    val loadStatus: LoadStatus = LoadStatus.INITIAL
)