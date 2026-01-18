package wade.owen.watts.base_jetpack.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail.DiaryDetailPage

object RootDestination {
    const val BOTTOM_NAV = "bottom_nav"
    const val DIARY_DETAIL = "diary_detail/{diary_id}"
    fun createDiaryDetailRoute(diaryId: Int = -1) = "diary_detail/$diaryId"
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

        composable(
            route = RootDestination.DIARY_DETAIL,
            arguments = listOf(
                navArgument("diary_id") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            DiaryDetailPage(modifier = modifier, navController = navController)
        }
    }
}