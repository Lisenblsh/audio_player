package com.lis.audio_player.domain.playerUseCases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.withTransaction
import com.lis.audio_player.data.room.MusicDatabase
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class GetMusicInfoFromId(private val database: MusicDatabase) {

    val title = MutableLiveData<String>()
    val artist = MutableLiveData<String>()

    val photo1200 = MutableLiveData<String>()
    val url = MutableLiveData<String>()

    suspend fun execute(musicId: Long){
        database.withTransaction {
            val music = database.musicDao().getMusicById(musicId)
            title.postValue(music.title)
            artist.postValue(music.artist)
            photo1200.postValue(music.photo1200)
            url.postValue(music.url)
        }
    }

}