package wade.owen.watts.base_jetpack.ui.pages.diary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.domain.models.enums.LoadStatus
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

    init {
        observeListDiary()
    }

    private fun observeListDiary() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value =
                _uiState.value.copy(loadStatus = LoadStatus.LOADING)

            diaryRepository.getDiaries(20, 0).catch {
                Log.e("DiaryViewModel", "Error loading diary data", it)
                _uiState.value =
                    _uiState.value.copy(loadStatus = LoadStatus.FAILURE)
            }.collect { diaries ->
                _uiState.value = _uiState.value.copy(
                    loadStatus = LoadStatus.SUCCESS,
                    diaries = diaries
                )
            }
        }
    }
}