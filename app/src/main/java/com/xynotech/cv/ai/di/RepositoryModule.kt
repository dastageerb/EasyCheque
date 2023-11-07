package com.xynotech.cv.ai.di

import com.xynotech.cv.ai.data.UploadCheckApiService
import com.xynotech.cv.ai.data.UploadCheckRepositoryImpl
import com.xynotech.cv.ai.domain.UploadCheckRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUploadCheckRepository(apiService: UploadCheckApiService): UploadCheckRepository {
        return UploadCheckRepositoryImpl(apiService)
    }
}