package com.mk.hls

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.mk.hls.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StreamListFragment.StreamSelectionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: StreamsPagerAdapter
    private val viewModel: StreamViewModel by viewModels()

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
    }

    private fun setupViewPager() {
        viewPagerAdapter = StreamsPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "DASH"  // Was previously HLS
                1 -> "HLS"   // Was previously DASH
                else -> ""
            }
        }.attach()
    }

    private fun setupObservers() {
        viewModel.streams.observe(this) { (dash, hls) ->
            viewPagerAdapter.submitLists(dash, hls)
        }
    }

    override fun onStreamSelected(stream: StreamItem) {
        binding.tvSelected.text = "URL: ${stream.url}"
    }
}