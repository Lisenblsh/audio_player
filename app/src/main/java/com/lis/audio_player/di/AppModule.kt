package com.lis.audio_player.di

import com.lis.audio_player.presentation.viewModels.AlbumListViewModel
import com.lis.audio_player.presentation.viewModels.MusicListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<MusicListViewModel> { (ownerId: Long?,albumId: Long?,accessKey: String?)->
        MusicListViewModel(
            repository = get(),
            database = get(),
            ownerId = ownerId,
            albumId = albumId,
            accessKey = accessKey,
        )
    }
    viewModel<AlbumListViewModel> {
        AlbumListViewModel(repository = get(), database = get())
    }
}