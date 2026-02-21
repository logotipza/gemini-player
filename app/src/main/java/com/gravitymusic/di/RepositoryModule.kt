package com.gravitymusic.di

import com.gravitymusic.data.local.GravityMusicDatabase
import com.gravitymusic.data.local.dao.FolderDao
import com.gravitymusic.data.local.dao.PlaylistDao
import com.gravitymusic.data.local.dao.TrackDao
import com.gravitymusic.data.repository.FolderRepositoryImpl
import com.gravitymusic.data.repository.PlaylistRepositoryImpl
import com.gravitymusic.data.repository.TrackRepositoryImpl
import com.gravitymusic.domain.repository.FolderRepository
import com.gravitymusic.domain.repository.PlaylistRepository
import com.gravitymusic.domain.repository.TrackRepository
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
    fun provideTrackDao(database: GravityMusicDatabase): TrackDao {
        return database.trackDao
    }

    @Provides
    @Singleton
    fun providePlaylistDao(database: GravityMusicDatabase): PlaylistDao {
        return database.playlistDao
    }

    @Provides
    @Singleton
    fun provideFolderDao(database: GravityMusicDatabase): FolderDao {
        return database.folderDao
    }

    @Provides
    @Singleton
    fun provideTrackRepository(dao: TrackDao): TrackRepository {
        return TrackRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(dao: PlaylistDao): PlaylistRepository {
        return PlaylistRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideFolderRepository(dao: FolderDao): FolderRepository {
        return FolderRepositoryImpl(dao)
    }
}
