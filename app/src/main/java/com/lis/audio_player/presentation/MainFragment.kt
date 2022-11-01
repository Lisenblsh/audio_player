package com.lis.audio_player.presentation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lis.audio_player.databinding.FragmentMainBinding
import com.lis.audio_player.domain.adapters.AlbumPagingAdapter
import com.lis.audio_player.domain.adapters.MusicPagingAdapter
import com.lis.audio_player.presentation.viewModels.AlbumListViewModel
import com.lis.audio_player.presentation.viewModels.MusicListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

const val TAG = "MainFragment"


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val musicViewModel by viewModel<MusicListViewModel>()
    private val albumViewModel by viewModel<AlbumListViewModel>()

    private val musicAdapter = MusicPagingAdapter()
    private val albumAdapter = AlbumPagingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        binding.viewMusicList()
        binding.viewAlbumList()
        return binding.root
    }

    private fun FragmentMainBinding.viewMusicList() {
        musicAdapter.setOnClickListener(object : MusicPagingAdapter.OnClickListener{
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
        musicList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        lifecycleScope.launch {
            musicViewModel.pagingMusicList.collectLatest(musicAdapter::submitData)
        }
    }

    private fun FragmentMainBinding.viewAlbumList(){
        albumAdapter.setOnClickListener(object : AlbumPagingAdapter.OnClickListener {
            override fun onItemClick(id: Long) {
                Toast.makeText(requireContext(), id.toString(), Toast.LENGTH_SHORT).show()
            }

        })

        albumList.adapter = albumAdapter
        albumList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        lifecycleScope.launch {
            albumViewModel.pagingAlbumList.collectLatest(albumAdapter::submitData)
        }
    }
}
