/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

package org.openani.anitorrent.api.files

import org.openani.anitorrent.api.pieces.PieceList
import org.openani.anitorrent.api.pieces.count
import org.openani.anitorrent.api.pieces.first
import org.openani.anitorrent.api.pieces.last
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TorrentFilePieceMatcherTest {
    @Test
    fun `matchPiecesForFile empty`() {
        val list = PieceList.create(10) { 5 }
        TorrentFilePieceMatcher.matchPiecesForFile(list, 0, 0).run {
            assertEquals(0, initialPieceIndex)
            assertEquals(0, count)
        }
    }

    @Test
    fun `matchPiecesForFile 1`() {
        val list = PieceList.create(10) { 5 }
        TorrentFilePieceMatcher.matchPiecesForFile(list, 8, 30).run {
            assertEquals(1, initialPieceIndex)
            assertEquals(7, count)

            assertEquals(5, first().dataStartOffset)
            assertEquals(9, first().dataLastOffset)
            assertEquals(0, first().indexInList)

            assertEquals(35, last().dataStartOffset)
            assertEquals(39, last().dataLastOffset)
        }
    }
}