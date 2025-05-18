package com.mk.hls.exo

import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener

class ManifestTransferListener : TransferListener {

    override fun onTransferInitializing(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean
    ) {
        val uri = dataSpec.uri.toString()
        //     println("Transfer onTransferInitializing : $uri")
        // Called when data transfer is about to start
    }

    override fun onTransferStart(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean
    ) {
        val uri = dataSpec.uri.toString()

        //  LoggingDataSource(source)
        // println("Transfer dataSpec: $source.")
        // println("Transfer onTransferStart: $uri")
    }

    override fun onBytesTransferred(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean,
        bytesTransferred: Int
    ) {

        val uri = dataSpec.uri.toString()
        // println("Transfer onBytesTransferred: $uri")

        // Called during data transfer
    }

    override fun onTransferEnd(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean
    ) {
        val uri = dataSpec.uri.toString()
        //  println("Transfer onTransferEnd: $uri")
        // Called when data transfer ends

        when {
            uri.endsWith(".m3u8") -> println("HLS Manifest URL: $uri")
            uri.endsWith(".mpd") -> println("DASH Manifest URL: $uri")
        }
    }
}