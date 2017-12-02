package de.felix_klauke.scallenger.accessor

import sun.misc.Unsafe

import java.nio.Buffer

/**
 * Unsafe memory accessor.
 *
 * @author Felix Klauke <fklauke></fklauke>@itemis.de>
 */
class MemoryAccessor private constructor(private val unsafe: Unsafe, private val offset: Long) {

    fun compareAndSwapInt(any: Any? = null, position: Long, compare0: Int, compare1: Int) : Boolean {
        return unsafe.compareAndSwapInt(any, position, compare0, compare1)
    }

    fun getByte(l: Long): Byte {
        return unsafe.getByte(l)
    }

    fun putByte(l: Long, b: Byte) {
        unsafe.putByte(l, b)
    }

    fun getInt(l: Long): Int {
        return unsafe.getInt(l)
    }

    fun putInt(l: Long, i: Int) {
        unsafe.putInt(l, i)
    }

    fun fetchAddress(buffer: Buffer): Long {
        return unsafe.getLong(buffer, offset)
    }

    /**
     * Companion object for factory methods.
     */
    companion object {

        fun createAccessor(): MemoryAccessor? {
            try {
                val field = Unsafe::class.java.getDeclaredField("theUnsafe")
                field.isAccessible = true
                val unsafe = field.get(null) as Unsafe

                return createAccessor(unsafe)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

            return null
        }

        private fun createAccessor(unsafe: Unsafe): MemoryAccessor {
            var offset: Long = 0

            try {
                offset = unsafe.objectFieldOffset(Buffer::class.java.getDeclaredField("address"))
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }

            return MemoryAccessor(unsafe, offset)
        }
    }
}
