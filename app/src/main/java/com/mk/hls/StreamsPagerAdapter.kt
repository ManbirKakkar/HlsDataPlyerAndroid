package com.mk.hls

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class StreamsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private var dashStreams = emptyList<StreamItem>()
    private var hlsStreams = emptyList<StreamItem>()

    fun submitLists(dash: List<StreamItem>, hls: List<StreamItem>) {
        dashStreams = dash
        hlsStreams = hls
        notifyDataSetChanged()  // Force full refresh
    }

    override fun getItemCount() = 2

    override fun createFragment(position: Int) = when (position) {
        0 -> StreamListFragment.newInstance(dashStreams)  // DASH at position 0
        1 -> StreamListFragment.newInstance(hlsStreams)   // HLS at position 1
        else -> throw IllegalArgumentException()
    }
}