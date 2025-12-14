package wade.owen.watts.base_jetpack.ui.main

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import wade.owen.watts.base_jetpack.data.models.enum.AppTheme
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(
        MainUiState()
    )
    val uiState: StateFlow<MainUiState> get() = _uiState.asStateFlow()


    fun changeTheme(theme: AppTheme) {
        if (theme != _uiState.value.theme) {
            _uiState.value = _uiState.value.copy(theme)
        }
    }
}