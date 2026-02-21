package com.gravitymusic.domain.usecase

import com.gravitymusic.domain.model.Track
import com.gravitymusic.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    operator fun invoke(): Flow<List<Track>> {
        return repository.getAllTracks()
    }
}
