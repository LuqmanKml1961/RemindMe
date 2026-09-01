package com.remindme.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_references ORDER BY created_at DESC")
    fun getAllReferences(): Flow<List<VaultReferenceEntity>>

    @Query("SELECT * FROM vault_references WHERE id = :id")
    suspend fun getReferenceById(id: Long): VaultReferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReference(reference: VaultReferenceEntity): Long

    @Update
    suspend fun updateReference(reference: VaultReferenceEntity)

    @Query("DELETE FROM vault_references WHERE id = :id")
    suspend fun deleteReference(id: Long)
}