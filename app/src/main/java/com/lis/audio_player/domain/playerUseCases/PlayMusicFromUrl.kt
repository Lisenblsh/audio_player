package com.lis.audio_player.domain.playerUseCases

import android.content.Context
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.data.room.MusicDB
import com.lis.audio_player.domain.tools.exoPlayer.DiffAudioData
import com.lis.audio_player.domain.tools.exoPlayer.currentMediaItems
import kotlinx.coroutines.launch

class PlayMusicFromUrl(
    context: Context,
    private val repository: MusicRepositoryImpl
) : ViewModel() {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private val myHandler = Handler(Looper.getMainLooper())
    private val differ = DiffAudioData(context = viewModelScope.coroutineContext, exoPlayer)

    val position = MutableLiveData<Long>(0)
    val duration = MutableLiveData<Long>(0)
    val downloadPosition = MutableLiveData<Long>(0)
    val isPlaying = MutableLiveData<Boolean>(false)
    val repeatMode = MutableLiveData<Int>(exoPlayer.repeatMode)

    fun setRepeatMode(repeatMode: Int) {
        exoPlayer.repeatMode = repeatMode
        this.repeatMode.value = repeatMode
    }

    private lateinit var audiosList: ArrayList<MusicDB>
    val musicInfo = MutableLiveData<MusicDB>()


    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY || playbackState == PlaybackState.STATE_FAST_FORWARDING) {
                    duration.value = exoPlayer.duration
                    musicInfo.value =
                        audiosList.find { it.musicId.toString() == exoPlayer.currentMediaItem?.mediaId }
                }
            }
        })
        viewModelScope.launch {
            audiosList = (getMusicList() ?: emptyList()) as ArrayList<MusicDB>
            differ.add(audiosList)
            exoPlayer.prepare()
        }
    }

    fun setupMusicFromId(musicId: Long) {
        for (i in exoPlayer.currentMediaItems.indices) {
            val media = exoPlayer.getMediaItemAt(i)
            Log.e("mediaId", "${media.mediaId}:$musicId")
            if (media.mediaId == musicId.toString()) {
                Log.e("mediaId True", "${media.mediaId}:$musicId")
                exoPlayer.seekTo(i,0)
                break
            }
        }
    }

    fun play() {
        exoPlayer.play()
        isPlaying.value = exoPlayer.isPlaying

        myHandler.postDelayed(updateSongTime, 100)
    }

    fun pause() {
        exoPlayer.pause()
        isPlaying.value = exoPlayer.isPlaying
        myHandler.removeCallbacks(updateSongTime)
    }

    fun seekTo(progress: Long) {
        exoPlayer.seekTo(progress)
        position.postValue(exoPlayer.currentPosition)
    }

    fun nextSong() {
        exoPlayer.seekToNextMediaItem()

    }

    fun prevSong() {
        exoPlayer.seekToPrevious()
    }

    fun destroy(){
        exoPlayer.release()
    }

    private val updateSongTime: Runnable = object : Runnable {
        override fun run() {
            position.value = exoPlayer.currentPosition
            downloadPosition.value = exoPlayer.bufferedPosition
            myHandler.postDelayed(this, 100)
        }
    }

    private suspend fun getMusicList(): List<MusicDB> {
        return repository.getMusicListFromDB()
    }

}