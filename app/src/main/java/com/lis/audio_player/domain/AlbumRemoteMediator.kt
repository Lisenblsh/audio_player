package com.lis.audio_player.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.lis.audio_player.data.network.Filters
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.data.room.AlbumDB
import com.lis.audio_player.data.room.MusicDatabase
import com.lis.audio_player.data.room.RemoteAlbumKeys
import com.lis.audio_player.domain.tools.convertToAlbumDB
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class AlbumRemoteMediator(
    private val repository: MusicRepositoryImpl,
    private val database: MusicDatabase
) : RemoteMediator<Int, AlbumDB>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AlbumDB>
    ): MediatorResult {
        val pageSize = state.config.pageSize
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteAlbumKeys = getRemoteKeysClosestToCurrentPosition(state)
                remoteAlbumKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteAlbumKeys = getRemoteKeysForLastItem(state)
                val nextKey = remoteAlbumKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteAlbumKeys != null)
                nextKey
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val (albumList, endOfPaginationReached) = getAlbumList(277745690,pageSize, pageSize + (page - 1))
            database.withTransaction {
                if(loadType == LoadType.REFRESH){
                    database.musicDao().deleteAlbum()
                    database.musicDao().clearRemoteAlbumKeysTable()
                }

                val prevKey = if(page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if(endOfPaginationReached) null else page + 1

                if(albumList != null){
                    database.musicDao().insertAlbumList(albumList)

                    val keys =
                        database.musicDao().getAlbumList().takeLast(pageSize).map {
                            RemoteAlbumKeys(
                                it.albumId,
                                prevKey,
                                nextKey
                            )
                        }
                    database.musicDao().insertAllAlbumKeys(keys)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException){
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }

    }

    private suspend fun getAlbumList(
        ownerId: Long,
        count: Int,
        offset: Int,
        extended: Int? = null,
        fields: String? = null,
        filters: Filters? = Filters.all
    ): Pair<List<AlbumDB>?, Boolean> {
        val albumList = repository.getAlbumList(ownerId, count, offset, extended, fields, filters)
            .body()?.response?.items?.convertToAlbumDB()
        val endOfPaginationReached = albumList.isNullOrEmpty()
        return Pair(albumList,endOfPaginationReached)
    }

    private suspend fun getRemoteKeysClosestToCurrentPosition(state: PagingState<Int, AlbumDB>): RemoteAlbumKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { albumId ->
                database.musicDao().getRemoteAlbumKey(albumId)
            }
        }

    }

    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, AlbumDB>): RemoteAlbumKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { repo ->
            database.musicDao().getRemoteAlbumKey(repo.albumId)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}