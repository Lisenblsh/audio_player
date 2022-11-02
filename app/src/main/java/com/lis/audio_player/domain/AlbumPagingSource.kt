package com.lis.audio_player.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lis.audio_player.data.network.Filters
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.domain.baseModels.AlbumModel
import com.lis.audio_player.domain.networkModels.VkAlbum
import com.lis.audio_player.domain.tools.convertToAlbumModel
import retrofit2.HttpException
import retrofit2.Response

class AlbumPagingSource(private val repository: MusicRepositoryImpl) :
    PagingSource<Int, AlbumModel>() {
    override fun getRefreshKey(state: PagingState<Int, AlbumModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AlbumModel> {
        val page = params.key ?: 1
        val pageSize = params.loadSize.coerceAtMost(10)

        val response = getAlbumList(
            count = pageSize,
            offset = pageSize * (page - 1),
        )

        return if (response.isSuccessful) {
            val albumList = checkNotNull(response.body()).response.items.convertToAlbumModel()
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (albumList.size < pageSize) null else page + 1
            LoadResult.Page(albumList, prevKey, nextKey)
        }else {
            LoadResult.Error(HttpException(response))
        }


    }

    private suspend fun getAlbumList(
        count: Int,
        offset: Int,
        extended: Int? = null,
        fields: String? = null,
        ownerId: Long? = null,
        filters: Filters? = Filters.all
    ): Response<VkAlbum> {
        return repository.getAlbumList(ownerId, count, offset, extended, fields, filters)
    }
}