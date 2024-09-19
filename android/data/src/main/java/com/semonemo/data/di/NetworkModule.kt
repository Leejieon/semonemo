package com.semonemo.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.semonemo.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    @Named("Spring")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json =
            Json {
                isLenient = true
                prettyPrint = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        return Retrofit
            .Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BuildConfig.SEVER_URL + BuildConfig.SPRING_PORT_NUMBER)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    @Named("Node")
    fun provideNFTRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json =
            Json {
                isLenient = true
                prettyPrint = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        return Retrofit
            .Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BuildConfig.TEST_URL + BuildConfig.NODE_PORT_NUMBER)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient() =
        OkHttpClient.Builder().run {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
            build()
        }
}