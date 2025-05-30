package com.mk.hls.shaka

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.mk.hls.R
import com.mk.hls.ui.MainActivity.Companion.TEST_URL

class ShakaPlayerActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val hlsUrl = TEST_URL

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shaka_player)

        webView = findViewById(R.id.webView)

        with(webView.settings) {
            javaScriptEnabled = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_NO_CACHE
        }

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.evaluateJavascript("setVideoUrl('$hlsUrl');", null)
            }
        }

        webView.loadUrl("file:///android_asset/shaka_player.html")
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}
