package com.lis.audio_player.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MusicDB::class, AlbumDB::class, ArtistDB::class, RemoteMusicKeys::class, RemoteAlbumKeys::class],
    version = 1
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        fun getInstance(context: Context) =
            buildDatabase(context)

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MusicDatabase::class.java,
                "music_db"
            ).fallbackToDestructiveMigration().build()
    }
}