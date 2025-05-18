package com.mk.hls.exo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.mk.hls.R
import com.mk.hls.ui.MainActivity.Companion.SELECTED_URL

class ExoPlayerActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exo_player)

        playerView = findViewById(R.id.player_view)

        // Initialize the player
        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player

        // Prepare the MediaSource
        val uri = Uri.parse(SELECTED_URL) // Replace with your HLS or DASH URL
        val mediaSource = buildMediaSource(uri)

        // Prepare the player with the MediaSource
        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        // Create the DataSource.Factory with the TransferListener

        val upstreamFactory = DefaultHttpDataSource.Factory().setTransferListener(ManifestTransferListener())

        // Wrap it with the custom PlaylistCaptureDataSourceFactory
        val dataSourceFactory = PlaylistCaptureDataSourceFactory(upstreamFactory)

        // Create the appropriate MediaSource based on the URI
        return when {
            uri.toString().endsWith(".m3u8") -> {
                HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
            }
            uri.toString().endsWith(".mpd") -> {
                DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
            }
            else -> throw IllegalStateException("Unsupported media type")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}