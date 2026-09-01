package com.remindme.di

import com.remindme.data.local.UserPreferencesRepositoryImpl
import com.remindme.data.repository.ReminderRepositoryImpl
import com.remindme.data.repository.TodoRepositoryImpl
import com.remindme.data.repository.VaultReferenceRepositoryImpl
import com.remindme.domain.repository.ReminderRepository
import com.remindme.domain.repository.TodoRepository
import com.remindme.domain.repository.UserPreferencesRepository
import com.remindme.domain.repository.VaultReferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        reminderRepositoryImpl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindTodoRepository(
        todoRepositoryImpl: TodoRepositoryImpl
    ): TodoRepository

    @Binds
    @Singleton
    abstract fun bindVaultReferenceRepository(
        vaultReferenceRepositoryImpl: VaultReferenceRepositoryImpl
    ): VaultReferenceRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}