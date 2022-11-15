package com.lis.audio_player.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.data.room.MusicDatabase
import com.lis.audio_player.domain.MusicPagingSource
import com.lis.audio_player.domain.baseModels.AudioModel
import kotlinx.coroutines.flow.Flow

class MusicListViewModel(
    private val repository: MusicRepositoryImpl,
    private val database: MusicDatabase,
    private val ownerId: Long? = null,
    private val albumId: Long? = null,
    private val accessKey: String? = null
) : ViewModel() {
    val pagingMusicList: Flow<PagingData<AudioModel>>

    init {
        pagingMusicList = getMusicList()
    }

    private fun getMusicList(): Flow<PagingData<AudioModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = PAGE_SIZE
            )
        ) {
            MusicPagingSource(
                repository = repository,
                ownerId = ownerId,
                albumId = albumId,
                accessKey = accessKey
            )
        }.flow
    }

    companion object {
        const val PAGE_SIZE = 20
    }

}