package com.remindme.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.remindme.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private object Keys {
        val AUTO_DELETE_DEFAULT = booleanPreferencesKey("auto_delete_default")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    }

    override val autoDeleteDefault: Flow<Boolean> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[Keys.AUTO_DELETE_DEFAULT] ?: false
        }

    override suspend fun setAutoDeleteDefault(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.AUTO_DELETE_DEFAULT] = enabled
        }
    }

    override val hasSeenOnboarding: Flow<Boolean> = context.userPreferencesDataStore.data
        .map { preferences ->
            preferences[Keys.HAS_SEEN_ONBOARDING] ?: false
        }

    override suspend fun setHasSeenOnboarding(seen: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.HAS_SEEN_ONBOARDING] = seen
        }
    }
}