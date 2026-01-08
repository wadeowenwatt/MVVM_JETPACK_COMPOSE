package wade.owen.watts.base_jetpack.ui.pages.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.data.models.enums.LoadStatus
import wade.owen.watts.base_jetpack.data.repository.DiaryRepository
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<DiaryUiState> = MutableStateFlow(
        DiaryUiState()
    )
    val uiState: StateFlow<DiaryUiState> get() = _uiState.asStateFlow()

    fun loadDiaryData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value =
                _uiState.value.copy(loadStatus = LoadStatus.LOADING)
            try {
                // Fetching all diaries with arbitrary limit/offset for now as per requirement just 'loadDiary'
                val result = diaryRepository.getDiaries(100, 0)
                _uiState.value = _uiState.value.copy(
                    loadStatus = LoadStatus.SUCCESS,
                    diaries = result
                )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(loadStatus = LoadStatus.FAILURE)
            }
        }
    }
}