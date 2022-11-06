package com.lis.audio_player.di

import com.lis.audio_player.domain.playerUseCases.PlayMusicFromUrl
import org.koin.dsl.module

val domainModule = module {


    factory <PlayMusicFromUrl> {
        params -> PlayMusicFromUrl(context = get(), repository = get(), exoPlayer = params.get())
    }
}