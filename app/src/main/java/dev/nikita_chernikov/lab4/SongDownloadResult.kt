package dev.nikita_chernikov.lab4

data class SongDownloadResult(val song: Song? = null, val error: ErrorType = ErrorType.Undefined)
