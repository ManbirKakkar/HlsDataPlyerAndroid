package com.mk.hls.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mk.hls.ui.StreamItem
import com.mk.hls.ui.StreamListFragment

class StreamsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private var dashStreams = emptyList<StreamItem>()
    private var hlsStreams = emptyList<StreamItem>()

    fun submitLists(dash: List<StreamItem>, hls: List<StreamItem>) {
        dashStreams = dash
        hlsStreams = hls
        notifyDataSetChanged()
    }

    override fun getItemCount() = 2

    override fun createFragment(position: Int) = when (position) {
        0 -> StreamListFragment.newInstance(dashStreams)
        1 -> StreamListFragment.newInstance(hlsStreams)
        else -> throw IllegalArgumentException()
    }
}