package dev.nikita_chernikov.lab4

import kotlinx.serialization.Serializable

@Serializable
data class WebRadioSongResponse(val artist: String, val title: String)
