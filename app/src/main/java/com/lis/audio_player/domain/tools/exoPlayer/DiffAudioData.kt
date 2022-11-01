package com.lis.audio_player.domain.tools.exoPlayer

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.github.difflib.DiffUtils
import com.github.difflib.patch.AbstractDelta
import com.github.difflib.patch.DeltaType
import com.github.difflib.patch.Patch
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.lis.audio_player.data.room.MusicDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL
import kotlin.coroutines.CoroutineContext

class DiffAudioData(private val context: CoroutineContext, private val player: ExoPlayer) :
    AudioDataUpdaterDiff {

    override suspend fun update(incoming: List<MusicDB>) {
        val oldData = player.currentMediaItems
        val newData = incoming.toMediaItems()

        val patch: Patch<MediaItem> = withContext(context) {
            DiffUtils.diff(oldData, newData)
        }

        patch.deltas.forEach { delta ->
            when (delta.type) {
                DeltaType.CHANGE -> {
                    delta.delete(player)
                    delta.insert(player)
                }
                DeltaType.DELETE -> {
                    delta.delete(player)
                }
                DeltaType.INSERT -> {
                    delta.insert(player)
                }
                DeltaType.EQUAL -> {}
                null -> {}
            }
        }
    }

    override suspend fun add(incoming: List<MusicDB>) {
        player.addMediaItems(incoming.toMediaItems())
    }

    private fun AbstractDelta<MediaItem>.delete(player: ExoPlayer) {
        player.removeMediaItems(target.position, target.position + source.lines.size)
    }

    private fun AbstractDelta<MediaItem>.insert(player: ExoPlayer) {
        player.addMediaItems(target.position, target.lines)
    }

    private fun List<MusicDB>.toMediaItems(): List<MediaItem> =
        map { data ->
            val mediaMetadata = MediaMetadata.Builder()
                .setArtist(data.artist)
                .setTitle(data.title)
                .setArtworkUri(Uri.parse(data.photo300))
                .build()
            MediaItem.Builder()
                .setUri(data.url)
                .setMediaId(data.musicId.toString())
                .setMediaMetadata(mediaMetadata)
                .build()
        }
}
