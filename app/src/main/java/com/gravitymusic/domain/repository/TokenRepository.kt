package com.gravitymusic.domain.repository

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun saveToken(token: String)
    suspend fun clearToken()
    fun getToken(): Flow<String?>
}
