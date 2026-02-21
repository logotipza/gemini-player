package com.gravitymusic.data.repository

import com.gravitymusic.core.util.DataError
import com.gravitymusic.core.util.Result
import com.gravitymusic.data.remote.api.YandexDiskApi
import com.gravitymusic.domain.repository.CloudRepository
import com.gravitymusic.domain.repository.CloudResource
import com.gravitymusic.domain.repository.TokenRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CloudRepositoryImpl @Inject constructor(
    private val api: YandexDiskApi,
    private val tokenRepository: TokenRepository
) : CloudRepository {

    override suspend fun getFolderContents(path: String): Result<List<CloudResource>, DataError.Network> {
        return try {
            val token = tokenRepository.getToken().first() ?: return Result.Error(DataError.Network.UNAUTHORIZED)
            val authHeader = "OAuth $token"

            val response = api.getResources(authHeader, path)
            if (response.isSuccessful) {
                val items = response.body()?.embedded?.items ?: emptyList()
                val resources = items.map {
                    CloudResource(
                        name = it.name,
                        path = it.path,
                        type = it.type,
                        mimeType = it.mimeType
                    )
                }
                Result.Success(resources)
            } else {
                Result.Error(DataError.Network.SERVER_ERROR)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Network.NO_INTERNET)
        }
    }

    override suspend fun getDownloadLink(path: String): Result<String, DataError.Network> {
        return try {
            val token = tokenRepository.getToken().first() ?: return Result.Error(DataError.Network.UNAUTHORIZED)
            val authHeader = "OAuth $token"

            val response = api.getDownloadLink(authHeader, path)
            if (response.isSuccessful) {
                val href = response.body()?.href
                if (href != null) {
                    Result.Success(href)
                } else {
                    Result.Error(DataError.Network.SERVER_ERROR)
                }
            } else {
                Result.Error(DataError.Network.SERVER_ERROR)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Network.NO_INTERNET)
        }
    }
}
