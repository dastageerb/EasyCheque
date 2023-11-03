package com.xynotech.cv.ai.di

import com.xynotech.cv.ai.data.UploadCheckApiService
import com.xynotech.cv.ai.data.UploadCheckRepositoryImpl
import com.xynotech.cv.ai.domain.UploadCheckRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
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