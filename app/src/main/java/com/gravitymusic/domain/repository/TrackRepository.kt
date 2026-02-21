package com.gravitymusic.domain.repository

import com.gravitymusic.core.util.DataError
import com.gravitymusic.core.util.Result
import com.gravitymusic.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getAllTracks(): Flow<List<Track>>
    fun getTracksByFolder(folderId: Long): Flow<List<Track>>
    suspend fun insertTrack(track: Track): Result<Unit, DataError.Local>
    suspend fun deleteTrack(trackId: Long): Result<Unit, DataError.Local>
}
