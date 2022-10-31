package com.lis.audio_player.di

import com.lis.audio_player.presentation.viewModels.MusicListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<MusicListViewModel>{
        MusicListViewModel(repository = get(), database = get())
    }
}