package com.lis.audio_player.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lis.audio_player.databinding.FragmentAudioBinding
import com.lis.audio_player.domain.adapters.MusicPagingAdapter
import com.lis.audio_player.presentation.PlayerActivity
import com.lis.audio_player.presentation.viewModels.MusicListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioFragment() : Fragment() {
    private lateinit var binding: FragmentAudioBinding

    private val musicAdapter = MusicPagingAdapter()

    constructor(albumId: Long, accessKey: String) : this() {
        this.albumId = albumId
        this.accessKey = accessKey
    }

    private var ownerId: Long? = null
    var albumId: Long? = null
    var accessKey: String? = null

    private val musicViewModel by viewModel<MusicListViewModel> {
        parametersOf(ownerId,albumId,accessKey)
    }

    override fun onStart() {
        try {
            val args = AudioFragmentArgs.fromBundle(requireArguments())
            ownerId = if(args.ownerId != 0L) args.ownerId else null
            albumId = if(args.albumId != 0L) args.albumId else null
            accessKey = if(args.accessKey != "") args.accessKey else null
        } catch (_: Exception) {}
        super.onStart()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioBinding.inflate(inflater, container, false)

        binding.viewMusicList()

        return binding.root
    }

    private fun FragmentAudioBinding.viewMusicList() {
        musicAdapter.setOnClickListener(object : MusicPagingAdapter.OnClickListener {
            override fun onMenuClick(id: Long) {
                //TODO("Not yet implemented")
            }

            override fun onItemClick(id: Long) {
                val intent = Intent(requireContext(), PlayerActivity::class.java)
                    .putExtra("music_id", id)
                startActivity(intent)
            }
        })

        musicList.adapter = musicAdapter
        musicList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        lifecycleScope.launch {
            musicViewModel.pagingMusicList.collectLatest(musicAdapter::submitData)
        }
    }
}