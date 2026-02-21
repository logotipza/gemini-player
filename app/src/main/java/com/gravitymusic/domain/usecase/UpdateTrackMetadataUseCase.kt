package com.gravitymusic.domain.usecase

import android.net.Uri
import com.gravitymusic.core.util.DataError
import com.gravitymusic.core.util.Result
import com.gravitymusic.data.local.metadata.MetadataManager
import com.gravitymusic.domain.repository.TrackRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateTrackMetadataUseCase @Inject constructor(
    private val metadataManager: MetadataManager,
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(
        trackId: Long,
        newTitle: String?,
        newArtist: String?,
        newAlbum: String?
    ): Result<Unit, DataError.Local> {
        val tracks = trackRepository.getAllTracks().first()
        val track = tracks.find { it.id == trackId } ?: return Result.Error(DataError.Local.UNKNOWN)

        val success = metadataManager.updateMetadata(
            uri = Uri.parse(track.uri),
            title = newTitle,
            artist = newArtist,
            album = newAlbum
        )

        return if (success) {
            val updatedTrack = track.copy(
                title = newTitle ?: track.title,
                artist = newArtist ?: track.artist,
                album = newAlbum ?: track.album
            )
            trackRepository.insertTrack(updatedTrack)
            Result.Success(Unit)
        } else {
            Result.Error(DataError.Local.IO_EXCEPTION)
        }
    }
}
