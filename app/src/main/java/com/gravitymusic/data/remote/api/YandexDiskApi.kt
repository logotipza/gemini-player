package com.gravitymusic.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YandexDiskApi {
    @GET("v1/disk/resources")
    suspend fun getResources(
        @Header("Authorization") authHeader: String,
        @Query("path") path: String,
        @Query("limit") limit: Int = 1000,
        @Query("fields") fields: String = "_embedded.items.name,_embedded.items.path,_embedded.items.mime_type,_embedded.items.type"
    ): retrofit2.Response<ResourcesResponse>

    @GET("v1/disk/resources/download")
    suspend fun getDownloadLink(
        @Header("Authorization") authHeader: String,
        @Query("path") path: String
    ): retrofit2.Response<DownloadLinkResponse>
}
