package com.remindme.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.remindme.presentation.ui.components.BrutButton
import com.remindme.presentation.ui.components.BrutOutlinedButton

private data class OnboardPage(
    val index: String,
    val title: String,
    val body: String
)

private val pages = listOf(
    OnboardPage(
        index = "01",
        title = "WHAT IS REMINDME",
        body = "Simple, exact reminders that buzz your phone on time.\n\nEverything stays on your device — no accounts, no cloud."
    ),
    OnboardPage(
        index = "02",
        title = "MAKE IT IN SECONDS",
        body = "Type a title, tap a time.\n\n5 minutes, half an hour, or a custom date & time — we handle the rest."
    ),
    OnboardPage(
        index = "03",
        title = "SHARE & IMPORT",
        body = "Share any reminder as a link.\n\nWhen a friend opens it, RemindMe imports it instantly."
    )
)

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    var page by rememberSaveable { mutableStateOf(0) }
    val current = pages[page]
    val isLast = page == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Wordmark
        Text(
            text = "REMINDME",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "REMIND·ME  v2",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Page content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Text(
                    text = current.index,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = current.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = current.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Page dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.indices.forEach { i ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(if (i == page) 24.dp else 12.dp)
                        .height(12.dp)
                        .background(
                            if (i == page) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                )
            }
        }

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (page > 0) {
                BrutOutlinedButton(
                    text = "Back",
                    onClick = { page-- },
                    modifier = Modifier.weight(1f)
                )
            } else {
                BrutOutlinedButton(
                    text = "Skip",
                    onClick = onGetStarted,
                    modifier = Modifier.weight(1f)
                )
            }
            BrutButton(
                text = if (isLast) "Get Started" else "Next",
                onClick = {
                    if (isLast) onGetStarted() else page++
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}