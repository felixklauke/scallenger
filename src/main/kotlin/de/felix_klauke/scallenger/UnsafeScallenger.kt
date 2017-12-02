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

import java.nio.file.Path
import java.util.*


internal class UnsafeScallenger(dataChunkSize: Long, file: Path) : AbstractScallenger(dataChunkSize, file) {

    /**
     * Locking mark.
     */
    private val lock: Int = Random().nextInt(1000000)

    override fun getByte(position: Long): Byte {
        return memoryAccessor.getByte(bufferAddress + position)
    }

    override fun putByte(position: Long, byte: Byte) {
        memoryAccessor.putByte(bufferAddress + position, byte)
    }

    override fun getInt(position: Long): Int {
        return memoryAccessor.getInt(bufferAddress + position)
    }

    override fun putInt(position: Long, int: Int) {
        memoryAccessor.putInt(bufferAddress + position, int)
    }

    override fun lock() {
        while (!compareAndSwapLock(ScallengerConstants.UNLOCK_MARK, lock)) {
            Thread.yield()
        }
    }

    override fun unlock() {
        compareAndSwapLock(lock, ScallengerConstants.UNLOCK_MARK)
    }

    /**
     * Compare and swap the integer at the lock position.
     */
    private fun compareAndSwapLock(first: Int, second: Int) : Boolean {
        return memoryAccessor.compareAndSwapInt(null, bufferAddress + ScallengerConstants.LOCK_OFFSET, first, second)
    }
}