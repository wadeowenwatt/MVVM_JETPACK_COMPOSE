package wade.owen.watts.base_jetpack.ui.pages.setting

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.data.models.enums.AppTheme
import wade.owen.watts.base_jetpack.global.LocalMainViewModel

@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SettingViewModel>()
    val mainVM = LocalMainViewModel.current

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Button(onClick = { viewModel.changeLanguage(context, "vi") }) {
                Text(text = "Vietnamese", color = Color.Black)
            }
            Button(onClick = { viewModel.changeLanguage(context, "en") }) {
                Text(text = "English", color = Color.Black)
            }

            // Text test multi language change
            Text(stringResource(R.string.list_diary_title))
            Box(Modifier.height(20.dp))
            Button(onClick = { mainVM.changeTheme(AppTheme.DARK) }) {
                Text(text = "Dark", color = Color.Black)
            }
            Button(onClick = { mainVM.changeTheme(AppTheme.LIGHT) }) {
                Text(text = "Light", color = Color.Black)
            }
            Button(onClick = { mainVM.changeTheme(AppTheme.SYSTEM) }) {
                Text(text = "System", color = Color.Black)
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun SettingPagePreview() {
    SettingPage()
}