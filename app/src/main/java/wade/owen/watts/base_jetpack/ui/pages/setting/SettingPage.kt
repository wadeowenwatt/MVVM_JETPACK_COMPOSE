package wade.owen.watts.base_jetpack.ui.pages.setting

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import wade.owen.watts.base_jetpack.domain.entities.enums.AppTheme
import wade.owen.watts.base_jetpack.global.LocalMainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SettingViewModel>()
    val mainVM = LocalMainViewModel.current
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.08f))

            Spacer(Modifier.height(24.dp))

            // Appearance Section
            SettingsSectionTitle("Appearance")

            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column {
                    AppearanceOption(
                        label = "Light",
                        emoji = "☀️",
                        selected = false,
                        onClick = { mainVM.changeTheme(AppTheme.LIGHT) }
                    )
                    HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.06f))
                    AppearanceOption(
                        label = "Dark",
                        emoji = "🌙",
                        selected = false,
                        onClick = { mainVM.changeTheme(AppTheme.DARK) }
                    )
                    HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.06f))
                    AppearanceOption(
                        label = "System Default",
                        emoji = "💻",
                        selected = true,
                        onClick = { mainVM.changeTheme(AppTheme.SYSTEM) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // General Section
            SettingsSectionTitle("General")

            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column {
                    SettingsNavRow(
                        icon = Icons.Default.Settings,
                        label = "Language",
                        subtitle = "English (US)",
                        onClick = {}
                    )
                    HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.06f))
                    SettingsNavRow(
                        icon = Icons.Default.Notifications,
                        label = "Notifications",
                        onClick = {}
                    )
                    HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.06f))
                    SettingsNavRow(
                        icon = Icons.Default.Info,
                        label = "Privacy & Security",
                        onClick = {}
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Data Section
            SettingsSectionTitle("Data")

            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column {
                    SettingsNavRow(
                        icon = Icons.Default.Share,
                        label = "Backup & Sync",
                        onClick = {}
                    )
                    HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.06f))
                    // Clear All Data — danger
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {}
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Clear All Data",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "✕",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorScheme.error
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Language quick-switch (retained from original)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Quick Language Switch",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colorScheme.onSurface.copy(alpha = 0.5f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colorScheme.onSurface)
                                .clickable { viewModel.changeLanguage(context, "en") }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "English",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colorScheme.surface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(colorScheme.onSurface.copy(alpha = 0.1f))
                                .clickable { viewModel.changeLanguage(context, "vi") }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "Tiếng Việt",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = androidx.compose.ui.unit.TextUnit(1f, androidx.compose.ui.unit.TextUnitType.Sp)
        ),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun AppearanceOption(
    label: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.size(36.dp)
                .padding(4.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            ),
            modifier = Modifier.weight(1f)
        )
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
        Text(
            text = "›",
            style = MaterialTheme.typography.titleMedium.copy(
                color = colorScheme.onSurface.copy(alpha = 0.35f)
            )
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun SettingPagePreview() {
    SettingPage()
}