package com.lis.audio_player.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.lis.audio_player.R
import com.lis.audio_player.databinding.ActivityPlayerBinding
import com.lis.audio_player.domain.playerUseCases.PlayMusicFromUrl
import com.lis.audio_player.domain.tools.ImageLoader
import com.lis.audio_player.presentation.service.PlayerService
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.MessageFormat
import java.text.ParseException
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val playMusicFromUrl: PlayMusicFromUrl by inject{ parametersOf(exoPlayer)}

    private var musicId: Long = -1

    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindToService()

    }

    var isBound = false

    private fun bindToService() {
        val playerServiceIntent = Intent(this@PlayerActivity, PlayerService::class.java)
        bindService(playerServiceIntent, playerServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private val playerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.ServiceBinder
            exoPlayer = binder.playerService.exoPlayer
            isBound = true

            binding.bindElements()
            lifecycleScope.launch {
                getIntentMethod()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }

    }

    private fun getIntentMethod() {
        musicId = intent.getLongExtra("music_id", -1)
    }

    private fun ActivityPlayerBinding.bindElements() {
        val imageLoader = ImageLoader()
        playMusicFromUrl.musicInfo.observe(this@PlayerActivity) { music ->
            imageLoader.setImage(music.photo1200, songImage)
            imageLoader.setImageOnBackground(music.photo1200, backgroundImage)
            songName.isSelected = true
            songName.text = music.title
            songAuthor.text = music.artist

        }

        playMusicFromUrl.duration.observe(this@PlayerActivity) { songDuration: Long ->
            songProgress.max = songDuration.toInt()
            this.songDuration.text = getStringSongDuration(songDuration)
        }
        playMusicFromUrl.position.observe(this@PlayerActivity) { position: Long ->
            songPosition.text = getStringSongDuration(position)
            songProgress.progress = position.toInt()
        } //установка текущей позиции
        playMusicFromUrl.downloadPosition.observe(this@PlayerActivity) { downloadPosition: Long ->
            songProgress.secondaryProgress = downloadPosition.toInt()
        }

        playMusicFromUrl.isPlaylistAdd.observe(this@PlayerActivity) {
            if (it) {
                lifecycleScope.launch {
                    playMusicFromUrl.setupMusicFromId(musicId)
                }
            }
        }

        playMusicFromUrl.repeatMode.observe(this@PlayerActivity) { repeatMode: Int ->
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> imageLoader.setImage(
                    R.drawable.repeate_mode_one,
                    buttonLoop
                )
                Player.REPEAT_MODE_ONE -> imageLoader.setImage(
                    R.drawable.repeate_mode_all,
                    buttonLoop
                )
                Player.REPEAT_MODE_ALL -> imageLoader.setImage(
                    R.drawable.repeat_mode_off,
                    buttonLoop
                )
            }
        } // установка иконки зацикливания

        playMusicFromUrl.isPlaying.observe(this@PlayerActivity) { isPlaying: Boolean ->
            imageLoader.setImage(
                if (isPlaying) R.drawable.pause else R.drawable.play,
                buttonPlayPause
            )
        } //установка иконки play\pause кнопки

        playMusicFromUrl.shuffleMode.observe(this@PlayerActivity) { isShuffled ->
            if (isShuffled) {
                buttonShuffle.alpha = 1f
            } else {
                buttonShuffle.alpha = 0.5f
            }
        }

        buttonPlayPause.setOnClickListener { startClickListener() }
        buttonNext.setOnClickListener { nextClickListener() }
        buttonPrevious.setOnClickListener { prevClickListener() }
        buttonLoop.setOnClickListener { loopClickListener() }
        buttonShuffle.setOnClickListener { shuffleClickListener() }

        songProgress.setOnSeekBarChangeListener(seekBarSelectProgressListener())

    }

    private fun startClickListener() {
        val isPlaying = playMusicFromUrl.isPlaying.value == true
        if (isPlaying) {
            playMusicFromUrl.pause()
        } else {
            playMusicFromUrl.play()
        }
    }

    private fun nextClickListener() {
        playMusicFromUrl.nextSong()
    }

    private fun prevClickListener() {
        playMusicFromUrl.prevSong()
    }

    private var repeatMode = Player.REPEAT_MODE_OFF

    private fun loopClickListener() {
        when (repeatMode) {
            Player.REPEAT_MODE_OFF -> repeatMode = Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> repeatMode = Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> repeatMode = Player.REPEAT_MODE_OFF
        }
        playMusicFromUrl.setRepeatMode(repeatMode)
    }

    private fun seekBarSelectProgressListener(): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                playMusicFromUrl.seekTo(seekBar.progress.toLong())
            }
        }
    }

    private fun shuffleClickListener() {
        val isShuffle = playMusicFromUrl.shuffleMode.value == true
        playMusicFromUrl.setShuffleMode(!isShuffle)
    }

    private fun getStringSongDuration(songDuration: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(songDuration).toInt()
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(songDuration)
                - TimeUnit.HOURS.toMinutes(hours.toLong())).toInt()
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(songDuration)
                - TimeUnit.MINUTES.toSeconds(minutes.toLong())
                - TimeUnit.HOURS.toSeconds(hours.toLong())).toInt()
        return convertTimeFormat(hours, minutes, seconds)
    }

    private fun convertTimeFormat(hours: Int, minutes: Int, seconds: Int): String {
        var oldFormat = "m:s"
        var newFormat = "mm:ss"
        var stringSongDuration = MessageFormat.format("{0}:{1}", minutes, seconds)
        if (hours > 0) {
            oldFormat = "h:m:s"
            newFormat = "hh:mm:ss"
            stringSongDuration = MessageFormat.format("{0}:{1}:{2}", hours, minutes, seconds)
        }
        try {
            stringSongDuration = SimpleDateFormat(newFormat, Locale.getDefault())
                .format(SimpleDateFormat(oldFormat, Locale.getDefault()).parse(stringSongDuration))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return stringSongDuration
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

    private fun doUnbindService() {
        if (isBound) {
            unbindService(playerServiceConnection)
            isBound = false
        }
    }
}
