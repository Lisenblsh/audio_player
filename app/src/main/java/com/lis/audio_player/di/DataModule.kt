package com.lis.audio_player.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.lis.audio_player.R
import com.lis.audio_player.data.network.retrofit.RetrofitService
import com.lis.audio_player.data.repository.MusicRepositoryImpl
import com.lis.audio_player.data.room.MusicDao
import com.lis.audio_player.data.room.MusicDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule = module {

    single<MusicDatabase> {
        MusicDatabase.getInstance(context = get())
    }

    single<MusicDao> {
        val database = get<MusicDatabase>()
        database.musicDao()
    }

    single<MusicRepositoryImpl> {
        MusicRepositoryImpl(service = get(), token = getToken(context = get()), dao = get())
    }

    single<RetrofitService> {
        RetrofitService.create(userAgent = getUserAgent(context = get()))
    }
}

private fun getPreference(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        context.resources.getString(
            R.string.authorization_info
        ),
        Context.MODE_PRIVATE
    )
}

private fun getToken(context: Context): String {
    return getPreference(context)
        .getString(context.resources.getString(R.string.token_key), "") ?: ""
}

private fun getUserAgent(context: Context): String {
    return getPreference(context)
        .getString(context.resources.getString(R.string.user_agent), "") ?: ""
}