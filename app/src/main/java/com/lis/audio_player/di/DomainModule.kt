package com.lis.audio_player.di

import com.lis.audio_player.domain.playerUseCases.GetMusicInfoFromId
import com.lis.audio_player.domain.MusicRemoteMediator
import com.lis.audio_player.domain.playerUseCases.PlayMusicFromUrl
import org.koin.dsl.module

val domainModule = module {
    factory<MusicRemoteMediator> {
        MusicRemoteMediator(database = get(), repository = get())
    }

    factory<GetMusicInfoFromId> {
        GetMusicInfoFromId(database = get())
    }

    factory <PlayMusicFromUrl> {
        params -> PlayMusicFromUrl(context = get(), repository = get(), exoPlayer = params.get())
    }
}