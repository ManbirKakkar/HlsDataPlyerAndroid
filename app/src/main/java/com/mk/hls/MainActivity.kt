package com.mk.hls

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mk.hls.databinding.ActivityMainBinding
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var pythonStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExtract.setOnClickListener {
            val url = binding.etUrl.text.toString().trim()
            if (url.isEmpty()) {
                binding.etUrl.error = "Please enter a YouTube URL"
                return@setOnClickListener
            }
            ExtractStreamsTask().execute(url)
        }
    }

    private inner class ExtractStreamsTask : AsyncTask<String, Void, Pair<List<String>, List<String>>?>() {

        override fun doInBackground(vararg params: String): Pair<List<String>, List<String>>? {
            try {
                if (!pythonStarted) {
                    Python.start(AndroidPlatform(applicationContext))
                    pythonStarted = true
                }
                val py = Python.getInstance()
                val result: PyObject = py
                    .getModule("extract_streams")
                    .callAttr("get_dash_and_hls_urls", params[0])

                // result.asList() will give a List<PyObject> for the tuple
                val tupleElements = result.asList()
                // Unpack the two lists
                val dashPyList = tupleElements[0].asList()
                val hlsPyList  = tupleElements[1].asList()

                // Convert to List<String>
                val dashUrls = dashPyList.map { it.toString() }
                val hlsUrls  = hlsPyList.map { it.toString() }

                return Pair(dashUrls, hlsUrls)
            } catch (e: Exception) {
                Log.e("PythonError", "Execution failed", e)
                return null
            }
        }

        override fun onPostExecute(result: Pair<List<String>, List<String>>?) {
            if (result == null) {
                binding.tvResult.text = "Failed to extract streams."
                return
            }
            val (dashUrls, hlsUrls) = result

            // Log each URL
            dashUrls.forEach { Log.d("DASH_URL", it) }
            hlsUrls.forEach  { Log.d("HLS_URL", it) }

            // Show in the TextView
            val displayText = buildString {
                append("DASH URLs:\n")
                if (dashUrls.isEmpty()) append("  (none)\n")
                else dashUrls.forEach { append("  • $it\n") }

                append("\nHLS URLs:\n")
                if (hlsUrls.isEmpty()) append("  (none)\n")
                else hlsUrls.forEach { append("  • $it\n") }
            }
            binding.tvResult.text = displayText
        }
    }
}
