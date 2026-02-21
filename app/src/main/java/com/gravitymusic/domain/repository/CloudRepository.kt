package com.gravitymusic.domain.repository

import com.gravitymusic.core.util.DataError
import com.gravitymusic.core.util.Result

interface CloudRepository {
    suspend fun getFolderContents(path: String): Result<List<CloudResource>, DataError.Network>
    suspend fun getDownloadLink(path: String): Result<String, DataError.Network>
}

data class CloudResource(
    val name: String,
    val path: String,
    val type: String, // "dir" or "file"
    val mimeType: String?
)
