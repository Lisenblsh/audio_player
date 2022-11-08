package com.lis.audio_player.domain.tools

import com.lis.audio_player.domain.adapters.MusicPagingAdapter
import com.lis.audio_player.domain.baseModels.AudioModel
import com.lis.audio_player.domain.tools.exoPlayer.AudioDataUpdaterDiff
import kotlin.coroutines.CoroutineContext

class DiffAudio(
    private val context: CoroutineContext,
    private val adapter: MusicPagingAdapter,
):AudioDataUpdaterDiff {
    private val oldData: List<AudioModel>? = null
    override suspend fun update(incoming: List<AudioModel>) {

    }

    override suspend fun add(incoming: List<AudioModel>) {
        TODO("Not yet implemented")
    }
}