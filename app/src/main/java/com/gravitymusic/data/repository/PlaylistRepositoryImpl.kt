package com.gravitymusic.data.repository

import com.gravitymusic.core.util.DataError
import com.gravitymusic.core.util.Result
import com.gravitymusic.data.local.dao.PlaylistDao
import com.gravitymusic.data.local.entity.PlaylistEntity
import com.gravitymusic.data.local.entity.PlaylistTrackCrossRef
import com.gravitymusic.data.mapper.toDomain
import com.gravitymusic.domain.model.Playlist
import com.gravitymusic.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val dao: PlaylistDao
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return dao.getAllPlaylists().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createPlaylist(name: String): Result<Long, DataError.Local> {
        return try {
            val id = dao.insertPlaylist(
                PlaylistEntity(
                    name = name,
                    createdAt = System.currentTimeMillis()
                )
            )
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(DataError.Local.IO_EXCEPTION)
        }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long): Result<Unit, DataError.Local> {
        return try {
            dao.insertPlaylistTrackCrossRef(
                PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId)
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.IO_EXCEPTION)
        }
    }
}
