package com.lis.audio_player.data.room

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface MusicDao {
    //AlbumBD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbum(albumDB: AlbumDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = AlbumDB::class)
    suspend fun insertAlbumList(albums: List<AlbumDB>)

    @Query("SELECT * FROM AlbumDB")
    @Transaction
    suspend fun getAlbumList(): List<AlbumDB>

    @Query("SELECT * FROM AlbumDB")
    @Transaction
    fun getPagingAlbumList(): PagingSource<Int, AlbumDB>

    @Transaction
    @Query("SELECT * FROM AlbumDB WHERE id = :id")
    fun getAlbumById(id: Long): AlbumDB?

    @Transaction
    @Query("DELETE FROM AlbumDB WHERE id = :id")
    fun deleteAlbumById(id: Long)

    @Transaction
    @Query("DELETE FROM AlbumDB")
    fun deleteAlbum()

    //MusicDB
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusic(musicDB: MusicDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = MusicDB::class)
    suspend fun insertMusicList(musics: List<MusicDB>)

    @Query("SELECT * FROM MusicDB")
    @Transaction
    suspend fun getMusicList(): List<MusicDB>

    @Query("SELECT * FROM MusicDB")
    @Transaction
    fun getPagingMusicList(): PagingSource<Int, MusicDB>

    @Transaction
    @Query("SELECT * FROM MusicDB WHERE musicId = :id")
    fun getMusicById(id: Long): MusicDB

    @Transaction
    @Query("DELETE FROM MusicDB WHERE musicId = :id")
    fun deleteMusicById(id: Long)

    @Transaction
    @Query("DELETE FROM MusicDB")
    fun deleteMusic()

    //ArtistDB
    @Insert
    fun insertArtist(artists: List<ArtistDB>)

    @Query("SELECT * FROM ArtistDB")
    @Transaction
    suspend fun getArtist(): ArtistDB?

    @Transaction
    @Query("SELECT * FROM ArtistDB WHERE id = :id")
    fun getArtistById(id: String): ArtistDB?

    @Transaction
    @Query("DELETE FROM ArtistDB WHERE id = :id")
    fun deleteArtistById(id: String)

    //RemoteMusicKeys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMusicKeys(remoteMusicKeys: List<RemoteMusicKeys>)

    @Transaction
    @Query("SELECT * FROM RemoteMusicKeys WHERE musicId = :musicId")
    suspend fun getRemoteMusicKey(musicId: Long): RemoteMusicKeys?

    @Transaction
    @Query("DELETE FROM RemoteMusicKeys")
    fun clearRemoteMusicKeysTable()

    //RemoteAlbumKeys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAlbumKeys(remoteAlbumKeys: List<RemoteAlbumKeys>)

    @Transaction
    @Query("SELECT * FROM RemoteAlbumKeys WHERE albumId = :albumId")
    suspend fun getRemoteAlbumKey(albumId: Long): RemoteAlbumKeys?

    @Transaction
    @Query("DELETE FROM RemoteAlbumKeys")
    fun clearRemoteAlbumKeysTable()

}