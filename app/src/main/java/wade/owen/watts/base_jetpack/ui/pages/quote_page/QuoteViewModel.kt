package wade.owen.watts.base_jetpack.ui.pages.quote_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import wade.owen.watts.base_jetpack.data.repository.KanyeWestRepository
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val kanyeWestRepository: KanyeWestRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> get() = _uiState.asStateFlow()

    fun fetchRandomQuote() {
        viewModelScope.launch {
            val defer = async {
                kanyeWestRepository.getRandomQuote(
                    onStart = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    },
                    onComplete = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    },
                    onError = { errorMessage ->
                        _uiState.value =
                            _uiState.value.copy(errorMessage = errorMessage)
                    }
                ).collect { quoteEntity ->
                    _uiState.value =
                        _uiState.value.copy(quote = quoteEntity.quote)
                }
            }
            defer.cancel()
        }
    }
}