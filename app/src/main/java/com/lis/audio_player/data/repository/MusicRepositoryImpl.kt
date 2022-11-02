package com.lis.audio_player.data.repository

import android.util.Log
import com.lis.audio_player.data.network.Filters
import com.lis.audio_player.data.network.retrofit.RetrofitService
import com.lis.audio_player.data.room.MusicDB
import com.lis.audio_player.data.room.MusicDao
import com.lis.audio_player.domain.MusicRepository
import com.lis.audio_player.domain.networkModels.VkAlbum
import retrofit2.Response
class MusicRepositoryImpl(
    private val service: RetrofitService,
    private val token: String,
    private val dao: MusicDao,
    private val userId: Long
) : MusicRepository {
    override suspend fun getMusicList(
        count: Int,
        offset: Int,
        ownerId: Long?,
        albumId: Long?,
        accessKey: String?
    ) = service.getAudio(token, count, offset, ownerId, albumId, accessKey)

    override suspend fun getAlbumList(
        ownerId: Long?,
        count: Int,
        offset: Int,
        extended: Int?,
        fields: String?,
        filters: Filters?
    ): Response<VkAlbum> {
        Log.e("ownerId","owner = $ownerId| user = $userId")
        val asd = ownerId ?: userId
        Log.e("ownerId2","asd = $asd")
        val response = service.getPlaylistsAudio(token, asd , count, offset, extended, fields, filters)
        Log.e("ownerId2","${response.raw().request.url}")
        Log.e("ownerId2","${response.body()}")
        return response
    }

    override suspend fun getMusicListFromDB(): List<MusicDB> {
        return dao.getMusicList()
    }
}

