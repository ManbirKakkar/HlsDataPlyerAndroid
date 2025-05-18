package com.mk.hls.exo

import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import java.io.ByteArrayOutputStream
import java.io.IOException

class PlaylistCaptureDataSource(
    private val upstreamDataSource: DataSource
) : DataSource {

    private var dataSpec: DataSpec? = null
    private val outputStream = ByteArrayOutputStream()

    override fun addTransferListener(transferListener: TransferListener) {
        // Forward the TransferListener to the upstream DataSource
        upstreamDataSource.addTransferListener(transferListener)
    }

    override fun open(dataSpec: DataSpec): Long {
        this.dataSpec = dataSpec
        return upstreamDataSource.open(dataSpec)
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val bytesRead = upstreamDataSource.read(buffer, offset, length)
        if (bytesRead != -1) {
            outputStream.write(buffer, offset, bytesRead)
        } else {
            // Transfer ended, process the captured data
            processCapturedData()
        }
        return bytesRead
    }

    private fun processCapturedData() {
        val uri = dataSpec?.uri.toString()
        if (uri.endsWith(".m3u8") || uri.endsWith(".mpd")) {
            val playlistData = outputStream.toString("UTF-8")
            println("Playlist for $uri:\n$playlistData")
        }
    }

    override fun close() {
        upstreamDataSource.close()
    }

    override fun getUri() = upstreamDataSource.uri

    override fun getResponseHeaders() = upstreamDataSource.responseHeaders
}