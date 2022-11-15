package com.lis.audio_player.domain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lis.audio_player.R
import com.lis.audio_player.domain.baseModels.AudioModel
import com.lis.audio_player.domain.tools.ImageLoader

class MusicAdapter: ListAdapter<AudioModel, MusicAdapter.MusicViewHolder>(
    AsyncDifferConfig.Builder(MUSIC_COMPARISON).build()
) {


    interface OnClickListener{
        fun onMenuClick(id: Long)
        fun onItemClick(id: Long)
    }

    private lateinit var clickListener: OnClickListener

    fun setOnClickListener(listener: OnClickListener){
        this.clickListener = listener
    }

    inner class MusicViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.music_image)
        private val title = itemView.findViewById<TextView>(R.id.title_music)
        private val artistName = itemView.findViewById<TextView>(R.id.artist_name)
        private val menu = itemView.findViewById<ImageView>(R.id.music_menu)

        private var music: AudioModel? = null

        fun bind(music: AudioModel?) {
            if(music == null){
                //TODO("Сюда можно вставить плэйс холдеры при загрузке")
            } else {
                showRepoData(music)
            }
        }

        private fun showRepoData(music: AudioModel) {
            this.music = music

            title.text = music.title
            artistName.text = music.artist
            ImageLoader().setImage(music.photo300.ifBlank { R.drawable.base_song_image },image)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_list_item, parent, false)
        return MusicViewHolder(view)
    }


    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = getItem(position)
        holder.bind(music)
    }

    companion object {
        private val MUSIC_COMPARISON = object : DiffUtil.ItemCallback<AudioModel>() {
            override fun areItemsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
                return oldItem.musicId == newItem.musicId
            }

            override fun areContentsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}