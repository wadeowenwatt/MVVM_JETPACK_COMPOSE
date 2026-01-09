package wade.owen.watts.base_jetpack.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavHostController
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import wade.owen.watts.base_jetpack.router.MainNavHost
import wade.owen.watts.base_jetpack.router.Destination

@Composable
fun MainPage(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                windowInsets = NavigationBarDefaults.windowInsets,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Destination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(route = destination.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo("root_graph") {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // re-selecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = {
                            Image(
                                painter = painterResource(
                                    destination.resourceId
                                ),
                                contentDescription = destination.contentDescription,
                                colorFilter = ColorFilter.tint(
                                    if (currentRoute == destination.route) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondary.copy(
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
        MainNavHost(
            navController,
            rootNavController,
            startDestination = Destination.DIARY,
            modifier = Modifier.padding(innerPadding)
        )
    }
}