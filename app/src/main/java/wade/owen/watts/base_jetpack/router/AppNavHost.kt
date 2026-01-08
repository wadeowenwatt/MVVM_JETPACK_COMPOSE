package wade.owen.watts.base_jetpack.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.ui.pages.calendar.CalendarPage
import wade.owen.watts.base_jetpack.ui.pages.diary.DiaryPage
import wade.owen.watts.base_jetpack.ui.pages.diary.diary_detail.DiaryDetailPage
import wade.owen.watts.base_jetpack.ui.pages.quote_page.QuotePage
import wade.owen.watts.base_jetpack.ui.pages.setting.SettingPage

@Immutable
enum class Destination(
    val route: String,
    val resourceLabel: Int,
    val resourceId: Int,
    val contentDescription: String
) {
    DIARY("diary", R.string.bottom_nav_diary, R.drawable.ic_list, "List Diary"),
    CALENDAR("calendar", R.string.bottom_nav_calendar, R.drawable.ic_calendar, "Calendar Page"),
    QUOTES("quotes", R.string.bottom_nav_quotes, R.drawable.ic_quote, "Quotes"),
    SETTING("setting", R.string.bottom_nav_setting, R.drawable.ic_setting, "Setting"),
    DIARY_DETAIL("diary_detail", R.string.bottom_nav_diary, R.drawable.ic_list, "Diary Detail")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
        route = "root_graph",
    ) {
        Destination.entries.forEach { destination ->
                composable(destination.route) {
                    when (destination) {
                        Destination.DIARY -> DiaryPage(modifier, navController)
                        Destination.CALENDAR -> CalendarPage()
                        Destination.QUOTES -> QuotePage()
                        Destination.SETTING -> SettingPage()
                        Destination.DIARY_DETAIL -> DiaryDetailPage(
                            navController = navController
                        )
                    }
                }
        }
    }
}