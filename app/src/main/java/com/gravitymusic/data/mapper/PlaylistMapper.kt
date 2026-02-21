package com.gravitymusic.data.mapper

import com.gravitymusic.data.local.entity.PlaylistEntity
import com.gravitymusic.domain.model.Playlist

fun PlaylistEntity.toDomain(trackIds: List<Long> = emptyList()): Playlist {
    return Playlist(
        id = id,
        name = name,
        createdAt = createdAt,
        trackIds = trackIds
    )
}

fun Playlist.toEntity(): PlaylistEntity {
    return PlaylistEntity(
        id = id,
        name = name,
        createdAt = createdAt
    )
}
