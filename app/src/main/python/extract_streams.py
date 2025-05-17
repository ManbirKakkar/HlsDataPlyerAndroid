import yt_dlp

def get_dash_and_hls_urls(youtube_url):
    """
    Returns a tuple of two lists: (dash_urls, hls_urls)
    """
    ydl_opts = {
        'quiet': True,
        'simulate': True,
        'forceurl': True,
        'skip_download': True,
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(youtube_url, download=False)

    dash_urls = []
    hls_urls = []

    for fmt in info.get('formats', []):
        url = fmt.get('url', '')
        proto = fmt.get('protocol', '').lower()
        if not url: continue

        if 'dash' in proto or url.endswith('.mpd'):
            dash_urls.append(url)
        elif 'm3u8' in proto or url.endswith('.m3u8'):
            hls_urls.append(url)

    # Deduplicate
    return list(dict.fromkeys(dash_urls)), list(dict.fromkeys(hls_urls))