package wade.owen.watts.base_jetpack.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import wade.owen.watts.base_jetpack.ui.pages.home.HomePage
import wade.owen.watts.base_jetpack.ui.theme.Jetpack_compose_mvvmTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Jetpack_compose_mvvmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomePage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun RandomQuote(quote: String, modifier: Modifier = Modifier) {
    Column() {
        Text(quote, modifier = Modifier.padding(50.dp))
        Text("this is author")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Jetpack_compose_mvvmTheme {
        RandomQuote("Android")
    }
}