package wade.owen.watts.base_jetpack.ui.pages.quote_page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotePage(
    modifier: Modifier = Modifier,
    viewModel: QuoteViewModel = hiltViewModel<QuoteViewModel>()
) {
    val uiState by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DAILY INSPIRATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colorScheme.onSurface.copy(alpha = 0.45f),
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp
                            )
                        )
                    }
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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.08f))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Large decorative quote mark
                        Text(
                            text = "\u201C",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 80.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface.copy(alpha = 0.12f),
                                lineHeight = 60.sp
                            )
                        )

                        AnimatedContent(
                            targetState = uiState.quote,
                            transitionSpec = {
                                (slideInVertically { it / 4 } + fadeIn())
                                    .togetherWith(slideOutVertically { -it / 4 } + fadeOut())
                            },
                            label = "quote_transition"
                        ) { quote ->
                            Text(
                                text = quote.ifBlank { "\"The only way to do great work is to love what you do.\"" },
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 36.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        Text(
                            text = "— Steve Jobs",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                color = colorScheme.onSurface.copy(alpha = 0.55f)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Mountain illustration (decorative placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏔️  🌄  🌿",
                    fontSize = 36.sp,
                    textAlign = TextAlign.Center
                )
            }

            // New Quote button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                shape = RoundedCornerShape(50.dp),
                color = colorScheme.onSurface,
                onClick = { /* TODO: viewModel.fetchNewQuote() */ }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "↻  New Quote",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = colorScheme.surface,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(72.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuotePagePreview(modifier: Modifier = Modifier) {
    QuotePage(modifier)
}