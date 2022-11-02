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
    private val database: MusicDatabase
) : ViewModel() {
    val pagingMusicList: Flow<PagingData<AudioModel>>

    init {
        pagingMusicList = getMusicList()
    }

    private fun getMusicList(): Flow<PagingData<AudioModel>> {
//        val pagingSourceFactory = { database.musicDao().getPagingMusicList() }
//
//        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = INITIAL_SIZE
            )
        ){
            MusicPagingSource(repository)
        }.flow
    }

    companion object {
        const val PAGE_SIZE = 20
        const val INITIAL_SIZE = 2
    }

}