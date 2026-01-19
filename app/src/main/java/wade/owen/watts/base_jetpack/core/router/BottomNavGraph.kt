package wade.owen.watts.base_jetpack.core.router

import androidx.compose.runtime.Immutable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.ui.pages.calendar.CalendarPage
import wade.owen.watts.base_jetpack.ui.pages.diary.DiaryPage
import wade.owen.watts.base_jetpack.ui.pages.quote_page.QuotePage
import wade.owen.watts.base_jetpack.ui.pages.setting.SettingPage

@Immutable
enum class BottomNavDestination(
    val route: String,
    val resourceLabel: Int,
    val resourceId: Int,
    val contentDescription: String
) {
    DIARY(
        "diary",
        R.string.bottom_nav_diary,
        R.drawable.ic_list,
        "List Diary"
    ),
    CALENDAR(
        "calendar",
        R.string.bottom_nav_calendar,
        R.drawable.ic_calendar,
        "Calendar Page"
    ),
    QUOTES(
        "quotes",
        R.string.bottom_nav_quotes,
        R.drawable.ic_quote,
        "Quotes"
    ),
    SETTING(
        "setting", R.string.bottom_nav_setting,
        R.drawable.ic_setting,
        "Setting"
    )
}

fun NavGraphBuilder.bottomNavGraph(navController: NavHostController) {
    navigation(
        startDestination = BottomNavDestination.DIARY.route,
        route = RootDestination.BOTTOM_NAV,
    ) {
        BottomNavDestination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    BottomNavDestination.DIARY -> DiaryPage(
                        navController = navController
                    )

                    BottomNavDestination.CALENDAR -> CalendarPage(
                        navController = navController
                    )

                    BottomNavDestination.QUOTES -> QuotePage()
                    BottomNavDestination.SETTING -> SettingPage()
                }
            }
        }
    }
}

fun shouldShowBottomNavBar(currentRoute: String?): Boolean {
    return currentRoute in BottomNavDestination.entries.map { it.route }
}