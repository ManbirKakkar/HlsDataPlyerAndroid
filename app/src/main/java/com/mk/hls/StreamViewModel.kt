package com.mk.hls

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StreamViewModel(application: Application) : AndroidViewModel(application) {
    private val _streams = MutableLiveData<Pair<List<StreamItem>, List<StreamItem>>>()
    val streams: LiveData<Pair<List<StreamItem>, List<StreamItem>>> = _streams

    fun loadStreams(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val py = Python.getInstance() // Now safe to call directly
                val result = py.getModule("extract_streams")
                    .callAttr("get_dash_and_hls_urls", url)

                Log.d("DATA", "Fetching streams for URL: $url")
                Log.d("DATA", "Raw Python result: ${result}")

                val dash = parseStreams(result.asList()[0])
                val hls = parseStreams(result.asList()[1])
                Log.d("DATA", "Parsed DASH: ${dash.size}, HLS: ${hls.size}")

                _streams.postValue(Pair(dash, hls))
            } catch (e: Exception) {
                Log.e("DATA", "Error loading streams", e)
            }
        }
    }

    private fun parseStreams(pyList: PyObject): List<StreamItem> {
        return pyList.asList().map { urlPyObject ->
            StreamItem(
                url = urlPyObject.toString(),
                protocol = if (urlPyObject.toString().endsWith(".mpd")) "DASH" else "HLS"
            )
        }
    }
}