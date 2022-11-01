package com.lis.audio_player.domain

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.data.room.*
import com.lis.audio_player.domain.tools.convertToMusicDB
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class MusicRemoteMediator(
    private val repository: MusicRepositoryImpl,
    private val database: MusicDatabase
) : RemoteMediator<Int, MusicDB>() {


    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MusicDB>
    ): MediatorResult {
        val pageSize = state.config.pageSize
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeysClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeysForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val (musicList, endOfPaginationReached) = getMusicList(pageSize, pageSize * (page-1))
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.musicDao().deleteMusic()
                    database.musicDao().clearRemoteMusicKeysTable()
                }

                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                if (musicList != null) {

                    database.musicDao().insertMusicList(musicList)

                    val keys =
                        database.musicDao().getMusicList().takeLast(pageSize).map {
                            Log.e("remoteKey Ids", "ids: ${it.id}")
                            RemoteMusicKeys(
                                it.musicId,
                                prevKey,
                                nextKey
                            )
                        }
                    database.musicDao().insertAllMusicKeys(keys)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getMusicList(
        count: Int,
        offset: Int,
        ownerId: Long? = null,
        albumId: Long? = null,
        accessKey: String? = null
    ): Pair<List<MusicDB>?, Boolean> {
        val musicList =
            repository.getMusicList(count, offset, ownerId, albumId, accessKey)
                .body()?.response?.items?.convertToMusicDB()
        val endOfPaginationReached = musicList.isNullOrEmpty()
        Log.e("remote take list", "")
        return Pair(musicList, endOfPaginationReached)
    }

    private suspend fun getRemoteKeysClosestToCurrentPosition(state: PagingState<Int, MusicDB>): RemoteMusicKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { musicId ->
                database.musicDao().getRemoteMusicKey(musicId)
            }
        }
    }

    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, MusicDB>): RemoteMusicKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { repo ->
            database.musicDao().getRemoteMusicKey(repo.musicId)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

}