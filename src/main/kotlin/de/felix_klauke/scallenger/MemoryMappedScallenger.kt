/*
 * MIT License
 *
 * Copyright (c) 2017 Felix Klauke
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.felix_klauke.scallenger

import java.nio.channels.FileLock
import java.nio.file.Path

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
class MemoryMappedScallenger(dataChunkSize: Long, file: Path) : AbstractScallenger(dataChunkSize, file) {

    private lateinit var lock: FileLock

    override fun getByte(position: Long): Byte {
        return buffer.get(position.toInt())
    }

    override fun putByte(position: Long, byte: Byte) {
        buffer.put(position.toInt(), byte)
    }

    override fun getInt(position: Long): Int {
        return buffer.getInt(position.toInt())
    }

    override fun putInt(position: Long, int: Int) {
        buffer.putInt(position.toInt(), int)
    }

    override fun lock() {
        lock = channel.lock()
    }

    override fun unlock() {
        lock.release()
    }
}