package com.lis.audio_player.domain.tools.exoPlayer

import com.lis.audio_player.domain.baseModels.AudioModel

interface AudioDataUpdaterDiff {
    suspend fun update(incoming: List<AudioModel>)

    suspend fun add(incoming: List<AudioModel>)
}