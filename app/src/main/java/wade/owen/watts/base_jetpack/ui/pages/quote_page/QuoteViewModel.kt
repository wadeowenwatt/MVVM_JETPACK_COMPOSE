package wade.owen.watts.base_jetpack.ui.pages.quote_page

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.core.viewmodel.BaseViewModel
import wade.owen.watts.base_jetpack.domain.repository.RandomQuoteRepository
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val randomQuoteRepository: RandomQuoteRepository
) : BaseViewModel<QuoteUiState, QuoteUiEvent>(
    initialState = QuoteUiState()
) {
    init {
        fetchRandomQuote()
    }

    fun fetchRandomQuote() {
        viewModelScope.launch {
            randomQuoteRepository.getRandomQuote(onStart = {
                setState {
                    copy(
                        isLoading = true, errorMessage = null
                    )
                }
            }, onComplete = {
                setState { copy(isLoading = false) }
            }, onError = { errorMessage ->
                setState { copy(errorMessage = errorMessage) }
                sendEvent(QuoteUiEvent.QuoteError(errorMessage))
            }).collect { quoteEntity ->
                setState { copy(quote = quoteEntity.quote) }
            }
        }
    }
}