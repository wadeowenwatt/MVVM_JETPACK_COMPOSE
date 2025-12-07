package wade.owen.watts.base_jetpack.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import wade.owen.watts.base_jetpack.ui.pages.calendar.CalendarPage
import wade.owen.watts.base_jetpack.ui.pages.diary.DiaryPage
import wade.owen.watts.base_jetpack.ui.pages.quote_page.QuotePage
import wade.owen.watts.base_jetpack.ui.pages.setting.SettingPage


enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    DIARY("diary", "Diary", Icons.AutoMirrored.Filled.List, "List Diary"),
    CALENDAR("calendar", "Calendar", Icons.AutoMirrored.Filled.List, "Calendar Page"),
    QUOTES("quotes", "Quotes", Icons.AutoMirrored.Filled.List, "Quotes"),
    SETTING("setting", "Setting", Icons.AutoMirrored.Filled.List, "Setting")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.DIARY -> DiaryPage()
                    Destination.CALENDAR -> CalendarPage()
                    Destination.QUOTES -> QuotePage()
                    Destination.SETTING -> SettingPage()
                }
            }
        }
    }
}