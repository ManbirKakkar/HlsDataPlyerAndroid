package com.mk.hls

fun String.shortUrl(): String {
    return replace(Regex("^(https?://[^/]+/).*"), "$1...")
}