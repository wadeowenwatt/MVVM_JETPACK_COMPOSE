package wade.owen.watts.base_jetpack.router

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import wade.owen.watts.base_jetpack.ui.main.MainPage
import wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail.DiaryDetailPage

object RootDestination {
    const val HOME = "home"
    const val DIARY_DETAIL = "diary_detail"
}

@Composable
fun RootNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = RootDestination.HOME,
        route = "root_graph"
    ) {
        composable(RootDestination.HOME) {
            MainPage(rootNavController = navController)
        }

        composable(RootDestination.DIARY_DETAIL) {
            DiaryDetailPage(navController = navController)
        }
    }
}
