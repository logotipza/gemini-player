package com.gravitymusic.data.repository

import com.gravitymusic.core.util.DataError
import com.gravitymusic.core.util.Result
import com.gravitymusic.data.local.dao.TrackDao
import com.gravitymusic.data.mapper.toDomain
import com.gravitymusic.data.mapper.toEntity
import com.gravitymusic.domain.model.Track
import com.gravitymusic.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val dao: TrackDao
) : TrackRepository {

    override fun getAllTracks(): Flow<List<Track>> {
        return dao.getAllTracks().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTracksByFolder(folderId: Long): Flow<List<Track>> {
        return dao.getTracksByFolder(folderId).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun insertTrack(track: Track): Result<Unit, DataError.Local> {
        return try {
            dao.insertTrack(track.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.IO_EXCEPTION)
        }
    }

    override suspend fun deleteTrack(trackId: Long): Result<Unit, DataError.Local> {
        return try {
            dao.deleteTrack(trackId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.IO_EXCEPTION)
        }
    }
}
