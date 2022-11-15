package com.lis.audio_player.data.repository

import android.util.Log
import com.lis.audio_player.data.network.Filters
import com.lis.audio_player.data.network.retrofit.RetrofitService
import com.lis.audio_player.data.room.MusicDB
import com.lis.audio_player.data.room.MusicDao
import com.lis.audio_player.domain.MusicRepository
import com.lis.audio_player.domain.networkModels.VkMusic
import retrofit2.Response

class MusicRepositoryImpl(
    private val service: RetrofitService,
    private val token: String,
    private val dao: MusicDao,
    private val userId: Long
) : MusicRepository {
    override suspend fun getMusicList(
        count: Int, offset: Int, ownerId: Long?, albumId: Long?, accessKey: String?
    ) = service.getAudio(token, count, offset, ownerId ?: userId, albumId, accessKey)

    override suspend fun getAlbumList(
        ownerId: Long?, count: Int, offset: Int, extended: Int?, fields: String?, filters: Filters?
    ) = service.getPlaylistsAudio(
        token, ownerId ?: userId, count, offset, extended, fields, filters
    )

    override suspend fun getMusicListFromDB(): List<MusicDB> {
        return dao.getMusicList()
    }
}

