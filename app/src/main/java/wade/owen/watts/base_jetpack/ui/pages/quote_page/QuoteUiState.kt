package wade.owen.watts.base_jetpack.ui.pages.quote_page

import wade.owen.watts.base_jetpack.core.viewmodel.UiState

data class QuoteUiState(
    val isLoading: Boolean = false,
    val quote: String = "",
    val errorMessage: String? = null,
): UiState