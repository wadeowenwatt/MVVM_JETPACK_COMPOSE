package wade.owen.watts.base_jetpack.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import wade.owen.watts.base_jetpack.core.viewmodel.BaseViewModel
import wade.owen.watts.base_jetpack.domain.entities.enums.AppTheme
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() :
    BaseViewModel<MainUiState, MainUiEvent>(initialState = MainUiState()) {
    fun changeTheme(theme: AppTheme) {
        if (theme != state.value.theme) {
            setState { copy(theme = theme) }
        }
    }
}