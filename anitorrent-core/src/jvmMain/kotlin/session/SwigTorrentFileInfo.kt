package org.openani.anitorrent.anitorrent.session

import org.openani.anitorrent.anitorrent.binding.torrent_file_t

class SwigTorrentFileInfo(
    private val native: torrent_file_t,
) : TorrentFileInfo {
    override val name: String get() = native.name
    override val path: String get() = native.path
    override val size: Long get() = native.size
}
