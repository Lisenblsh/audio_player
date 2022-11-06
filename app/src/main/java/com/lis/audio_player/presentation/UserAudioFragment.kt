package com.lis.audio_player.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lis.audio_player.databinding.FragmentUserAudioBinding
import com.lis.audio_player.domain.adapters.AlbumPagingAdapter
import com.lis.audio_player.domain.adapters.MusicPagingAdapter
import com.lis.audio_player.presentation.viewModels.AlbumListViewModel
import com.lis.audio_player.presentation.viewModels.MusicListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class UserAudioFragment : Fragment() {

    private lateinit var binding: FragmentUserAudioBinding

    private val musicViewModel by viewModel<MusicListViewModel>()
    private val albumViewModel by viewModel<AlbumListViewModel>()

    private val musicAdapter = MusicPagingAdapter()
    private val albumAdapter = AlbumPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserAudioBinding.inflate(layoutInflater, container, false)
        binding.viewMusicList()
        binding.viewAlbumList()
        binding.bindButtons()
        return binding.root
    }

    private fun FragmentUserAudioBinding.viewMusicList() {
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

    private fun FragmentUserAudioBinding.viewAlbumList() {
        albumAdapter.setOnClickListener(object : AlbumPagingAdapter.OnClickListener {
            override fun onItemClick(id: Long) {
                Toast.makeText(requireContext(), id.toString(), Toast.LENGTH_SHORT).show()
            }

        })

        albumList.adapter = albumAdapter
        albumList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        lifecycleScope.launch {
            albumViewModel.pagingAlbumList.collectLatest(albumAdapter::submitData)
        }
    }

    private fun FragmentUserAudioBinding.bindButtons() {
        showAllAlbum.setOnClickListener {
            val directions = MainFragmentDirections.actionMainFragmentToAlbumFragment()
            NavHostFragment.findNavController(this@UserAudioFragment).navigate(directions)
        }
    }
}
