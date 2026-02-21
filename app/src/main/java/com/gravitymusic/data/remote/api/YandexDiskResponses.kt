package com.gravitymusic.data.remote.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResourcesResponse(
    @Json(name = "_embedded") val embedded: EmbeddedResources?
)

@JsonClass(generateAdapter = true)
data class EmbeddedResources(
    @Json(name = "items") val items: List<ResourceItem>?
)

@JsonClass(generateAdapter = true)
data class ResourceItem(
    @Json(name = "name") val name: String,
    @Json(name = "path") val path: String,
    @Json(name = "type") val type: String,
    @Json(name = "mime_type") val mimeType: String?
)

@JsonClass(generateAdapter = true)
data class DownloadLinkResponse(
    @Json(name = "href") val href: String,
    @Json(name = "method") val method: String,
    @Json(name = "templated") val templated: Boolean
)
