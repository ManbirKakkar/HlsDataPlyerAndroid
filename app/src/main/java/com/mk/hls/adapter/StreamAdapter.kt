package com.mk.hls.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mk.hls.databinding.ItemStreamBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mk.hls.R
import com.mk.hls.ui.StreamItem
import com.mk.hls.util.shortUrl

class StreamAdapter(
    private val onSelectionChanged: (StreamItem) -> Unit
) : ListAdapter<StreamItem, StreamAdapter.StreamViewHolder>(DiffCallback()) {

    private var selectedPosition = -1

    inner class StreamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemStreamBinding.bind(view)

        fun bind(stream: StreamItem, isSelected: Boolean) {
            with(binding) {
                tvUrl.text = stream.url.shortUrl()
                root.setOnClickListener {
                    val previous = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previous)
                    notifyItemChanged(adapterPosition)
                    onSelectionChanged(stream)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StreamViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_stream, parent, false)
    )

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    class DiffCallback : DiffUtil.ItemCallback<StreamItem>() {
        override fun areItemsTheSame(oldItem: StreamItem, newItem: StreamItem) =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: StreamItem, newItem: StreamItem) =
            oldItem == newItem
    }
}