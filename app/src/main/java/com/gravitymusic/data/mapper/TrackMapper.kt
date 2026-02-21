package com.gravitymusic.data.mapper

import com.gravitymusic.data.local.entity.TrackEntity
import com.gravitymusic.domain.model.Track

fun TrackEntity.toDomain(): Track {
    return Track(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        uri = uri,
        folderId = folderId
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        uri = uri,
        folderId = folderId
    )
}
