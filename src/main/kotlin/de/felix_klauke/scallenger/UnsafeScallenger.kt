package de.felix_klauke.scallenger

import de.felix_klauke.scallenger.subject.ScallengerSubject
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
        return memoryAccessor.compareAndSwapInt(null, bufferAddress + ScallengerConstants.LOCK_OFFSET, first, second);
    }
}