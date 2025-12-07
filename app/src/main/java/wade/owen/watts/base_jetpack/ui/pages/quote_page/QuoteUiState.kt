package wade.owen.watts.base_jetpack.ui.pages.quote_page

data class QuoteUiState(
    val isLoading: Boolean = false,
    val quote: String = "",
    val errorMessage: String? = null,
)