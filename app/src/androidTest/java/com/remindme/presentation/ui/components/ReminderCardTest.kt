package com.remindme.presentation.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.remindme.domain.model.Reminder
import com.remindme.domain.model.ReminderType
import com.remindme.presentation.ui.theme.RemindMeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ReminderCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val reminder = Reminder(
        id = 1L,
        title = "Buy milk",
        description = "2 bottles",
        type = ReminderType.GENERAL,
        dueDate = LocalDateTime.of(2024, 3, 1, 9, 0)
    )

    @Test
    fun `reminder card shows title and description`() {
        composeTestRule.setContent {
            RemindMeTheme {
                ReminderCard(
                    reminder = reminder,
                    onComplete = {},
                    onEdit = {},
                    onDelete = {},
                    onShare = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Buy milk").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 bottles").assertIsDisplayed()
    }

    @Test
    fun `medical reminder card shows medicine name`() {
        val medical = reminder.copy(
            type = ReminderType.MEDICAL,
            medicineName = "Metformin",
            dosage = "500mg"
        )

        composeTestRule.setContent {
            RemindMeTheme {
                ReminderCard(
                    reminder = medical,
                    onComplete = {},
                    onEdit = {},
                    onDelete = {},
                    onShare = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Metformin 500mg").assertIsDisplayed()
    }

    @Test
    fun `monthly reminder card shows amount`() {
        val monthly = reminder.copy(type = ReminderType.MONTHLY, amount = 1200.0)

        composeTestRule.setContent {
            RemindMeTheme {
                ReminderCard(
                    reminder = monthly,
                    onComplete = {},
                    onEdit = {},
                    onDelete = {},
                    onShare = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Amount: RM1200.0").assertIsDisplayed()
    }
}