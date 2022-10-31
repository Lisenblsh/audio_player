package com.lis.audio_player.domain.tools.exoPlayer

import com.lis.audio_player.data.room.MusicDB

interface AudioDataUpdaterDiff {
    suspend fun update(incoming: List<MusicDB>)

    suspend fun add(incoming: List<MusicDB>)
}