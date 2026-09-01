package com.remindme.data.repository

import com.remindme.data.local.VaultDao
import com.remindme.data.local.toDomain
import com.remindme.data.local.toEntity
import com.remindme.domain.model.VaultReference
import com.remindme.domain.repository.VaultReferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultReferenceRepositoryImpl @Inject constructor(
    private val vaultDao: VaultDao
) : VaultReferenceRepository {

    override fun getAllReferences(): Flow<List<VaultReference>> {
        return vaultDao.getAllReferences().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsertReference(reference: VaultReference) {
        val existing = if (reference.id > 0) vaultDao.getReferenceById(reference.id) else null
        val entity = reference.toEntity()
        if (existing != null) {
            vaultDao.updateReference(entity.copy(createdAt = existing.createdAt))
        } else {
            vaultDao.insertReference(entity)
        }
    }

    override suspend fun deleteReference(id: Long) {
        vaultDao.deleteReference(id)
    }
}