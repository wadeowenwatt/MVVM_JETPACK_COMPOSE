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
import wade.owen.watts.base_jetpack.data.models.enum.AppTheme
import wade.owen.watts.base_jetpack.ui.main.MainActivity
import wade.owen.watts.base_jetpack.ui.main.MainViewModel

@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SettingViewModel>()

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
            Button(onClick = { mainViewModel.changeTheme(AppTheme.DARK) }) {
                Text(text = "Dark", color = Color.Black)
            }
            Button(onClick = { mainViewModel.changeTheme(AppTheme.LIGHT) }) {
                Text(text = "Light", color = Color.Black)
            }
            Button(onClick = { mainViewModel.changeTheme(AppTheme.SYSTEM) }) {
                Text(text = "System", color = Color.Black)
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun SettingPagePreview() {
    // SettingPage() // Preview might be broken without a ViewModel instance, but we can't easily mock it here without DI or a fake.
    // Commenting out for now as creating a ViewModel manually is tricky with Hilt or we need a wrapper.
    // Alternatively, we could default the parameter to null or use a composition local, but aiming for correctness first.
    // For now, I will just leave it empty or comment it out to avoid build errors if I can't construct it.
    // Actually, MainViewModel has @Inject constructor() with no args, so we might be able to instantiate it directly if we weren't using HiltViewModel inside?
    // But it inherits ViewModel.
    // Let's just comment out the call in preview for safety or try to instantiate it.
    val mainViewModel = MainViewModel() // This should work since it has an empty @Inject constructor
    SettingPage(mainViewModel = mainViewModel)
}