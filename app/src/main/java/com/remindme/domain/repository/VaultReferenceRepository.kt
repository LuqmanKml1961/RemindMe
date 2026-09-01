package com.remindme.domain.repository

import com.remindme.domain.model.VaultReference
import kotlinx.coroutines.flow.Flow

interface VaultReferenceRepository {
    fun getAllReferences(): Flow<List<VaultReference>>
    suspend fun upsertReference(reference: VaultReference)
    suspend fun deleteReference(id: Long)
}