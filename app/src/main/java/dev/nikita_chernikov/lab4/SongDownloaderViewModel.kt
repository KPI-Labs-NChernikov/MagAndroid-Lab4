package dev.nikita_chernikov.lab4

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class SongDownloaderViewModel(application: App, val sqliteManager: SQLiteManager) : AndroidViewModel(application) {
    private var previousIsOnline = true
    private val isGlobalOnline = getApplication<App>().isGlobalOnline

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val _songFlow = MutableSharedFlow<SongDownloadResult>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val songFlow = _songFlow.asSharedFlow()

    init {
        startDownloading()
    }

    private fun startDownloading()
    {
        viewModelScope.launch {
            while (isActive) {
                val isOnline = isGlobalOnline.value
                if (isOnline)
                {
                    downloadSong()
                }
                if (!isOnline && previousIsOnline)
                {
                    _songFlow.emit(SongDownloadResult(error = ErrorType.NoInternetConnection))
                }
                previousIsOnline = isOnline
                delay(20_000L)
            }
        }
    }

    private suspend fun downloadSong()
    {
        try {
            val songResponse = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://webradio.io/api/radio/pi/current-song")
                    .build()

                client.newCall(request).execute().use { response ->
                    val body = response.body?.string() ?: throw IllegalArgumentException("Body cannot be null")
                    json.decodeFromString<WebRadioSongResponse>(body)
                }
            }

            val lastSong = sqliteManager.getLastSong()
            if (lastSong != null && lastSong.artist == songResponse.artist && lastSong.title == songResponse.title)
            {
                return
            }

            val song = Song(title = songResponse.title, artist = songResponse.artist)
            sqliteManager.addSong(song)
            _songFlow.emit(SongDownloadResult(song = song))
        }
        catch (e: Exception) {
            Log.e("SongDownloaderViewModel", "Error while loading data", e)
        }
    }
}
