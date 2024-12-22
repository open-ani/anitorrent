package org.openani.anitorrent.anitorrent

import org.openani.anitorrent.api.TorrentLibraryLoader

internal actual fun getAnitorrentTorrentLibraryLoader(): TorrentLibraryLoader {
    return AnitorrentLibraryLoader
}
