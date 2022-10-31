package com.lis.audio_player.presentation.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.lis.audio_player.R
import com.lis.audio_player.presentation.PlayerActivity
import org.koin.androidx.viewmodel.scope.emptyState

class PlayerService : Service() {

    val serviceBinder: IBinder = ServiceBinder()

    lateinit var exoPlayer: ExoPlayer
    lateinit var playerNotificationManager: PlayerNotificationManager

    inner class ServiceBinder : Binder() {
        val playerService: PlayerService
            get() = this@PlayerService

    }

    override fun onBind(intent: Intent): IBinder {
        return serviceBinder
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        val channelId = resources.getString(R.string.app_name) + " Music Channel "
        val notificationId = 1111111
        playerNotificationManager = PlayerNotificationManager.Builder(this, notificationId, channelId)
            .setNotificationListener(notificationListener)
            .setMediaDescriptionAdapter(descriptionAdapter)
            .setChannelImportance(IMPORTANCE_HIGH)
            .setSmallIconResourceId(R.drawable.play)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setNextActionIconResourceId(R.drawable.next)
            .setPreviousActionIconResourceId(R.drawable.previous)
            .setPlayActionIconResourceId(R.drawable.play)
            .setPauseActionIconResourceId(R.drawable.pause)
            .setStopActionIconResourceId(R.drawable.stop_action)
            .setChannelNameResourceId(R.string.app_name)
            .build()

        playerNotificationManager.setPlayer(exoPlayer)
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_MAX)
        playerNotificationManager.setUseStopAction(true)
        playerNotificationManager.setUseRewindAction(false)
        playerNotificationManager.setUseFastForwardAction(false)
    }

    override fun onDestroy() {
        if(exoPlayer.isPlaying) exoPlayer.stop()
        playerNotificationManager.setPlayer(null)
        exoPlayer.release()
        stopForeground(true)
        stopSelf()
        super.onDestroy()
    }

    private val notificationListener: PlayerNotificationManager.NotificationListener = object: PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            super.onNotificationCancelled(notificationId, dismissedByUser)
            stopForeground(true)
            if(exoPlayer.isPlaying) {
                exoPlayer.pause()
            }
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            super.onNotificationPosted(notificationId, notification, ongoing)
            startForeground(notificationId, notification)
        }
    }

    private val descriptionAdapter: PlayerNotificationManager.MediaDescriptionAdapter = object: PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return  exoPlayer.currentMediaItem?.mediaMetadata?.title ?: ""
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            val openAppIntent = Intent(applicationContext, PlayerActivity::class.java)

            return PendingIntent.getActivity(applicationContext, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return null
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            return null
        }

    }




}