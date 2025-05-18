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

class MainActivity : AppCompatActivity(), StreamListFragment.StreamSelectionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: StreamsPagerAdapter
    private val viewModel: StreamViewModel by viewModels()

    companion object {
        var SELECTED_URL = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupObservers()

        binding.btnExtract.setOnClickListener {
            val url = binding.etUrl.text.toString().trim()
            if (url.isEmpty()) {
                binding.etUrl.error = "Please enter a YouTube URL"
                return@setOnClickListener
            }
            viewModel.loadStreams(url)
        }

        binding.exoPlayer.setOnClickListener {
            playWithExoPlayer()
        }
    }

    private fun playWithExoPlayer() {
        if (binding.tvSelected.text.isNullOrBlank()) {
            Toast.makeText(this, "No URL found", Toast.LENGTH_SHORT).show()
        } else {
            SELECTED_URL = binding.tvSelected.text.toString().replace("URL: ", "").trim()
            startActivity(Intent(this, ExoPlayerActivity::class.java))
        }
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

    override fun onStreamSelected(stream: StreamItem) {
        binding.tvSelected.text = "URL: ${stream.url}"
    }
}