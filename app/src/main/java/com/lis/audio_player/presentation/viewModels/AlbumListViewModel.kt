package com.lis.audio_player.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.data.room.AlbumDB
import com.lis.audio_player.data.room.MusicDatabase
import com.lis.audio_player.domain.AlbumRemoteMediator
import kotlinx.coroutines.flow.Flow

class AlbumListViewModel(
    private val repository: MusicRepositoryImpl,
    private val database: MusicDatabase
): ViewModel() {
    val pagingAlbumList: Flow<PagingData<AlbumDB>>
    init {
        pagingAlbumList = getAlbumList()
    }

    private fun getAlbumList(): Flow<PagingData<AlbumDB>> {
        val pagingSourceFactory = {database.musicDao().getPagingAlbumList()}

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = INITIAL_SIZE
            ),
            remoteMediator = AlbumRemoteMediator(repository,database),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val PAGE_SIZE = 5
        const val INITIAL_SIZE = 2
    }
}