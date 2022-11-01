package com.lis.audio_player.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lis.audio_player.R
import com.lis.audio_player.data.room.AlbumDB

class AlbumPagingAdapter: PagingDataAdapter<AlbumDB, RecyclerView.ViewHolder>(ALBUM_COMPARISON) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val album = getItem(position)
        (holder as AlbumViewHolder).bind(album)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.album_list_item, parent, false)
        return AlbumViewHolder(view)
    }

    interface OnClickListener{
        fun onItemClick(id: Long)
    }

    private lateinit var clickListener: OnClickListener

    fun setOnClickListener(listener: OnClickListener) {
        this.clickListener = listener
    }

    inner class AlbumViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val image = itemView.findViewById<ImageView>(R.id.album_image)
        private val title = itemView.findViewById<TextView>(R.id.album_title)
        private val albumAuthor = itemView.findViewById<TextView>(R.id.album_author)

        fun bind(album: AlbumDB?) {

        }

    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.album_list_item
    }

    companion object {
        private val ALBUM_COMPARISON = object: DiffUtil.ItemCallback<AlbumDB>() {
            override fun areItemsTheSame(oldItem: AlbumDB, newItem: AlbumDB): Boolean {
                return oldItem.albumId == newItem.albumId
            }

            override fun areContentsTheSame(oldItem: AlbumDB, newItem: AlbumDB): Boolean {
                return oldItem == newItem
            }

        }
    }
}