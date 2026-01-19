package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.core.viewmodel.BaseViewModel
import wade.owen.watts.base_jetpack.domain.entities.Diary
import wade.owen.watts.base_jetpack.domain.entities.enums.LoadStatus
import wade.owen.watts.base_jetpack.domain.repository.DiaryRepository
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<DiaryDetailUiState, DiaryDetailEvent>(
    initialState = DiaryDetailUiState()
) {
    init {
        val diaryId: Int? = savedStateHandle.get<Int>("diary_id")
        if (diaryId != null && diaryId != -1) {
            loadDiary(diaryId)
        }
    }

    private fun loadDiary(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            diaryRepository.getDiaryById(id).collect { diary ->
                setState {
                    copy(
                        title = diary.title,
                        content = diary.content,
                        diaryId = diary.id,
                        originalDiary = diary
                    )
                }
            }
        }
    }

    fun updateTitle(title: String) {
        setState { copy(title = title) }
    }

    fun updateContent(content: String) {
        setState { copy(content = content) }
    }

    fun checkChangesAndDismiss() {
        val currentState = state.value
        val hasChanges = if (currentState.originalDiary != null) {
            currentState.title != currentState.originalDiary.title ||
                    currentState.content != currentState.originalDiary.content
        } else {
            currentState.title.isNotEmpty() || currentState.content.isNotEmpty()
        }

        if (hasChanges) {
            setState { copy(showDiscardDialog = true) }
        } else {
            sendEvent(DiaryDetailEvent.NavigateBack)
        }
    }

    fun dismissDiscardDialog() {
        setState { copy(showDiscardDialog = false) }
    }

    fun confirmDiscard() {
        sendEvent(DiaryDetailEvent.NavigateBack)
    }

    fun saveDiary() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loadStatus = LoadStatus.LOADING) }

            val currentState = state.value
            val currentDiary = currentState.originalDiary

            try {
                if (currentDiary != null) {
                    val updatedDiary = currentDiary.copy(
                        title = currentState.title,
                        content = currentState.content,
                        updatedDate = Date()
                    )
                    diaryRepository.updateDiary(updatedDiary)
                } else {
                    val newDiary = Diary(
                        title = currentState.title,
                        content = currentState.content,
                        createdDate = Date(),
                        updatedDate = Date()
                    )
                    diaryRepository.insertDiary(newDiary)
                }

                setState { copy(loadStatus = LoadStatus.SUCCESS) }
                sendEvent(DiaryDetailEvent.NavigateBack)
            } catch (e: Exception) {
                Log.e("Save diary failed", e.toString())
                setState { copy(loadStatus = LoadStatus.FAILURE) }
                sendEvent(DiaryDetailEvent.DiaryDetailError("Save diary failed"))
            }
        }
    }
}