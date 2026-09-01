package com.remindme.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.remindme.domain.model.ReminderType
import com.remindme.presentation.ui.theme.AccentBlue
import com.remindme.presentation.ui.theme.AccentRed
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ----------------------------------------------------------
// Time formatting helpers
// ----------------------------------------------------------
private val loudDateFmt = DateTimeFormatter.ofPattern("EEE, MMM d — h:mm a")
private val dateOnlyFmt = DateTimeFormatter.ofPattern("EEE, MMM d")
private val timeOnlyFmt = DateTimeFormatter.ofPattern("h:mm a")

fun loudDateTime(date: LocalDateTime): String = date.format(loudDateFmt)
fun dateOnly(date: LocalDateTime): String = date.format(dateOnlyFmt)
fun timeOnly(date: LocalDateTime): String = date.format(timeOnlyFmt)

/** e.g. "IN 5 MIN", "IN 3 HR", "TODAY 6:00 PM", "TOMORROW 9 AM", or "JAN 5 9 AM". */
fun dueLabel(due: LocalDateTime): String {
    val now = LocalDateTime.now()
    val minutes = java.time.Duration.between(now, due).toMinutes()
    return when {
        due.isBefore(now) -> "OVERDUE"
        minutes < 90 -> "IN ${maxOf(1, minutes)} MIN"
        minutes < 60 * 26 -> {
            if (due.toLocalDate() == now.toLocalDate()) "TODAY ${timeOnly(due)}"
            else if (due.toLocalDate() == now.toLocalDate().plusDays(1)) "TOMORROW ${timeOnly(due)}"
            else "${dateOnly(due)} ${timeOnly(due)}"
        }
        else -> "${dateOnly(due)} ${timeOnly(due)}"
    }
}

// ----------------------------------------------------------
// BrutCard — hard offset shadow + 1dp ink border
// ----------------------------------------------------------
@Composable
fun BrutCard(
    modifier: Modifier = Modifier,
    shadow: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Box(modifier) {
        if (shadow) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 4.dp, y = 4.dp)
                    .background(onSurface)
            )
        }
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, onSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            content()
        }
    }
}

// ----------------------------------------------------------
// BrutPass — a plain bordered container without shadow
// ----------------------------------------------------------
@Composable
fun BrutPass(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, borderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            content()
        }
    }
}

// ----------------------------------------------------------
// BrutButton / BrutOutlinedButton
// ----------------------------------------------------------
@Composable
fun BrutButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val background = MaterialTheme.colorScheme.background
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(0.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = onSurface,
            contentColor = background,
            disabledContainerColor = onSurface.copy(alpha = 0.3f),
            disabledContentColor = background.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BrutOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface = MaterialTheme.colorScheme.surface
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(0.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = surface,
            contentColor = onSurface,
            disabledContainerColor = surface.copy(alpha = 0.5f),
            disabledContentColor = onSurface.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, onSurface),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

// ----------------------------------------------------------
// BrutChip — square bordered selectable chip
// ----------------------------------------------------------
@Composable
fun BrutChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val surface = MaterialTheme.colorScheme.surface
    Box(
        modifier = modifier
            .height(40.dp)
            .background(if (selected) onSurface else surface)
            .border(1.dp, onSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (selected) surface else onSurface
        )
    }
}

// ----------------------------------------------------------
// SectionHeader — uppercase title + ink rule
// ----------------------------------------------------------
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null
) {
    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            trailing?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ----------------------------------------------------------
// StatBox — dashboard stat
// ----------------------------------------------------------
@Composable
fun StatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.onSurface
) {
    BrutCard(modifier = modifier, shadow = false) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = accent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ----------------------------------------------------------
// TypeTag — MED / BILL / GEN
// ----------------------------------------------------------
@Composable
fun TypeTag(
    type: ReminderType,
    modifier: Modifier = Modifier
) {
    val (chipBg, chipFg) = when (type) {
        ReminderType.MEDICAL -> AccentRed to Color.White
        ReminderType.MONTHLY -> AccentBlue to Color.White
        ReminderType.GENERAL ->
            MaterialTheme.colorScheme.onSurface to MaterialTheme.colorScheme.surface
    }
    Box(
        modifier = modifier
            .background(chipBg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = when (type) {
                ReminderType.MEDICAL -> "MED"
                ReminderType.MONTHLY -> "BILL"
                ReminderType.GENERAL -> "GEN"
            },
            style = MaterialTheme.typography.labelSmall,
            color = chipFg,
            fontWeight = FontWeight.Bold
        )
    }
}

// ----------------------------------------------------------
// ReminderType accent color (shared with cards/headers)
// ----------------------------------------------------------
@Composable
fun typeAccent(type: ReminderType): Color = when (type) {
    ReminderType.MEDICAL -> AccentRed
    ReminderType.MONTHLY -> AccentBlue
    ReminderType.GENERAL -> MaterialTheme.colorScheme.onSurface
}