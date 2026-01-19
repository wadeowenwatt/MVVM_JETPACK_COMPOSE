package wade.owen.watts.base_jetpack.ui.pages.quote_page

import wade.owen.watts.base_jetpack.core.viewmodel.UiEvent

sealed class QuoteUiEvent : UiEvent {
    data class QuoteError(val errorMessage: String?) : QuoteUiEvent()
}