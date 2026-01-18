package wade.owen.watts.base_jetpack.ui.pages.diary

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.core.viewmodel.BaseViewModel
import wade.owen.watts.base_jetpack.data.repository.DiaryRepository
import wade.owen.watts.base_jetpack.domain.models.Diary
import wade.owen.watts.base_jetpack.domain.models.enums.LoadStatus
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : BaseViewModel<DiaryUiState, DiaryUiEvent>(
    initialState = DiaryUiState()
) {
    init {
        observeListDiary()
    }

    private fun observeListDiary() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(loadStatus = LoadStatus.LOADING) }

            diaryRepository.getDiaries(20, 0).catch {
                Log.e("DiaryViewModel", "Error loading diary data", it)
                setState { copy(loadStatus = LoadStatus.FAILURE) }
                sendEvent(DiaryUiEvent.DiaryError("Error loading diary data"))
            }.collect { diaries ->
                setState {
                    copy(
                        loadStatus = LoadStatus.SUCCESS,
                        diaries = diaries
                    )
                }
            }
        }
    }

    fun showDeleteDialog(diary: Diary) {
        setState { copy(diaryPendingToDelete = diary) }
    }

    fun dismissDeleteDialog() {
        setState { copy(diaryPendingToDelete = null) }
    }

    fun deleteDiary() {
        val diaryToDelete = state.value.diaryPendingToDelete ?: return
        viewModelScope.launch(Dispatchers.IO) {
            diaryRepository.deleteDiary(diaryToDelete)

            setState {
                copy(
                    diaryPendingToDelete = null,
                    diaries = diaries
                )
            }
        }
    }
}