package wade.owen.watts.base_jetpack.ui.main

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import wade.owen.watts.base_jetpack.data.models.enum.AppTheme
import wade.owen.watts.base_jetpack.router.AppNavHost
import wade.owen.watts.base_jetpack.router.Destination
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
            val isDarkTheme: Boolean? = when (viewModel.currentTheme.value) {
                AppTheme.DARK -> true
                AppTheme.LIGHT -> false
                else -> null
            }
            Jetpack_compose_mvvmTheme(
                dynamicColor = false,
                darkTheme = isDarkTheme ?: isSystemInDarkTheme(),
            ) {
                val navController = rememberNavController()
                val startDestination = Destination.DIARY
                var selectedDestination by rememberSaveable {
                    mutableIntStateOf(
                        startDestination.ordinal
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(), bottomBar = {
                        NavigationBar(
                            windowInsets = NavigationBarDefaults.windowInsets,
                            containerColor = MaterialTheme.colorScheme.primary,
                        ) {
                            Destination.entries.forEachIndexed { index, destination ->
                                NavigationBarItem(
                                    selected = selectedDestination == index,
                                    onClick = {
                                        navController.navigate(route = destination.route)
                                        selectedDestination = index
                                    },
                                    icon = {
                                        Image(
                                            painter = painterResource(
                                                destination.resourceId
                                            ),
                                            contentDescription = destination.contentDescription,
                                            colorFilter = ColorFilter.tint(
                                                if (selectedDestination == index) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondary.copy(
                                                    alpha = 0.3f
                                                )
                                            )
                                        )
                                    },
                                    label = {
                                        Text(
                                            destination.label
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors()
                                        .copy(
                                            unselectedTextColor = MaterialTheme.colorScheme.secondary.copy(
                                                alpha = 0.3f
                                            ),
                                            selectedIndicatorColor = Color.Transparent,
                                        )
                                )
                            }
                        }
                    }) { innerPadding ->
                    AppNavHost(
                        navController,
                        startDestination,
                        viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}