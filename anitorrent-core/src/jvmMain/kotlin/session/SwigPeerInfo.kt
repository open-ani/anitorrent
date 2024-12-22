package org.openani.anitorrent.anitorrent.session

import org.openani.anitorrent.anitorrent.HandleId
import org.openani.anitorrent.anitorrent.binding.peer_info_t
import org.openani.anitorrent.api.peer.PeerInfo

class SwigPeerInfo(
    native: peer_info_t,
) : PeerInfo {
    override val handle: HandleId = native.torrent_handle_id
    override val id: CharArray = native.peer_id.toCharArray()
    override val client: String = native.client
    override val ipAddr: String = native.ip_addr
    override val ipPort: Int = native.ip_port
    override val progress: Float = native.progress
    override val totalDownload: Long = native.total_download
    override val totalUpload: Long = native.total_upload
    override val flags: Long = native.flags
}