package com.lis.audio_player.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.domain.baseModels.AudioModel
import com.lis.audio_player.domain.networkModels.VkMusic
import com.lis.audio_player.domain.tools.convertToMusicModel
import retrofit2.HttpException
import retrofit2.Response

class MusicPagingSource(private val repository: MusicRepositoryImpl) :
    PagingSource<Int, AudioModel>() {
    override fun getRefreshKey(state: PagingState<Int, AudioModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AudioModel> {
        val page = params.key ?: 1
        val pageSize = params.loadSize.coerceAtMost(10)

        val response = getMusicList(
            count = pageSize,
            offset = pageSize * (page - 1)
        )

        return if (response.isSuccessful) {
            val musicList =
                checkNotNull(response.body()).musicResponse.musicItems.convertToMusicModel()
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (musicList.size < pageSize) null else page + 1
            LoadResult.Page(musicList, prevKey, nextKey)
        } else {
            LoadResult.Error(HttpException(response))
        }

    }

    private suspend fun getMusicList(
        count: Int,
        offset: Int,
        ownerId: Long? = null,
        albumId: Long? = null,
        accessKey: String? = null
    ): Response<VkMusic> {
        return repository.getMusicList(count, offset, ownerId, albumId, accessKey)
    }
}