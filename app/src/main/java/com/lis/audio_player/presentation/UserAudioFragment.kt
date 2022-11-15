package com.lis.audio_player.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.lis.audio_player.R
import com.lis.audio_player.databinding.FragmentUserAudioBinding
import com.lis.audio_player.domain.adapters.AlbumPagingAdapter
import com.lis.audio_player.domain.adapters.MusicPagingAdapter
import com.lis.audio_player.presentation.viewModels.AlbumListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class UserAudioFragment : Fragment() {

    private lateinit var binding: FragmentUserAudioBinding

    private val albumViewModel by viewModel<AlbumListViewModel>()

    private val musicAdapter = MusicPagingAdapter()
    private val albumAdapter = AlbumPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserAudioBinding.inflate(layoutInflater, container, false)
        //binding.viewMusicList()
        binding.viewMusicFragment()
        binding.viewAlbumFragment()
        //binding.viewAlbumList()
        binding.bindButtons()
        return binding.root
    }

    private fun FragmentUserAudioBinding.viewMusicFragment() {
        val audioFragment: Fragment = AudioFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.music_fragment,audioFragment).commit()


    }

    private fun FragmentUserAudioBinding.viewAlbumFragment() {
        val albumFragment: Fragment = AlbumFragment(SMALL)
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.album_fragment,albumFragment).commit()
    }

    private fun FragmentUserAudioBinding.viewAlbumList() {
        albumAdapter.setOnClickListener(object : AlbumPagingAdapter.OnClickListener {
            override fun onItemClick(albumId: Long, accessKey: String) {
                Toast.makeText(requireContext(), albumId.toString(), Toast.LENGTH_SHORT).show()
            }

        })

//        albumList.adapter = albumAdapter
//        albumList.layoutManager =
//            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

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
