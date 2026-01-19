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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import wade.owen.watts.base_jetpack.core.router.BottomNavDestination
import wade.owen.watts.base_jetpack.core.router.RootDestination
import wade.owen.watts.base_jetpack.core.router.RootNavHost
import wade.owen.watts.base_jetpack.core.router.shouldShowBottomNavBar

@Composable
fun MainPage(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomNavBar(currentRoute)) {
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets,
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    BottomNavDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(route = destination.route) {
                                    popUpTo(RootDestination.BOTTOM_NAV) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
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
            }
        }) { innerPadding ->
        RootNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}