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

import de.felix_klauke.scallenger.accessor.MemoryAccessor
import de.felix_klauke.scallenger.subject.ScallengerSubject
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/**
 * Abstraction of the read / write mechanism.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
abstract class AbstractScallenger(private val dataChunkSize: Long, private val file: Path) : Scallenger {

    /**
     * If scallenger is initialized.
     */
    private var initialized: Boolean = false

    /**
     * The buffer that is used to read and write memory.
     */
    private lateinit var buffer: MappedByteBuffer

    /**
     * The address of the mapped byte buffer in the memory.
     */
    var bufferAddress: Long = -1

    /**
     * The channel to the file.
     */
    private val channel: FileChannel = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)

    /**
     * The unsafe accessor to memory.
     */
    val memoryAccessor: MemoryAccessor = MemoryAccessor.createAccessor()!!

    /**
     * Initialize resource.
     */
    override fun initialize() {
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, ScallengerConstants.INITIAL_POSITION, dataChunkSize)
        bufferAddress = memoryAccessor.fetchAddress(buffer)

        initialized = true

        writeHeaders()
    }

    /**
     * Write the file header.
     */
    private fun writeHeaders() {
        if (getInt(ScallengerConstants.STATE_OFFSET.toLong()) == 0) {
            putInt(ScallengerConstants.STATE_OFFSET.toLong(), 1)
        }
    }


    override fun writeMessage(subject: ScallengerSubject) {
        var write = true

        while (write) {
            lock()

            val dataLength = getInt(ScallengerConstants.DATA_LENGTH_OFSSET.toLong())
            write = dataLength != ScallengerConstants.EMPTY

            unlock()
            Thread.yield()
        }

        putInt(ScallengerConstants.DATA_LENGTH_OFSSET.toLong(), subject.data.size)

        for (i in 0 until subject.data.size) {
            putByte((ScallengerConstants.HEADER_OFFSET + i).toLong(), subject.data[i])
        }

        unlock()
    }

    override fun readMessage(): ScallengerSubject {
        var contentLength = 0

        var read = true

        while (read) {
            lock()

            contentLength = getInt(ScallengerConstants.DATA_LENGTH_OFSSET.toLong())
            read = contentLength == ScallengerConstants.EMPTY

            unlock()
            Thread.yield()
        }

        val buf = ByteArray(contentLength)

        for (i in 0 until contentLength) {
            buf[i] = getByte((ScallengerConstants.HEADER_OFFSET + i).toLong())
        }

        putInt(ScallengerConstants.DATA_LENGTH_OFSSET.toLong(), ScallengerConstants.EMPTY)

        return ScallengerSubject(buf)
    }

    /**
     * Get the byte at th egiven position.
     */
    abstract fun getByte(position: Long) : Byte

    /**
     * Put the given byte at the given position.
     */
    abstract fun putByte(position: Long, byte: Byte)

    /**
     * Get the int at the given position.
     */
    abstract fun getInt(position: Long) : Int

    /**
     * Put the given int at the given position.
     */
    abstract fun putInt(position: Long, int: Int)
}