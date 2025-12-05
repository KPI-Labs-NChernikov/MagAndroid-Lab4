package dev.nikita_chernikov.lab4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Date

class SQLiteManager( context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object
    {
        private const val DATABASE_NAME: String = "SongsDB"
        private const val DATABASE_VERSION: Int = 2

        private const val TABLE_NAME: String = "Songs"

        private const val ID_FIELD: String = "Id"
        private const val ARTIST_FIELD: String = "Artist"
        private const val TITLE_FIELD: String = "Title"
        private const val CREATED_AT_FIELD: String = "CreatedAt"

        @Volatile
        private var INSTANCE: SQLiteManager? = null

        fun getInstance(context: Context): SQLiteManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SQLiteManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL ( """CREATE TABLE $TABLE_NAME (
            $ID_FIELD INTEGER PRIMARY KEY AUTOINCREMENT,
            $ARTIST_FIELD TEXT NOT NULL,
            $TITLE_FIELD TEXT NOT NULL,
            $CREATED_AT_FIELD INTEGER NOT NULL)""".trimMargin())
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addSong(song: Song) {
        val values = ContentValues().apply {
            put(ARTIST_FIELD, song.artist)
            put(TITLE_FIELD, song.title)
            put(CREATED_AT_FIELD, song.createdAt.time)
        }

        val db = writableDatabase
        val id = db.insert(TABLE_NAME, null, values)
        song.id = id.toInt()
    }

    fun getLastSong() : Song?
    {
        val db = this.readableDatabase

        var song: Song? = null

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME ORDER BY $ID_FIELD DESC LIMIT 1",
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                song = Song(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    Date(cursor.getLong(3))
                )
            }
        }

        return song
    }

    fun getSongs() : ArrayList<Song> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $ID_FIELD DESC", null)

        val songs = ArrayList<Song>()
        cursor.use {
            while (cursor.moveToNext())
            {
                val song = Song(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    Date(cursor.getLong(3))
                )
                songs.add(song)
            }
        }

        return songs
    }
}
