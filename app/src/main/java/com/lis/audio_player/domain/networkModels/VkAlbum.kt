package com.lis.audio_player.domain.networkModels

import com.google.gson.annotations.SerializedName

data class VkAlbum(
    @SerializedName("response")
    val response: AlbumResponse
)

data class AlbumResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("items")
    val items: List<AlbumItem>,
)

data class AlbumItem(
    @SerializedName("access_key")
    val accessKey: String,
    @SerializedName("album_type")
    val albumType: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("create_time")
    val createTime: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("followed")
    val followed: Followed?,
    @SerializedName("followers")
    val followers: Int,
    @SerializedName("genres")
    val genres: List<Genre>,
    @SerializedName("id")
    val id: Long,
    @SerializedName("is_explicit")
    val isExplicit: Boolean,
    @SerializedName("is_following")
    val isFollowing: Boolean,
    @SerializedName("main_artists")
    val mainArtists: List<MainArtist>?,
    @SerializedName("original")
    val original: Original?,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("photo")
    val photo: Thumb?,
    @SerializedName("plays")
    val plays: Int,
    @SerializedName("thumbs")
    val thumbs: List<Thumb>?,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("update_time")
    val updateTime: Int,
    @SerializedName("year")
    val year: Int
)

data class Original(
    @SerializedName("access_key")
    val accessKey: String,
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("playlist_id")
    val playlistId: Int
)

data class Followed(
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("playlist_id")
    val playlistId: Int
)

data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class MainArtist(
    @SerializedName("name")
    val name: String,
    @SerializedName("domain")
    val domain: String?,
    @SerializedName("id")
    val id: String?
)