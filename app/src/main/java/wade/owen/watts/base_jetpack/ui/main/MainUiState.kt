package wade.owen.watts.base_jetpack.ui.main

import wade.owen.watts.base_jetpack.data.models.enum.AppTheme

data class MainUiState (
    val theme: AppTheme = AppTheme.SYSTEM
)