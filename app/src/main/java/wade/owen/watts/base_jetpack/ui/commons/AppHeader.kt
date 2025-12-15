package wade.owen.watts.base_jetpack.ui.commons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.size.Size
import kotlinx.coroutines.delay
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.utils.DateTimeFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AppHeader(modifier: Modifier = Modifier) {
    Column(
        modifier
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                stringResource(R.string.list_diary_title),
                style = MaterialTheme.typography.headlineSmall
            )
            LiveTimeText(modifier)
        }
        HorizontalDivider(
            modifier = Modifier.height(2.dp)
        )
    }

}

@Composable
fun LiveTimeText(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            delay(1000L)
        }
    }

    Text(
        currentTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.PATTERN_FULL_DATE)),
        style = MaterialTheme.typography.titleMedium.copy(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
        )
    )

}