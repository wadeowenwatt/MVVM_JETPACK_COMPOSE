package wade.owen.watts.base_jetpack.ui.pages.setting

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import wade.owen.watts.base_jetpack.R
import wade.owen.watts.base_jetpack.domain.entities.enums.AppTheme
import wade.owen.watts.base_jetpack.global.LocalMainViewModel

// ─── SettingPage ──────────────────────────────────────────────────────────────

@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SettingViewModel>()
    val mainVM = LocalMainViewModel.current
    val mainState by mainVM.state.collectAsState()
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    var bgMusicOn by remember { mutableStateOf(true) }
    var soundFxOn by remember { mutableStateOf(false) }

    val themeLabel = when (mainState.theme) {
        AppTheme.LIGHT -> "Light"
        AppTheme.DARK -> "Dark"
        AppTheme.SYSTEM -> "System"
    }
    val nextTheme = when (mainState.theme) {
        AppTheme.LIGHT -> AppTheme.DARK
        AppTheme.DARK -> AppTheme.SYSTEM
        AppTheme.SYSTEM -> AppTheme.LIGHT
    }

    Scaffold(containerColor = cs.background) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Settings",
                    style = ty.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = cs.onBackground
                )
            }

            // ── Content ────────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(
                    top = 24.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // ── APPEARANCE ──────────────────────────────────────────────
                SettingsSection(title = "APPEARANCE") {
                    SettingsNavRow(
                        iconRes = R.drawable.ic_sun,
                        label = "Theme",
                        valueText = themeLabel,
                        showDivider = true,
                        onClick = { mainVM.changeTheme(nextTheme) }
                    )
                    SettingsNavRow(
                        iconRes = R.drawable.ic_type,
                        label = "Font Size",
                        valueText = "Medium",
                        showDivider = false,
                        onClick = {}
                    )
                }

                // ── SOUND ───────────────────────────────────────────────────
                SettingsSection(title = "SOUND") {
                    SettingsToggleRow(
                        iconRes = R.drawable.ic_music,
                        label = "Background Music",
                        checked = bgMusicOn,
                        showDivider = true,
                        onCheckedChange = { bgMusicOn = it }
                    )
                    SettingsToggleRow(
                        iconRes = R.drawable.ic_volume_2,
                        label = "Sound Effects",
                        checked = soundFxOn,
                        showDivider = false,
                        onCheckedChange = { soundFxOn = it }
                    )
                }

                // ── ABOUT ───────────────────────────────────────────────────
                SettingsSection(title = "ABOUT") {
                    SettingsNavRow(
                        iconRes = R.drawable.ic_info,
                        label = "Version",
                        valueText = "1.2.0",
                        showDivider = true,
                        onClick = {}
                    )
                    SettingsNavRow(
                        iconRes = R.drawable.ic_shield,
                        label = "Privacy Policy",
                        showDivider = true,
                        onClick = {}
                    )
                    SettingsNavRow(
                        iconRes = R.drawable.ic_file_text,
                        label = "Terms of Service",
                        showDivider = true,
                        onClick = {}
                    )
                    SettingsNavRow(
                        iconRes = R.drawable.ic_star,
                        label = "Rate App",
                        showDivider = false,
                        onClick = {}
                    )
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

// ─── Section container ────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = ty.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp
            ),
            color = cs.onSurfaceVariant
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surface),
            border = BorderStroke(1.dp, cs.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column { content() }
        }
    }
}

// ─── Nav row (label + value/chevron) ─────────────────────────────────────────

@Composable
private fun SettingsNavRow(
    iconRes: Int,
    label: String,
    valueText: String? = null,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = cs.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = ty.bodyMedium.copy(fontSize = 14.sp),
                color = cs.onBackground,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (valueText != null) {
                Text(
                    text = valueText,
                    style = ty.bodySmall.copy(fontSize = 13.sp),
                    color = cs.onSurfaceVariant
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(color = cs.outline, thickness = 1.dp)
        }
    }
}

// ─── Toggle row ───────────────────────────────────────────────────────────────

@Composable
private fun SettingsToggleRow(
    iconRes: Int,
    label: String,
    checked: Boolean,
    showDivider: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = cs.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = ty.bodyMedium.copy(fontSize = 14.sp),
                color = cs.onBackground,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = cs.onBackground,
                    checkedThumbColor = cs.background,
                    uncheckedTrackColor = cs.outline,
                    uncheckedThumbColor = cs.background,
                    uncheckedBorderColor = Color.Transparent,
                    checkedBorderColor = Color.Transparent
                )
            )
        }
        if (showDivider) {
            HorizontalDivider(color = cs.outline, thickness = 1.dp)
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun SettingPagePreview() {
    SettingPage()
}
