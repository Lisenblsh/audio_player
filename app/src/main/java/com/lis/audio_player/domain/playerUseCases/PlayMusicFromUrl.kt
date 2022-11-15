package com.lis.audio_player.domain.playerUseCases

import android.content.Context
import android.content.Intent
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.flatMap
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.domain.baseModels.AudioModel
import com.lis.audio_player.domain.networkModels.VkMusic
import com.lis.audio_player.domain.tools.convertToMusicModel
import com.lis.audio_player.domain.tools.exoPlayer.DiffAudioData
import com.lis.audio_player.domain.tools.exoPlayer.currentMediaItems
import com.lis.audio_player.presentation.service.PlayerService
import kotlinx.coroutines.launch
import retrofit2.Response

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

    private var page = 1

    fun setRepeatMode(repeatMode: Int) {
        exoPlayer.repeatMode = repeatMode
        this.repeatMode.value = repeatMode
    }

    fun setShuffleMode(isShuffle: Boolean) {
        exoPlayer.shuffleModeEnabled = isShuffle
        shuffleMode.value = isShuffle
    }

    lateinit var audiosList: ArrayList<AudioModel>
    val musicInfo = MutableLiveData<AudioModel>()

    val isPlaylistAdd = MutableLiveData<Boolean>(false)

    init {
        if (exoPlayer.isPlaying) {
            play()
        } else {
            pause()
        }
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
            addingAudioToPlayer()
            exoPlayer.prepare()
        }
    }

    private suspend fun addingAudioToPlayer() {
        audiosList = getMusicList(
            count = PAGE_SIZE,
            offset = PAGE_SIZE * (page - 1)
        ) as ArrayList<AudioModel>
        differ.add(audiosList)
        isPlaylistAdd.value = true
    }

    suspend fun updateAudioToPlayer() {
        Log.e("update","${audiosList.size}")
        audiosList.addAll(getMusicList(
            count = PAGE_SIZE,
            offset = PAGE_SIZE * (page - 1)
        ))
        Log.e("update","${audiosList.size}")
        differ.update(audiosList)
        isPlaylistAdd.value = true
    }


    fun setupMusicFromId(musicId: Long) {
            isPlaylistAdd.value = false
            for (i in exoPlayer.currentMediaItems.indices) {
                val media = exoPlayer.getMediaItemAt(i)
                if (media.mediaId == musicId.toString()) {
                    exoPlayer.seekTo(i, 0)
                    break
                }
            }
//            if (!isAudioFinding) {
//                viewModelScope.launch {
//                    addingAudioToPlayer()
//                    setupMusicFromId(musicId)
//                    isPlaylistAdd.value = true
//                }
//            }

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
        position.value = exoPlayer.currentPosition
    }

    fun prevSong() {
        exoPlayer.seekToPrevious()
        if (isPlaying.value == false) {
            position.value = exoPlayer.currentPosition
        }
    }

    private val updateSongTime: Runnable = object : Runnable {
        override fun run() {
            position.value = exoPlayer.currentPosition
            downloadPosition.value = exoPlayer.bufferedPosition
            myHandler.postDelayed(this, 100)
        }
    }

    private suspend fun  getMusicList(
        count: Int = PAGE_SIZE,
        offset: Int,
        ownerId: Long? = null,
        albumId: Long? = null,
        accessKey: String? = null
    ): List<AudioModel> {
        val response = repository.getMusicList(count, offset, ownerId, albumId, accessKey)
        page++
        return checkNotNull(response.body()).musicResponse.musicItems.convertToMusicModel()
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}