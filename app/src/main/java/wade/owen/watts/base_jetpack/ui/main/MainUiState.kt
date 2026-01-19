package wade.owen.watts.base_jetpack.ui.main

import wade.owen.watts.base_jetpack.core.viewmodel.UiState
import wade.owen.watts.base_jetpack.domain.entities.enums.AppTheme

data class MainUiState(
    val theme: AppTheme = AppTheme.SYSTEM
): UiState