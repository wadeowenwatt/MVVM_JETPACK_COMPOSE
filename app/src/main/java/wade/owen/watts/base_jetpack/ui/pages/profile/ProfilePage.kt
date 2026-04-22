package wade.owen.watts.base_jetpack.ui.pages.profile

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import wade.owen.watts.base_jetpack.R

// ─── ProfilePage ──────────────────────────────────────────────────────────────

@Composable
fun ProfilePage() {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Scaffold(containerColor = cs.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "Profile",
                    style = ty.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = cs.onBackground
                )
            }

            // ── Content ────────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(
                    top = 24.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // ── Profile Card ────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cs.surface),
                    border = BorderStroke(1.dp, cs.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(cs.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_user),
                                contentDescription = null,
                                tint = cs.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            text = "Sarah Johnson",
                            style = ty.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = cs.onBackground
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "sarah.johnson@email.com",
                                style = ty.bodySmall.copy(fontSize = 13.sp),
                                color = cs.onSurfaceVariant
                            )
                            Text(
                                text = "Joined June 2023",
                                style = ty.bodySmall.copy(fontSize = 12.sp),
                                color = cs.onSurfaceVariant
                            )
                        }
                    }
                }

                // ── Stats Row ───────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        number = "127",
                        label = "Entries"
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        number = "45",
                        label = "Photos"
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        number = "12",
                        label = "Tags"
                    )
                }

                // ── Menu Card ───────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cs.surface),
                    border = BorderStroke(1.dp, cs.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        ProfileMenuItem(
                            iconRes = R.drawable.ic_user_pen,
                            label = "Edit Profile",
                            showDivider = true
                        )
                        ProfileMenuItem(
                            iconRes = R.drawable.ic_download,
                            label = "Export Data",
                            showDivider = true
                        )
                        ProfileMenuItem(
                            iconRes = R.drawable.ic_lock,
                            label = "Privacy",
                            showDivider = true
                        )
                        ProfileMenuItem(
                            iconRes = R.drawable.ic_life_buoy,
                            label = "Help & Support",
                            showDivider = false
                        )
                    }
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}

// ─── Stat Card ────────────────────────────────────────────────────────────────

@Composable
private fun ProfileStatCard(
    modifier: Modifier = Modifier,
    number: String,
    label: String
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, cs.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = number,
                style = ty.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = cs.onBackground
            )
            Text(
                text = label,
                style = ty.labelSmall.copy(fontSize = 11.sp),
                color = cs.onSurfaceVariant
            )
        }
    }
}

// ─── Menu Item ────────────────────────────────────────────────────────────────

@Composable
private fun ProfileMenuItem(
    iconRes: Int,
    label: String,
    showDivider: Boolean
) {
    val cs = MaterialTheme.colorScheme
    val ty = MaterialTheme.typography

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {}
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
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = cs.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
        if (showDivider) {
            HorizontalDivider(
                color = cs.outline,
                thickness = 1.dp
            )
        }
    }
}
