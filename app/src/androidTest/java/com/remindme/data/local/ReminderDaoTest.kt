package com.remindme.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReminderDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var reminderDao: ReminderDao
    private lateinit var todoDao: TodoDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        reminderDao = database.reminderDao()
        todoDao = database.todoDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun sampleEntity(title: String = "Buy milk") = ReminderEntity(
        title = title,
        type = "GENERAL",
        createdAt = System.currentTimeMillis(),
        isCompleted = false,
        isArchived = false,
        autoDelete = false
    )

    @Test
    fun insert_and_get_all() = runBlocking {
        reminderDao.insertReminder(sampleEntity("One"))
        reminderDao.insertReminder(sampleEntity("Two"))

        val reminders = reminderDao.getAllReminders().first()

        assertEquals(2, reminders.size)
    }

    @Test
    fun insert_and_get_by_id() = runBlocking {
        val id = reminderDao.insertReminder(sampleEntity("Pay rent"))

        val reminder = reminderDao.getReminderById(id).first()

        assertNotNull(reminder)
        assertEquals("Pay rent", reminder!!.title)
    }

    @Test
    fun complete_and_archive() = runBlocking {
        val id = reminderDao.insertReminder(sampleEntity())
        reminderDao.completeReminder(id)
        reminderDao.archiveReminder(id)

        val reminder = reminderDao.getReminderById(id).first()

        assertTrue(reminder!!.isCompleted)
        assertTrue(reminder.isArchived)
    }

    @Test
    fun delete_reminder() = runBlocking {
        val id = reminderDao.insertReminder(sampleEntity())
        reminderDao.deleteReminder(id)

        val reminder = reminderDao.getReminderById(id).first()

        assertNull(reminder)
    }

    @Test
    fun delete_completed_with_autoDelete() = runBlocking {
        val keep = reminderDao.insertReminder(sampleEntity("Keep"))
        val delete = reminderDao.insertReminder(sampleEntity("Delete").copy(autoDelete = true))

        reminderDao.completeReminder(keep)
        reminderDao.completeReminder(delete)
        reminderDao.deleteCompletedReminders()

        val remaining = reminderDao.getAllReminders().first()
        assertEquals(1, remaining.size)
        assertEquals("Keep", remaining[0].title)
    }

    @Test
    fun get_by_share_id() = runBlocking {
        reminderDao.insertReminder(sampleEntity().copy(shareId = "abc-123"))

        val found = reminderDao.getReminderByShareId("abc-123").first()
        val notFound = reminderDao.getReminderByShareId("missing").first()

        assertNotNull(found)
        assertNull(notFound)
    }

    @Test
    fun todo_foreign_key_cascades_on_reminder_delete() = runBlocking {
        val reminderId = reminderDao.insertReminder(sampleEntity())
        val todoId = todoDao.insertTodo(
            TodoEntity(text = "Sync task", reminderId = reminderId, createdAt = 1L)
        )

        reminderDao.deleteReminder(reminderId)

        val todos = todoDao.getAllTodos().first()
        assertTrue(todos.isEmpty())
    }
}