package dev.nikita_chernikov.lab4

import java.util.Date

data class Song(var id: Int = 0, var artist : String, var title: String, val createdAt: Date = Date())
