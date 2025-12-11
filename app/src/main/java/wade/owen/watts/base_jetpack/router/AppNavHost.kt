package wade.owen.watts.base_jetpack.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.ui.main.MainViewModel
import wade.owen.watts.base_jetpack.ui.pages.calendar.CalendarPage
import wade.owen.watts.base_jetpack.ui.pages.diary.DiaryPage
import wade.owen.watts.base_jetpack.ui.pages.quote_page.QuotePage
import wade.owen.watts.base_jetpack.ui.pages.setting.SettingPage

enum class Destination(
    val route: String,
    val label: String,
    val resourceId: Int,
    val contentDescription: String
) {
    DIARY("diary", "Diary", R.drawable.ic_list, "List Diary"),
    CALENDAR("calendar", "Calendar", R.drawable.ic_calendar, "Calendar Page"),
    QUOTES("quotes", "Quotes", R.drawable.ic_quote, "Quotes"),
    SETTING("setting", "Setting", R.drawable.ic_setting, "Setting")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    mainViewModel: MainViewModel, // Injected ViewModel
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
                    Destination.DIARY -> DiaryPage(modifier)
                    Destination.CALENDAR -> CalendarPage()
                    Destination.QUOTES -> QuotePage()
                    Destination.SETTING -> SettingPage(mainViewModel = mainViewModel)
                }
            }
        }
    }
}