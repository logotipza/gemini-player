package com.gravitymusic.di

import android.content.Context
import androidx.room.Room
import com.gravitymusic.data.local.GravityMusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGravityMusicDatabase(
        @ApplicationContext context: Context
    ): GravityMusicDatabase {
        return Room.databaseBuilder(
            context,
            GravityMusicDatabase::class.java,
            "gravity_music_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): androidx.work.WorkManager {
        return androidx.work.WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideEqualizerManager(): com.gravitymusic.media.equalizer.EqualizerManager {
        return com.gravitymusic.media.equalizer.EqualizerManager()
    }
}
