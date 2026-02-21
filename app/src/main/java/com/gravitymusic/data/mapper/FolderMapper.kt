package com.gravitymusic.data.mapper

import com.gravitymusic.data.local.entity.FolderPathEntity
import com.gravitymusic.domain.model.Folder

fun FolderPathEntity.toDomain(): Folder {
    return Folder(
        id = id,
        path = path,
        isScanned = isScanned
    )
}

fun Folder.toEntity(): FolderPathEntity {
    return FolderPathEntity(
        id = id,
        path = path,
        isScanned = isScanned
    )
}
