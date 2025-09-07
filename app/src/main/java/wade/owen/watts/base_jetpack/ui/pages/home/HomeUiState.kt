package wade.owen.watts.base_jetpack.ui.pages.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val quote: String = "",
    val errorMessage: String? = null,
)