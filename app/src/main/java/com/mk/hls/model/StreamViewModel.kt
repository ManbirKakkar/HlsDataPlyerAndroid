package com.mk.hls.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.mk.hls.ui.StreamItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StreamViewModel(application: Application) : AndroidViewModel(application) {
    private val _streams = MutableLiveData<Pair<List<StreamItem>, List<StreamItem>>>()
    val streams: LiveData<Pair<List<StreamItem>, List<StreamItem>>> = _streams
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun loadStreams(url: String) {
        _loading.postValue(true)
        _streams.postValue(Pair(emptyList(), emptyList()))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val py = Python.getInstance()
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
            }finally {
                _loading.postValue(false)
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