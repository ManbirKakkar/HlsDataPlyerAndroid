package com.mk.hls.util

fun String.shortUrl(): String {
    return replace(Regex("^(https?://[^/]+/).*"), "$1...")
}