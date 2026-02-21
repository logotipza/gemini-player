package com.gravitymusic.di

import android.content.Context
import com.gravitymusic.data.remote.api.YandexDiskApi
import com.gravitymusic.data.repository.CloudRepositoryImpl
import com.gravitymusic.data.repository.TokenRepositoryImpl
import com.gravitymusic.domain.repository.CloudRepository
import com.gravitymusic.domain.repository.TokenRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenRepository(@ApplicationContext context: Context): TokenRepository {
        return TokenRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideYandexDiskApi(client: OkHttpClient): YandexDiskApi {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://cloud-api.yandex.net/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(YandexDiskApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCloudRepository(
        api: YandexDiskApi,
        tokenRepository: TokenRepository
    ): CloudRepository {
        return CloudRepositoryImpl(api, tokenRepository)
    }
}
