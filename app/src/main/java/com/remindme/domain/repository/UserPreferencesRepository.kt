package com.remindme.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val autoDeleteDefault: Flow<Boolean>
    suspend fun setAutoDeleteDefault(enabled: Boolean)
}