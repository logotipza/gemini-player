package com.gravitymusic.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val trackIds: List<Long> = emptyList()
)
