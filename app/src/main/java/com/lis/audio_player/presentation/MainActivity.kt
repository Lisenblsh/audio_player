package com.lis.audio_player.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.lis.audio_player.R
import com.lis.audio_player.databinding.ActivityMainBinding
import com.lis.audio_player.domain.adapters.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        showAuthorizationActivity()
        binding.setTabs()
        setContentView(binding.root)
    }

    private fun showAuthorizationActivity() {
        if (checkToken()) {
            val intent = Intent(this, AuthorizationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkToken(): Boolean {
        val pref = getSharedPreferences(getString(R.string.authorization_info), Context.MODE_PRIVATE)
        return pref != null && pref.getString(getString(R.string.token_key), "").isNullOrEmpty()
    }

    private fun ActivityMainBinding.setTabs() {
        val fragmentList = arrayListOf(
            MainFragment(),
            SettingsFragment()
        )
        val adapter = ViewPagerAdapter(fragmentList, supportFragmentManager, lifecycle)
        viewpagerMain.adapter = adapter
        viewpagerMain.isUserInputEnabled = false
        TabLayoutMediator(tablayoutMain, viewpagerMain) { tab, position ->
            when(position){
                0-> {
                    tab.setIcon(R.drawable.main_tab_icon)
                }
                1-> {
                    tab.setIcon(R.drawable.settings_tab_icon)
                }
            }
        }.attach()
    }
}
