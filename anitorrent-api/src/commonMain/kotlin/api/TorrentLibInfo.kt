package org.openani.anitorrent.api

class TorrentLibInfo(
    val vendor: String,//  "libtorrent"
    val version: String, // LibTorrent.version()
    val supportsStreaming: Boolean,
)
