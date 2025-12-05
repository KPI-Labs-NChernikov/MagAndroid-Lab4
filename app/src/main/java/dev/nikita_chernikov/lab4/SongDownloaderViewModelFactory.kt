package dev.nikita_chernikov.lab4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SongDownloaderViewModelFactory(
    private val app: App,
    private val repo: SQLiteManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongDownloaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SongDownloaderViewModel(app, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
