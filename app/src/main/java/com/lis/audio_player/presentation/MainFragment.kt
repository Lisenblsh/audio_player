package com.lis.audio_player.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.lis.audio_player.R
import com.lis.audio_player.databinding.FragmentMainBinding
import com.lis.audio_player.domain.adapters.ViewPagerAdapter

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater,container, false)
        showAuthorizationActivity()
        binding.setTabs()
        return binding.root
    }

    private fun FragmentMainBinding.setTabs() {
        val fragmentList = arrayListOf(
            HomeFragment(),
            UserAudioFragment(),
            SettingsFragment()
        )
        val adapter = ViewPagerAdapter(fragmentList, childFragmentManager, lifecycle)
        viewpagerMain.adapter = adapter
        viewpagerMain.isUserInputEnabled = false
        TabLayoutMediator(tablayoutMain, viewpagerMain) { tab, position ->
            when(position){
                0->{
                    tab.setIcon(R.drawable.main_tab_icon)
                }
                1-> {
                    tab.setIcon(R.drawable.user_music_tab_icon)
                }
                2-> {
                    tab.setIcon(R.drawable.settings_tab_icon)
                }
            }
        }.attach()
    }

    private fun showAuthorizationActivity() {
        if (checkToken()) {
            val intent = Intent(requireContext(), AuthorizationActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun checkToken(): Boolean {
        val pref = activity?.getSharedPreferences(getString(R.string.authorization_info), Context.MODE_PRIVATE)
        return pref != null && pref.getString(getString(R.string.token_key), "").isNullOrEmpty()
    }
}