package wade.owen.watts.base_jetpack.ui.pages.home

sealed class HomeUiState {
    data class Success(val quoteString: String, val author: String): HomeUiState()
    object Loading: HomeUiState()
    object Error: HomeUiState()
}