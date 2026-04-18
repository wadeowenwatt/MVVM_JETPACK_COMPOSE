package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import android.net.Uri
import wade.owen.watts.base_jetpack.core.viewmodel.UiState
import wade.owen.watts.base_jetpack.domain.entities.Diary
import wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus

data class DiaryDetailUiState(
    val loadStatus: LoadStatus = LoadStatus.INITIAL,
    val title: String = "",
    val content: String = "",           // plain-text mirror để lưu DB & đếm từ
    val diaryId: Int? = null,
    val showDiscardDialog: Boolean = false,
    val originalDiary: Diary? = null,
    // ── New features ────────────────────────────────────────────────────────────
    val imageUris: List<Uri> = emptyList(),    // ảnh chọn từ gallery
    val locationText: String? = null,          // địa chỉ sau khi reverse-geocode
    val isLoadingLocation: Boolean = false,    // đang fetch GPS
    val wordCount: Int = 0,                    // đếm số từ realtime
    val isViewMode: Boolean = false,           // true when viewing existing entry
    val shareText: String = "",                // formatted text for sharing
    // ── Delete features ─────────────────────────────────────────────────────────
    val isDeletingEntry: Boolean = false,      // loading state during deletion
    val showDeleteConfirmDialog: Boolean = false, // confirmation before delete
) : UiState
