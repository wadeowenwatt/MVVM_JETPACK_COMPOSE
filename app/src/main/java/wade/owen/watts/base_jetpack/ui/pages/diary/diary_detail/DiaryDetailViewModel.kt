package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.data.repository.DiaryRepository
import wade.owen.watts.base_jetpack.domain.models.Diary
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiaryDetailUiState())
    val uiState: StateFlow<DiaryDetailUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun createNewDiary() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = _uiState.value
            val newDiary = Diary(
                title = currentState.title,
                content = currentState.content,
                createdDate = Date(),
                updatedDate = Date()
            )
            diaryRepository.insertDiary(newDiary)
        }
    }

    // TODO: To be implemented for edit mode
    fun updateDiary() {
    }
}