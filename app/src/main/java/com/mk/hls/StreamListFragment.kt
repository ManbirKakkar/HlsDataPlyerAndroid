package com.mk.hls

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mk.hls.databinding.FragmentStreamListBinding
import kotlinx.parcelize.Parcelize

@Parcelize
data class StreamItem(
    val url: String,
    val protocol: String
) : Parcelable

class StreamListFragment : Fragment() {
    interface StreamSelectionListener {
        fun onStreamSelected(stream: StreamItem)
    }

    private var _binding: FragmentStreamListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StreamAdapter
    private var listener: StreamSelectionListener? = null

    companion object {
        private const val ARG_STREAMS = "streams"

        fun newInstance(streams: List<StreamItem>) = StreamListFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(ARG_STREAMS, ArrayList(streams))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? StreamSelectionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStreamListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val streams = arguments?.getParcelableArrayList<StreamItem>(ARG_STREAMS) ?: emptyList()

        adapter = StreamAdapter { stream ->
            listener?.onStreamSelected(stream)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@StreamListFragment.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        // This is the corrected line:
        adapter.submitList(streams)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}