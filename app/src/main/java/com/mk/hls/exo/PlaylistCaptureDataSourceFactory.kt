package com.mk.hls.exo

import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSource.Factory

class PlaylistCaptureDataSourceFactory(
    private val upstreamFactory: DataSource.Factory
) : DataSource.Factory {

    override fun createDataSource(): DataSource {
        return PlaylistCaptureDataSource(upstreamFactory.createDataSource())
    }
}