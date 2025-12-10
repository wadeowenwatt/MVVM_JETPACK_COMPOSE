package wade.owen.watts.base_jetpack.ui.main

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import wade.owen.watts.base_jetpack.data.models.enum.AppTheme
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _currentTheme: MutableState<AppTheme> = mutableStateOf(AppTheme.SYSTEM)
    val currentTheme: State<AppTheme> get() = _currentTheme

    fun changeTheme(theme: AppTheme) {
        _currentTheme.value = theme
    }
}