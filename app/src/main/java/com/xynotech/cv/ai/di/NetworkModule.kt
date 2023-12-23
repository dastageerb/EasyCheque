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
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule
{

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val jsonInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(jsonInterceptor)  // Add the JSON interceptor here
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(client:OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://b830-2400-adc1-178-8b00-9427-fcc1-ccf9-de1f.ngrok-free.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCheckUploadApi(retrofit: Retrofit): UploadCheckApiService {
        return retrofit.create(UploadCheckApiService::class.java)
    }
}