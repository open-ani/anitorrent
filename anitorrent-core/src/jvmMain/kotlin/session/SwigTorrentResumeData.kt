package org.openani.anitorrent.anitorrent.session

import kotlinx.io.files.Path
import org.openani.anitorrent.anitorrent.binding.torrent_resume_data_t
import me.him188.ani.utils.io.toNioPath
import kotlin.io.path.absolutePathString

class SwigTorrentResumeData(
    private val native: torrent_resume_data_t,
) : TorrentResumeData {
    override fun saveToPath(path: Path) {
        native.save_to_file(path.toNioPath().absolutePathString())
    }
}
