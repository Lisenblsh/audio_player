package com.lis.audio_player.domain.networkModels

data class VkFriend(
    val musicResponse: MusicResponse
)

data class FriendResponse(
    val count: Int,
    val musicItems: List<MusicItem>
)

data class FriendItem(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val photo_100: String,
)