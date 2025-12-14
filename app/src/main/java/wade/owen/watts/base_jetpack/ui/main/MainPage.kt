package wade.owen.watts.base_jetpack.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import wade.owen.watts.base_jetpack.router.AppNavHost
import wade.owen.watts.base_jetpack.router.Destination

@Composable
fun MainPage(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.DIARY
    var selectedDestination by rememberSaveable {
        mutableIntStateOf(
            startDestination.ordinal
        )
    }


    Scaffold(
        modifier = modifier.fillMaxSize(), bottomBar = {
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
                                stringResource(destination.resourceLabel)
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
            modifier = Modifier.padding(innerPadding)
        )
    }
}