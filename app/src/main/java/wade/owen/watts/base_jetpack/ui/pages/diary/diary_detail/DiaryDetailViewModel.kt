package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.core.viewmodel.BaseViewModel
import wade.owen.watts.base_jetpack.data.repository.DiaryRepository
import wade.owen.watts.base_jetpack.domain.models.Diary
import wade.owen.watts.base_jetpack.domain.models.enums.LoadStatus
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : BaseViewModel<DiaryDetailUiState, DiaryDetailEvent>(
    initialState = DiaryDetailUiState()
) {
    fun updateTitle(title: String) {
        setState { copy(title = title) }
    }

    fun updateContent(content: String) {
        setState { copy(content = content) }
    }

    fun createNewDiary() {
        viewModelScope.launch(Dispatchers.IO) {
            setState {
                copy(
                    loadStatus = LoadStatus.LOADING
                )
            }

            val currentState = state.value
            val newDiary = Diary(
                title = currentState.title,
                content = currentState.content,
                createdDate = Date(),
                updatedDate = Date()
            )

            try {
                diaryRepository.insertDiary(newDiary)
                setState {
                    copy(
                        loadStatus = LoadStatus.SUCCESS
                    )
                }
                sendEvent(DiaryDetailEvent.NavigateBack)
            } catch (e: Exception) {
                Log.e("Create diary failed", e.toString())
                setState {
                    copy(
                        loadStatus = LoadStatus.FAILURE
                    )
                }
                sendEvent(DiaryDetailEvent.DiaryDetailError("Create diary failed"))
            }
        }
    }
}