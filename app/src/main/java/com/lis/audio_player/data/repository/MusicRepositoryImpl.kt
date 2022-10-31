package com.lis.audio_player.data.repository

import com.lis.audio_player.data.network.retrofit.RetrofitService
import com.lis.audio_player.data.room.MusicDB
import com.lis.audio_player.data.room.MusicDao
import com.lis.audio_player.data.room.MusicDatabase
import com.lis.audio_player.domain.MusicRepository

class MusicRepositoryImpl(
    private val service: RetrofitService,
    private val token: String,
    private val dao: MusicDao
) : MusicRepository {
    override suspend fun getMusicList(
        count: Int,
        offset: Int,
        ownerId: Long?,
        albumId: Long?,
        accessKey: String?
    ) = service.getAudio(token, count, offset, ownerId, albumId, accessKey)

    override suspend fun getMusicListFromDB(): List<MusicDB> {
        return dao.getMusicList()
    }
}