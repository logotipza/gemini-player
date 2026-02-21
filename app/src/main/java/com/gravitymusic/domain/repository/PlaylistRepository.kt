package com.gravitymusic.domain.repository

import com.gravitymusic.core.util.DataError
import com.gravitymusic.core.util.Result
import com.gravitymusic.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String): Result<Long, DataError.Local>
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long): Result<Unit, DataError.Local>
}
