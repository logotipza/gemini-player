package com.gravitymusic.data.repository

import com.gravitymusic.core.util.DataError
import com.gravitymusic.core.util.Result
import com.gravitymusic.data.local.dao.FolderDao
import com.gravitymusic.data.local.entity.FolderPathEntity
import com.gravitymusic.data.mapper.toDomain
import com.gravitymusic.domain.model.Folder
import com.gravitymusic.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val dao: FolderDao
) : FolderRepository {

    override fun getAllFolders(): Flow<List<Folder>> {
        return dao.getAllFolders().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun addFolder(path: String): Result<Long, DataError.Local> {
        return try {
            val id = dao.insertFolder(
                FolderPathEntity(
                    path = path,
                    isScanned = false
                )
            )
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(DataError.Local.IO_EXCEPTION)
        }
    }
}
