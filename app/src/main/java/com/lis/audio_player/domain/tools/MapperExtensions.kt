package com.lis.audio_player.domain.tools

import com.lis.audio_player.domain.baseModels.*
import com.lis.audio_player.domain.networkModels.AlbumItem
import com.lis.audio_player.domain.networkModels.MusicItem

fun List<MusicItem>.convertToMusicModel(): List<AudioModel> {
    return this.map { musicItem ->
        AudioModel(
            musicId = musicItem.id,
            ownerId = musicItem.ownerId,
            contentIs = "${musicItem.id}_${musicItem.ownerId}",
            artist = musicItem.artist,
            title = musicItem.title,
            url = musicItem.url,
            photo300 = musicItem.album?.thumb?.photo600 ?: "",
            photo1200 = musicItem.album?.thumb?.photo1200 ?: "",
            duration = musicItem.duration,
            isExplicit = musicItem.isExplicit,
            album = AlbumForMusic(musicItem.id, musicItem.ownerId, musicItem.accessKey),
            lyricsId = musicItem.lyricsId ?: 0,
            genreId = GenreType.getGenreById(musicItem.genreId?.toInt() ?: 0),
            mainArtist = musicItem.mainArtists?.map {
                ArtistModel(
                    name = it.name,
                    domain = it.domain,
                    id = it.id
                )
            },
            featuredArtist = musicItem.featuredArtists?.map{
                ArtistModel(
                    name = it.name,
                    domain = it.domain,
                    id = it.id
                )
            }
        )
    }
}

fun List<AlbumItem>.convertToAlbumModel(): List<AlbumModel> {
    return this.map { albumItem ->
        AlbumModel(
            albumId = albumItem.id,
            ownerId = albumItem.ownerId,
            accessKey = albumItem.accessKey,
            title = albumItem.title,
            year = albumItem.year,
            musicCount = albumItem.count,
            isFollowing = albumItem.isFollowing,
            isExplicit = albumItem.isExplicit,
            photo300 = albumItem.thumbs?.get(0)?.photo300 ?: (albumItem.photo?.photo300 ?: ""),
            photo600 = albumItem.thumbs?.get(0)?.photo600 ?: (albumItem.photo?.photo300 ?: ""),
            mainArtist = albumItem.mainArtists?.map {
                ArtistModel(
                    name = it.name,
                    domain = it.domain,
                    id = it.id
                )
            }
        )
    }
}