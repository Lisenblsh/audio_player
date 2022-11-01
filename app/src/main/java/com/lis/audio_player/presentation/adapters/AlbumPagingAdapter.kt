package com.lis.audio_player.presentation.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lis.audio_player.data.room.AlbumDB

class AlbumPagingAdapter: PagingDataAdapter<AlbumDB, RecyclerView.ViewHolder>(ALBUM_COMPARISON) {

    companion object {
        private val ALBUM_COMPARISON = object: DiffUtil.ItemCallback<AlbumDB>() {
            override fun areItemsTheSame(oldItem: AlbumDB, newItem: AlbumDB): Boolean {
                return oldItem.albumId == newItem.albumId
            }

            override fun areContentsTheSame(oldItem: AlbumDB, newItem: AlbumDB): Boolean {
                TODO("Not yet implemented")
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }
}