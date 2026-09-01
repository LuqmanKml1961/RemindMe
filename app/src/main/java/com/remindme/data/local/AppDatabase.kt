package com.remindme.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ReminderEntity::class,
        TodoEntity::class,
        MedicationEntity::class,
        VaultReferenceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun todoDao(): TodoDao
    abstract fun vaultDao(): VaultDao

    companion object {
        const val DATABASE_NAME = "remindme_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Repeat rule on all reminders (v1 stored recurrence_days only for bills)
                db.execSQL("ALTER TABLE reminders ADD COLUMN recurrence TEXT")
                db.execSQL(
                    "UPDATE reminders SET recurrence = 'EVERY_' || recurrence_days " +
                        "WHERE recurrence_days IS NOT NULL AND recurrence_days > 0"
                )
                // Medications table (multiple meds per medical reminder)
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS medications (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "reminder_id INTEGER NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "dosage TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_medications_reminder_id " +
                        "ON medications(reminder_id)"
                )
                // Carry old single-medicine fields into the new table
                db.execSQL(
                    "INSERT INTO medications (reminder_id, name, dosage, instructions) " +
                        "SELECT id, COALESCE(medicine_name, ''), COALESCE(dosage, ''), COALESCE(instructions, '') " +
                        "FROM reminders WHERE type = 'MEDICAL' " +
                        "AND medicine_name IS NOT NULL AND medicine_name != ''"
                )
                // Vault (zero-alert reference data)
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS vault_references (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "category TEXT NOT NULL, " +
                        "title TEXT NOT NULL, " +
                        "note TEXT NOT NULL, " +
                        "created_at INTEGER NOT NULL)"
                )
            }
        }
    }
}