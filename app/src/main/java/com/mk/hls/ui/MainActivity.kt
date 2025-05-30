package com.mk.hls.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.mk.hls.adapter.StreamsPagerAdapter
import com.mk.hls.databinding.ActivityMainBinding
import com.mk.hls.exo.ExoPlayerActivity
import com.mk.hls.model.StreamViewModel
import com.mk.hls.shaka.ShakaPlayerActivity

class MainActivity : AppCompatActivity(), StreamListFragment.StreamSelectionListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: StreamsPagerAdapter
    private val viewModel: StreamViewModel by viewModels()

    companion object {
        var SELECTED_URL: String = ""
        var TEST_URL =
            "https://manifest.googlevideo.com/api/manifest/hls_playlist/expire/1747716945/ei/8bYraNCLD_zRsfIP5ZTLuAY/ip/2601:642:4f7c:b9e0:fd81:66d9:292f:5eaf/id/750c38c3d5a05dc4/itag/232/source/youtube/requiressl/yes/ratebypass/yes/pfa/1/sgovp/clen%3D16782510%3Bdur%3D212.040%3Bgir%3Dyes%3Bitag%3D136%3Blmt%3D1717051830491480/rqh/1/hls_chunk_host/rr1---sn-o097znz7.googlevideo.com/xpc/EgVo2aDSNQ%3D%3D/met/1747695345,/mh/7c/mm/31,29/mn/sn-o097znz7,sn-n4v7snse/ms/au,rdu/mv/m/mvi/1/pl/34/rms/au,au/initcwndbps/4098750/bui/AecWEAY7gol-MBQi7EsmmThVIDWGs5QPomoq1FDRlpnjFseykT3YmDrP0lqPSw9W4KMepLrIFLnUiLdc/spc/wk1kZhYhWVI5Sy_nHIALBiJLmJCAC89WyzLfkCMMlJQ-RfpQY3M/vprv/1/playlist_type/DVR/dover/13/txp/4535434/mt/1747694947/fvip/2/short_key/1/keepalive/yes/sparams/expire,ei,ip,id,itag,source,requiressl,ratebypass,pfa,sgovp,rqh,xpc,bui,spc,vprv,playlist_type/sig/AJfQdSswRAIgbUfetv7iyYzkOe3vYXSYGkdRTmbXAZXm6BUgXrM4mOwCIC2bojDg_KB6vaEE1vKW21363tquri6Smmq4G1K0lOxY/lsparams/hls_chunk_host,met,mh,mm,mn,ms,mv,mvi,pl,rms,initcwndbps/lsig/ACuhMU0wRQIhAJLGbhiN-PAxC0WCIbPJgUp_0G5VNvn9FTJnexKH8xV-AiBMyoEuaCFHraWVMUJRaez9a_AdeUIJEjCVH6BngjGBDA%3D%3D/playlist/index.m3u8"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupObservers()
        setupListeners()
    }

    private fun setupViewPager() {
        viewPagerAdapter = StreamsPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "DASH"
                1 -> "HLS"
                else -> ""
            }
        }.attach()
    }

    private fun setupObservers() {
        viewModel.streams.observe(this) { (dash, hls) ->
            viewPagerAdapter.submitLists(dash, hls)
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnExtract.isEnabled = !isLoading
        }
    }

    private fun setupListeners() {
        binding.btnExtract.setOnClickListener {
            val url = binding.etUrl.text.toString().trim()
            if (url.isEmpty()) {
                binding.etUrl.error = "Please enter a YouTube URL"
            } else {
                viewModel.loadStreams(url)
            }
        }

        binding.exoPlayer.setOnClickListener {
            handleStreamPlayback(ExoPlayerActivity::class.java)
        }

        binding.ShakaPlayer.setOnClickListener {
            handleStreamPlayback(ShakaPlayerActivity::class.java)
        }
    }

    private fun handleStreamPlayback(playerActivity: Class<*>) {
        val selectedUrl = binding.tvSelected.text.toString().removePrefix("URL: ").trim()
        if (selectedUrl.isBlank()) {
            Toast.makeText(this, "No URL found", Toast.LENGTH_SHORT).show()
        } else {
            SELECTED_URL = selectedUrl
            startActivity(Intent(this, playerActivity))
        }
    }

    override fun onStreamSelected(stream: StreamItem) {
        binding.tvSelected.text = "URL: ${stream.url}"
    }
}
