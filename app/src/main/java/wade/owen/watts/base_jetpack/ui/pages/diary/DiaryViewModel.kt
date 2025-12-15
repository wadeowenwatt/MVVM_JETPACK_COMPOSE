package wade.owen.watts.base_jetpack.ui.pages.diary

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor() : ViewModel() {
    private val _uiState: MutableStateFlow<DiaryUiState> = MutableStateFlow(
        DiaryUiState()
    )
    val uiState: StateFlow<DiaryUiState> get() = _uiState.asStateFlow()

    fun loadDiaryData() {

    }

}