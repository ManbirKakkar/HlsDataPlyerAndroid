package com.mk.hls.exo


import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener

class LoggingDataSource(
    private val upstreamDataSource: DataSource
) : DataSource {

    override fun addTransferListener(transferListener: TransferListener) {
        upstreamDataSource.addTransferListener(transferListener)
    }

    override fun open(dataSpec: DataSpec): Long {
        return upstreamDataSource.open(dataSpec)
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val bytesRead = upstreamDataSource.read(buffer, offset, length)
        if (bytesRead != -1) {
            // Log the data packets
            val data = buffer.copyOfRange(offset, offset + bytesRead)
            println("Data Packet: ${data.decodeToString()}")
        }
        return bytesRead
    }

    override fun close() {
        upstreamDataSource.close()
    }

    override fun getUri() = upstreamDataSource.uri

    override fun getResponseHeaders() = upstreamDataSource.responseHeaders
}