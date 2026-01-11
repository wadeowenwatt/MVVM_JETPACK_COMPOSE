package wade.owen.watts.base_jetpack.ui.main

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import wade.owen.watts.base_jetpack.data.models.enums.AppTheme
import wade.owen.watts.base_jetpack.global.ProvideMainViewModel
import wade.owen.watts.base_jetpack.ui.theme.Jetpack_compose_mvvmTheme
import wade.owen.watts.base_jetpack.utils.LocaleManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.setLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val isDarkTheme: Boolean? = when (uiState.theme) {
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
                else -> null
            }

            Jetpack_compose_mvvmTheme(
                dynamicColor = false,
                darkTheme = isDarkTheme ?: isSystemInDarkTheme(),
            ) {
                ProvideMainViewModel(viewModel) {
                    MainPage()
                }
            }
        }
    }
}