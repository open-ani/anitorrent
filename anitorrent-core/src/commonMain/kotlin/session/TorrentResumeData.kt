package org.openani.anitorrent.anitorrent.session

import kotlinx.io.files.Path

interface TorrentResumeData {
    fun saveToPath(path: Path)
} 
