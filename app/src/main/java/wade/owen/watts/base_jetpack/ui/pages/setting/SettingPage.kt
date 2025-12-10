package wade.owen.watts.base_jetpack.ui.pages.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.util.LocaleManager
import java.util.Locale

@Composable
fun SettingPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var currentLocale by remember { mutableStateOf(Locale.getDefault().language) }


    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Button(onClick = { changeLanguage(context, "vi") }) {
                Text(text = "Vietnamese")
            }
            Button(onClick = { changeLanguage(context, "en") }) {
                Text(text = "English")
            }

            // Text test multi language change
            Text(stringResource(R.string.list_diary_title))
        }
    }
}

fun changeLanguage(context: android.content.Context, code: String) {
    LocaleManager.setNewLocale(context, code)
    if (context is android.app.Activity) {
        context.recreate()
    }
}


@Preview(showBackground = true)
@Composable
fun SettingPagePreview() {
    SettingPage()
}