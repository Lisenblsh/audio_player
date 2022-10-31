package com.lis.audio_player.domain.tools

import com.lis.audio_player.domain.models.Item
import com.lis.audio_player.data.room.AlbumForMusic
import com.lis.audio_player.data.room.GenreType
import com.lis.audio_player.data.room.MusicDB

fun List<Item>.convertToMusicDB(): List<MusicDB> {
    return this.map {
        MusicDB(
            0,
            it.id,
            it.ownerId,
            "${it.id}_${it.ownerId}",
            it.artist,
            it.title,
            it.url,
            it.album?.thumb?.photo600 ?: "",
            it.album?.thumb?.photo1200 ?: "",
            it.duration,
            it.isExplicit,
            AlbumForMusic(it.id, it.ownerId, it.accessKey),
            it.lyricsId ?: 0,
            GenreType.getGenreById(it.genreId?.toInt() ?: 0)
        )
    }
}