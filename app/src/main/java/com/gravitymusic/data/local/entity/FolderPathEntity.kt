package com.gravitymusic.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderPathEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val path: String,
    val isScanned: Boolean
)
