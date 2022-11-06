package com.lis.audio_player.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.lis.audio_player.databinding.FragmentAlbumBinding
import com.lis.audio_player.domain.adapters.AlbumPagingAdapter
import com.lis.audio_player.presentation.viewModels.AlbumListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumFragment : Fragment() {

    private lateinit var binding: FragmentAlbumBinding

    private val albumViewModel by viewModel<AlbumListViewModel>()

    private val albumAdapter = AlbumPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        binding.viewAlbumList()
        return binding.root
    }

    private fun FragmentAlbumBinding.viewAlbumList() {
        albumAdapter.setOnClickListener(object : AlbumPagingAdapter.OnClickListener {
            override fun onItemClick(id: Long) {
                Toast.makeText(requireContext(), id.toString(), Toast.LENGTH_SHORT).show()
            }

        })

        albumList.adapter = albumAdapter
        albumList.layoutManager =
            FlexboxLayoutManager(requireContext(), FlexDirection.ROW).apply {
                justifyContent = JustifyContent.CENTER }

        lifecycleScope.launch {
            albumViewModel.pagingAlbumList.collectLatest(albumAdapter::submitData)
        }
    }
}
