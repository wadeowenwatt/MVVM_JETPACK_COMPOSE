package wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.data.repository.DiaryRepository
import wade.owen.watts.base_jetpack.domain.models.Diary
import wade.owen.watts.base_jetpack.domain.models.enums.LoadStatus
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

    fun createNewDiary(navController: NavController) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(
                loadStatus = LoadStatus.LOADING
            )

            val currentState = _uiState.value
            val newDiary = Diary(
                title = currentState.title,
                content = currentState.content,
                createdDate = Date(),
                updatedDate = Date()
            )

            try {
                diaryRepository.insertDiary(newDiary)
                _uiState.value.copy(
                    loadStatus = LoadStatus.SUCCESS
                )
                navController.popBackStack()
            } catch (e: Exception) {
                Log.e("Create diary failed", e.toString())
                _uiState.value.copy(
                    loadStatus = LoadStatus.FAILURE
                )
            }
        }
    }
}