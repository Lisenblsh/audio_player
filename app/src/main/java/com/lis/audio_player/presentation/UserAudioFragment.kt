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
        if(!this::binding.isInitialized){
            binding = FragmentUserAudioBinding.inflate(layoutInflater, container, false)
            viewMusicFragment()
            viewAlbumFragment()
            binding.bindButtons()
        }
        return binding.root
    }

    private fun viewMusicFragment() {
        val audioFragment: Fragment = AudioFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.music_fragment,audioFragment).commit()


    }

    private fun viewAlbumFragment() {
        val albumFragment: Fragment = AlbumFragment(SMALL)
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.album_fragment,albumFragment).commit()
    }

    private fun FragmentUserAudioBinding.bindButtons() {
        showAllAlbum.setOnClickListener {
            val directions = MainFragmentDirections.actionMainFragmentToAlbumFragment()
            NavHostFragment.findNavController(this@UserAudioFragment).navigate(directions)
        }
    }
}
