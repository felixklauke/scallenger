package de.felix_klauke.scallenger.subject

import java.util.*

/**
 * A subject that will be written or read from a file.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
data class ScallengerSubject(val data: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScallengerSubject) return false

        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }

    override fun toString(): String {
        return "ScallengerSubject(data=${Arrays.toString(data)})"
    }
}