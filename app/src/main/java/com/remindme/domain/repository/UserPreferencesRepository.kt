package com.remindme.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val autoDeleteDefault: Flow<Boolean>
    suspend fun setAutoDeleteDefault(enabled: Boolean)

    val hasSeenOnboarding: Flow<Boolean>
    suspend fun setHasSeenOnboarding(seen: Boolean)
}