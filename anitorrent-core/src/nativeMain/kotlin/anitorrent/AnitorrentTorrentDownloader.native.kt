package org.openani.anitorrent.anitorrent

import kotlinx.io.files.Path
import org.openani.anitorrent.api.HttpFileDownloader
import org.openani.anitorrent.api.TorrentDownloaderConfig
import kotlin.coroutines.CoroutineContext

internal actual fun createAnitorrentTorrentDownloader(
    rootDataDirectory: Path,
    httpFileDownloader: HttpFileDownloader,
    torrentDownloaderConfig: TorrentDownloaderConfig,
    parentCoroutineContext: CoroutineContext
): AnitorrentTorrentDownloader<*, *> {
    TODO("Not yet implemented")
}
