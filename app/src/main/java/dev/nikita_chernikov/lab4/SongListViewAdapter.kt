package dev.nikita_chernikov.lab4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class SongListViewAdapter(private val songs: MutableList<Song>) : RecyclerView.Adapter<SongListViewAdapter.ViewHolderClass>() {
    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameTextView: TextView = itemView.findViewById(R.id.id_full_name)
        val createdAtTextView: TextView = itemView.findViewById(R.id.created_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_item_layout, parent, false)
        return ViewHolderClass(view)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val song = songs[position]

        holder.fullNameTextView.text = holder.itemView.context.getString(
            R.string.id_full_name_text,
            song.id,
            song.artist,
            song.title
        )
        holder.createdAtTextView.text = holder.itemView.context.getString(
            R.string.created_at_text,
            SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(song.createdAt)
        )
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}
