package com.lis.audio_player.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.data.room.MusicDatabase
import com.lis.audio_player.domain.AlbumPagingSource
import com.lis.audio_player.domain.baseModels.AlbumModel
import kotlinx.coroutines.flow.Flow

class AlbumListViewModel(
    private val repository: MusicRepositoryImpl,
    private val database: MusicDatabase
): ViewModel() {
    val pagingAlbumList: Flow<PagingData<AlbumModel>>
    init {
        pagingAlbumList = getAlbumList()
    }

    private fun getAlbumList(): Flow<PagingData<AlbumModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = INITIAL_SIZE
            ),
        ){
            AlbumPagingSource(repository)
        }.flow
    }

    companion object {
        const val PAGE_SIZE = 10
        const val INITIAL_SIZE = PAGE_SIZE
    }
}