package com.lis.audio_player.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.lis.audio_player.databinding.FragmentAlbumBinding
import com.lis.audio_player.databinding.FragmentAudioBinding
import com.lis.audio_player.domain.adapters.AlbumPagingAdapter
import com.lis.audio_player.presentation.viewModels.AlbumListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class AlbumFragment() : Fragment() {

    private lateinit var binding: FragmentAlbumBinding

    private val albumViewModel by viewModel<AlbumListViewModel>()

    private val albumAdapter = AlbumPagingAdapter()

    private var albumFragmentType = FULL

    constructor(@AlbumFragmentType albumFragmentType: Int) : this() {
        this.albumFragmentType = albumFragmentType
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentAlbumBinding.inflate(inflater, container, false)
            binding.viewAlbumList()
            binding.initSwipeRefresh()

        }
        return binding.root
    }

    private fun FragmentAlbumBinding.initSwipeRefresh() {
        lifecycleScope.launch {
            albumAdapter.loadStateFlow.collectLatest { loadState ->
                if (loadState.refresh is LoadState.NotLoading) {
                    val position = when (albumFragmentType) {
                        FULL -> {
                            (albumList.layoutManager as FlexboxLayoutManager).findFirstVisibleItemPosition()
                        }
                        SMALL -> {
                            (albumList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                        }
                        else -> {
                            throw IOException("wrong type")
                        }
                    }
                    if (position == 0) {
                        albumList.scrollToPosition(0)
                    }
                    refreshLayout.isRefreshing = false

                }
            }
        }
        refreshLayout.setOnRefreshListener {
            albumAdapter.refresh()
        }
    }

    private fun FragmentAlbumBinding.viewAlbumList() {
        albumAdapter.setOnClickListener(object : AlbumPagingAdapter.OnClickListener {
            override fun onItemClick(albumId: Long, accessKey: String) {
                val directions = when (albumFragmentType) {
                    FULL -> {
                        AlbumFragmentDirections.actionAlbumFragmentToAudioFragment().apply {
                            this.albumId = albumId
                            this.accessKey = accessKey
                        }
                    }
                    SMALL -> {
                        MainFragmentDirections.actionMainFragmentToAudioFragment().apply {
                            this.albumId = albumId
                            this.accessKey = accessKey
                        }
                    }
                    else -> {
                        throw IOException("wrong type")
                    }
                }
                NavHostFragment.findNavController(this@AlbumFragment).navigate(directions)
            }
        })

        albumList.adapter = albumAdapter
        val layoutManager = if (albumFragmentType == FULL) {
            FlexboxLayoutManager(requireContext(), FlexDirection.ROW).apply {
                justifyContent = JustifyContent.CENTER
            }
        } else {
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }
        albumList.layoutManager = layoutManager

        lifecycleScope.launch {
            albumViewModel.pagingAlbumList.collectLatest(albumAdapter::submitData)
        }
    }
}

annotation class AlbumFragmentType

var FULL = 0
var SMALL = 1
