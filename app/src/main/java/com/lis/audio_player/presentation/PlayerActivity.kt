package com.lis.audio_player.presentation

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.lis.audio_player.R
import com.lis.audio_player.databinding.ActivityPlayerBinding
import com.lis.audio_player.domain.playerUseCases.GetMusicInfoFromId
import com.lis.audio_player.domain.playerUseCases.PlayMusicFromUrl
import com.lis.audio_player.domain.tools.Coil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.MessageFormat
import java.text.ParseException
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private val playMusicFromUrl: PlayMusicFromUrl by inject()

    private var musicId: Long = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bindElements()

        lifecycleScope.launch {
            getIntentMethod()
        }
    }

    private fun getIntentMethod() {
        musicId = intent.getLongExtra("music_id", -1)
    }

    private fun ActivityPlayerBinding.bindElements() {
        playMusicFromUrl.musicInfo.observe(this@PlayerActivity) { music ->
            Coil().setImage(music.photo1200, songImage)
            songName.isSelected = true
            songName.text = music.title
            songAuthor.text = music.artist

        }

        lifecycleScope.launch {
            TODO(
                "надо чтото делать с сингтоном наверное для этой активити" +
                        "надо убрать этот делей"
            )
            delay(2000)
            playMusicFromUrl.setupMusicFromId(musicId)
        }

        playMusicFromUrl.isPlaying.observe(this@PlayerActivity) { isPlaying: Boolean ->
            Coil().setImage(
                if (isPlaying) R.drawable.pause else R.drawable.play,
                buttonPlayPause
            )
        } //установка иконки play\pause кнопки

        buttonPlayPause.setOnClickListener { startClickListener() }
        buttonNext.setOnClickListener { nextClickListener() }
        buttonPrevious.setOnClickListener { prevClickListener() }

        songProgress.setOnSeekBarChangeListener(seekBarSelectProgressListener())

    }

    private fun startClickListener() {
        var isPlaying = playMusicFromUrl.isPlaying.value ?: false
        if (isPlaying) {
            playMusicFromUrl.pause()
        } else {
            playMusicFromUrl.play()
        }
        isPlaying = playMusicFromUrl.isPlaying.value ?: false
    }

    private fun nextClickListener() {
        playMusicFromUrl.nextSong()
    }

    private fun prevClickListener() {
        playMusicFromUrl.prevSong()
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
        playMusicFromUrl.destroy()
        super.onDestroy()
    }
}
