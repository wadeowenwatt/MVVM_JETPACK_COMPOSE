package wade.owen.watts.base_jetpack.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail.DiaryDetailPage

object RootDestination {
    const val BOTTOM_NAV = "bottom_nav"
    const val DIARY_DETAIL = "diary_detail"
}

@Composable
fun RootNavHost(
    navController: NavHostController,
    modifier: Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = RootDestination.BOTTOM_NAV,
    ) {
        bottomNavGraph(navController)

        composable(RootDestination.DIARY_DETAIL) {
            DiaryDetailPage(modifier = modifier, navController = navController)
        }
    }
}