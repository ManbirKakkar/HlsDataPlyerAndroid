package com.mk.hls.shaka

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.mk.hls.R
import com.mk.hls.ui.MainActivity.Companion.SELECTED_URL
import java.net.URLEncoder

class ShakaPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shaka_player)

        val webView = findViewById<WebView>(R.id.webView).apply {
            settings.apply {
                javaScriptEnabled = true
                mediaPlaybackRequiresUserGesture = false
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            WebView.setWebContentsDebuggingEnabled(true)

            webChromeClient = WebChromeClient() // enable console logs, fullscreen, etc.
            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    Log.d(
                        "ShakaWebView",
                        "JS ${consoleMessage?.messageLevel()} @ ${consoleMessage?.sourceId()}:${consoleMessage?.lineNumber()} – ${consoleMessage?.message()}"
                    )
                    return true
                }

                // For fullscreen video support, if you need it
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) { /* … */ }
                override fun onHideCustomView() { /* … */ }
            }
        }

        // Build a file:// URL with query param
        val encoded = URLEncoder.encode(SELECTED_URL, "UTF-8")
        val url = "file:///android_asset/shaka_player.html?streamUrl=$encoded"
        webView.loadUrl(url)
    }
}
