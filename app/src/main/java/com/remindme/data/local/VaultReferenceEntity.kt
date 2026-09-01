package com.remindme.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_references")
data class VaultReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val title: String,
    val note: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)