package wade.owen.watts.base_jetpack.global

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import wade.owen.watts.base_jetpack.ui.main.MainViewModel

val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> {
    error("MainViewModel not provided")
}

@Composable
fun ProvideMainViewModel(
    mainViewModel: MainViewModel,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalMainViewModel provides mainViewModel) {
        content()
    }
}