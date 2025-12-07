package wade.owen.watts.base_jetpack.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import wade.owen.watts.base_jetpack.router.AppNavHost
import wade.owen.watts.base_jetpack.router.Destination
import wade.owen.watts.base_jetpack.ui.theme.Jetpack_compose_mvvmTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Jetpack_compose_mvvmTheme(dynamicColor = false) {
                val navController = rememberNavController()
                val startDestination = Destination.DIARY
                var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
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
                                        Icon(
                                            destination.icon,
                                            contentDescription = destination.contentDescription
                                        )
                                    },
                                    label = { Text(destination.label) },
                                    colors = NavigationBarItemDefaults.colors().copy(
                                        unselectedIconColor = Color.Black.copy(alpha = 0.3f),
                                        unselectedTextColor = Color.Black.copy(alpha = 0.3f),
                                        selectedIndicatorColor = Color.Transparent,
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController,
                        startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}