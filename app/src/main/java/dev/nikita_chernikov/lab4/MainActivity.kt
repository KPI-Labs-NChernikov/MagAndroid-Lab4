package dev.nikita_chernikov.lab4

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.nikita_chernikov.lab4.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sqliteManager: SQLiteManager
    private lateinit var viewModel: SongDownloaderViewModel
    private lateinit var songs: MutableList<Song>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sqliteManager = SQLiteManager.getInstance(this)
        songs = sqliteManager.getSongs()

        recyclerView = binding.rvSongs
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val adapter = SongListViewAdapter(songs)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            SongDownloaderViewModelFactory(application as App, sqliteManager)
        )[SongDownloaderViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.songFlow.collect { songResult ->
                    if (songResult.song != null)
                    {
                        songs.add(0, songResult.song)
                        adapter.notifyItemInserted(0)
                    }
                    if (songResult.error != ErrorType.Undefined)
                    {
                        val errorMessage = when (songResult.error)
                        {
                            ErrorType.NoInternetConnection -> R.string.no_internet_error
                            else -> R.string.unknown_error
                        }
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
