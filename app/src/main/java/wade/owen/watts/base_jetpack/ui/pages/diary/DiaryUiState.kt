package wade.owen.watts.base_jetpack.ui.pages.diary

import wade.owen.watts.base_jetpack.core.viewmodel.UiState
import wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus
import wade.owen.watts.base_jetpack.domain.entities.Diary

enum class SortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST,
    ALPHABETICAL
}

data class DiaryUiState(
    val loadStatus: LoadStatus = LoadStatus.INITIAL,
    val diaries: List<Diary> = emptyList(),
    val diaryPendingToDelete: Diary? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isRefreshing: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,
) : UiState {
    val filteredDiaries: List<Diary>
        get() {
            val filtered = if (searchQuery.isBlank()) diaries
            else diaries.filter { diary ->
                diary.title.contains(searchQuery, ignoreCase = true) ||
                    diary.content.contains(searchQuery, ignoreCase = true)
            }

            return when (sortOrder) {
                SortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.createdDate }
                SortOrder.OLDEST_FIRST -> filtered.sortedBy { it.createdDate }
                SortOrder.ALPHABETICAL -> filtered.sortedBy { it.title }
            }
        }
}