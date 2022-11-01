package com.lis.audio_player.domain.playerUseCases

import android.content.Context
import android.content.Intent
import android.media.session.PlaybackState
import android.net.Uri
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
import com.lis.audio_player.presentation.service.PlayerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlayMusicFromUrl(
    context: Context,
    private val repository: MusicRepositoryImpl,
    private val exoPlayer: ExoPlayer
) : ViewModel() {
    //private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private val myHandler = Handler(Looper.getMainLooper())
    private val differ = DiffAudioData(context = viewModelScope.coroutineContext, exoPlayer)

    val position = MutableLiveData<Long>(0)
    val duration = MutableLiveData<Long>(0)
    val downloadPosition = MutableLiveData<Long>(0)
    val isPlaying = MutableLiveData<Boolean>(false)
    val repeatMode = MutableLiveData<Int>(exoPlayer.repeatMode)
    val shuffleMode = MutableLiveData<Boolean>(false)

    fun setRepeatMode(repeatMode: Int) {
        exoPlayer.repeatMode = repeatMode
        this.repeatMode.value = repeatMode
    }

    fun setShuffleMode(isShuffle: Boolean){
        exoPlayer.shuffleModeEnabled = isShuffle
        shuffleMode.value = isShuffle
    }

    private lateinit var audiosList: ArrayList<MusicDB>
    val musicInfo = MutableLiveData<MusicDB>()

    val isPlaylistAdd = MutableLiveData<Boolean>(false)

    init {
        context.startService(Intent(context.applicationContext, PlayerService::class.java))
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
            isPlaylistAdd.value = true
            exoPlayer.prepare()
        }
    }

    fun setupMusicFromId(musicId: Long) {
        for (i in exoPlayer.currentMediaItems.indices) {
            val media = exoPlayer.getMediaItemAt(i)
            Log.e("mediaId", "${media.mediaId}:$musicId")
            if (media.mediaId == musicId.toString()) {
                Log.e("mediaId True", "${media.mediaId}:$musicId")
                exoPlayer.seekTo(i, 0)
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
        position.value = exoPlayer.currentPosition
    }

    fun nextSong() {
        exoPlayer.seekToNextMediaItem()
        position.value = 0
    }

    fun prevSong() {
        exoPlayer.seekToPrevious()
        if(isPlaying.value==false){
            position.value = 0
        }
    }

    fun destroy() {
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