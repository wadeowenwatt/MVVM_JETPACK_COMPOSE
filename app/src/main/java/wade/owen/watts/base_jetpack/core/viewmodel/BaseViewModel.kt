package wade.owen.watts.base_jetpack.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface UiState
interface UiEvent

abstract class BaseViewModel<S : UiState, E : UiEvent>(
    initialState: S
) : ViewModel() {
    // ------------- STATE --------------
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    protected fun setState(reducer: S.() -> S) {
        _state.value = _state.value.reducer()
    }

    // -------------- EVENT ---------------
    private val _event = MutableSharedFlow<E>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val event = _event.asSharedFlow()

    protected fun sendEvent(event: E) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _event.emit(event)
        }
    }
}