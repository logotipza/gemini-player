package com.gravitymusic.core.util

sealed interface DataError: Error {
    enum class Local: DataError {
        DISK_FULL,
        IO_EXCEPTION,
        UNKNOWN
    }
    enum class Playback: DataError {
        FILE_NOT_FOUND,
        UNSUPPORTED_FORMAT,
        UNKNOWN
    }
}
