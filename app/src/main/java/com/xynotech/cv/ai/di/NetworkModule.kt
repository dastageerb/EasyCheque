package com.xynotech.cv.ai.di

import com.xynotech.cv.ai.data.UploadCheckApiService
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
object NetworkModule
{

    @Provides
    @Singleton
    fun providesOkHttpClient(interceptor:Interceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(interceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1,TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(client:OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://f7fe-2400-adc1-178-8b00-9862-2cdc-4f3-987c.ngrok-free.app/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCheckUploadApi(retrofit: Retrofit): UploadCheckApiService {
        return retrofit.create(UploadCheckApiService::class.java)
    }
}