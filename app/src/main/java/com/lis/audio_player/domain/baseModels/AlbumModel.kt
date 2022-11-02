package com.lis.audio_player.domain.baseModels

data class AlbumModel(
    val albumId: Long,
    val ownerId: Long,
    val accessKey: String,
    val title: String,
    val year: Int,
    val musicCount: Int,
    val isFollowing: Boolean,
    val isExplicit: Boolean,
    val photo300: String,
    val photo600: String,
    val mainArtist: List<ArtistModel>?
)

