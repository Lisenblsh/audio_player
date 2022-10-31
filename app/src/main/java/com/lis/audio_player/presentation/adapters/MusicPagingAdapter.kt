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
import com.lis.audio_player.data.room.MusicDB
import com.lis.audio_player.domain.tools.Coil

class MusicPagingAdapter : PagingDataAdapter<MusicDB, RecyclerView.ViewHolder>(MUSIC_COMPARISON) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val music = getItem(position)
        (holder as MusicViewHolder).bind(music)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_list_item, parent, false)
        return MusicViewHolder(view)
    }

    interface OnClickListener{
        fun onMenuClick(id: Long)
        fun onItemClick(id: Long)
    }

    private lateinit var clickListener: OnClickListener

    fun setOnClickListener(listener: OnClickListener){
        this.clickListener = listener
    }

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val image = itemView.findViewById<ImageView>(R.id.music_image)
        private val title = itemView.findViewById<TextView>(R.id.title_music)
        private val artistName = itemView.findViewById<TextView>(R.id.artist_name)
        private val menu = itemView.findViewById<ImageView>(R.id.music_menu)

        private var music: MusicDB? = null

        init {

        }

        fun bind(music: MusicDB?) {
            if(music == null){
                //TODO("Сюда можно вставить плэйс холдеры при загрузке")
            } else {
                showRepoData(music)
            }
        }

        private fun showRepoData(music: MusicDB) {
            this.music = music

            title.text = music.title
            artistName.text = music.artist
            Coil().setImage(music.photo300.ifBlank { R.drawable.base_song_image },image)
            itemView.setOnClickListener {
                val id = music.musicId
                clickListener.onItemClick(id)
                clickListener.onMenuClick(id)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.music_list_item
    }

    companion object {
        private val MUSIC_COMPARISON = object : DiffUtil.ItemCallback<MusicDB>() {
            override fun areItemsTheSame(oldItem: MusicDB, newItem: MusicDB): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MusicDB, newItem: MusicDB): Boolean {
                return oldItem == newItem
            }

        }
    }
}