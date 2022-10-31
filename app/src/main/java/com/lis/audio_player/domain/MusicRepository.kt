package com.lis.audio_player.domain

import com.lis.audio_player.data.room.MusicDB
import com.lis.audio_player.domain.models.VkMusic
import retrofit2.Response

interface MusicRepository {
    suspend fun getMusicList(
        count: Int,
        offset: Int,
        ownerId: Long? = null,
        albumId: Long? = null,
        accessKey: String? = null
    ): Response<VkMusic>

    suspend fun getMusicListFromDB(): List<MusicDB>
}