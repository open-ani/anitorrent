package org.openani.anitorrent.anitorrent

import org.openani.anitorrent.api.HttpFileDownloader
import org.openani.anitorrent.api.TorrentDownloaderConfig
import org.openani.anitorrent.api.TorrentDownloaderFactory
import org.openani.anitorrent.api.TorrentLibraryLoader
import me.him188.ani.utils.io.SystemPath
import kotlin.coroutines.CoroutineContext

class AnitorrentDownloaderFactory : TorrentDownloaderFactory {
    override val name: String get() = "Anitorrent" // don't change

    override val libraryLoader: TorrentLibraryLoader get() = getAnitorrentTorrentLibraryLoader()

    override fun createDownloader(
        rootDataDirectory: SystemPath,
        httpFileDownloader: HttpFileDownloader,
        torrentDownloaderConfig: TorrentDownloaderConfig,
        parentCoroutineContext: CoroutineContext
    ): AnitorrentTorrentDownloader<*, *> =
        createAnitorrentTorrentDownloader(
            rootDataDirectory,
            httpFileDownloader,
            torrentDownloaderConfig,
            parentCoroutineContext,
        )
}

internal expect fun getAnitorrentTorrentLibraryLoader(): TorrentLibraryLoader
