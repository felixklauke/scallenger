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
abstract class AbstractScallenger(private val dataChunkSize: Long, file: Path) : Scallenger {

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
    val channel: FileChannel = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE)

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

            write = getInt(ScallengerConstants.DATA_LENGTH_OFSSET.toLong()) != ScallengerConstants.EMPTY

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