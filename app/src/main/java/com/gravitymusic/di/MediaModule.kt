package com.gravitymusic.di

import android.content.Context
import com.gravitymusic.media.controller.MusicController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideMusicController(
        @ApplicationContext context: Context
    ): MusicController {
        return MusicController(context).apply {
            initialize()
        }
    }
}
