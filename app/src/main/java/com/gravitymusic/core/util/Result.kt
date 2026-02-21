package com.gravitymusic.core.util

sealed interface Result<out D, out E: com.gravitymusic.core.util.Error> {
    data class Success<out D, out E: com.gravitymusic.core.util.Error>(val data: D): Result<D, E>
    data class Error<out D, out E: com.gravitymusic.core.util.Error>(val error: E): Result<D, E>
}
