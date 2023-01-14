package com.scrumteam.mytask.di

import com.scrumteam.mytask.data.repository.auth.AuthRepository
import com.scrumteam.mytask.data.repository.auth.AuthRepositoryImpl
import com.scrumteam.mytask.data.repository.notification.NotificationRepository
import com.scrumteam.mytask.data.repository.notification.NotificationRepositoryImpl
import com.scrumteam.mytask.data.repository.task.TaskRepository
import com.scrumteam.mytask.data.repository.task.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository


    @Binds
    @Singleton
    abstract fun bindsTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindsNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}