package com.gravitymusic.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gravitymusic.data.local.dao.FolderDao
import com.gravitymusic.data.local.dao.PlaylistDao
import com.gravitymusic.data.local.dao.TrackDao
import com.gravitymusic.data.local.entity.FolderPathEntity
import com.gravitymusic.data.local.entity.PlaylistEntity
import com.gravitymusic.data.local.entity.PlaylistTrackCrossRef
import com.gravitymusic.data.local.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        FolderPathEntity::class,
        PlaylistTrackCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GravityMusicDatabase : RoomDatabase() {
    abstract val trackDao: TrackDao
    abstract val playlistDao: PlaylistDao
    abstract val folderDao: FolderDao
}
