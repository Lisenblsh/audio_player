package com.lis.audio_player.domain

import com.lis.audio_player.data.network.Filters
import com.lis.audio_player.data.room.MusicDB
import com.lis.audio_player.domain.networkModels.VkAlbum
import com.lis.audio_player.domain.networkModels.VkMusic
import retrofit2.Response

interface MusicRepository {
    suspend fun getMusicList(
        count: Int,
        offset: Int,
        ownerId: Long? = null,
        albumId: Long? = null,
        accessKey: String? = null
    ): Response<VkMusic>


    suspend fun getAlbumList(
        ownerId: Long? = null,
        count: Int,
        offset: Int,
        extended: Int? = null,
        fields: String? = null,
        filters: Filters? = null,
    ): Response<VkAlbum>

    suspend fun getMusicListFromDB(): List<MusicDB>
}