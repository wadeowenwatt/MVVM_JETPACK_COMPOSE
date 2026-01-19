package wade.owen.watts.base_jetpack.ui.pages.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.core.router.RootDestination
import wade.owen.watts.base_jetpack.core.designsystem.AppHeader

@Composable
fun CalendarPage(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController?.navigate(
                        RootDestination.createDiaryDetailRoute()
                    )
                },
                shape = CircleShape,
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "Add",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        }
    ) { innerPadding ->
        Column() {
            AppHeader(
                Modifier.padding(innerPadding)
            )
        }
    }
}