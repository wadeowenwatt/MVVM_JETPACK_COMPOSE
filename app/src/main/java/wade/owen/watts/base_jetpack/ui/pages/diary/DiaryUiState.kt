package wade.owen.watts.base_jetpack.ui.pages.diary

import wade.owen.watts.base_jetpack.core.viewmodel.UiState
import wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus
import wade.owen.watts.base_jetpack.domain.entities.Diary

data class DiaryUiState(
    val loadStatus: LoadStatus = LoadStatus.INITIAL,
    val diaries: List<Diary> = emptyList(),
    val diaryPendingToDelete: Diary? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
) : UiState {
    val filteredDiaries: List<Diary>
        get() = if (searchQuery.isBlank()) diaries
        else diaries.filter { diary ->
            diary.title.contains(searchQuery, ignoreCase = true) ||
                diary.content.contains(searchQuery, ignoreCase = true)
        }
}