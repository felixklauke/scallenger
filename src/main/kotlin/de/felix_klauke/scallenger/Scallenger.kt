package de.felix_klauke.scallenger

import de.felix_klauke.scallenger.subject.ScallengerSubject

/**
 * The central interface that defines the usage of scallenger.
 *
 * @author Felix Klauke <fklauke@itemis.de>
 */
interface Scallenger {

    /**
     * Initialize scallenger.
     */
    fun initialize()

    /**
     * Lock all operations.
     */
    fun lock()

    /**
     * Unlock all operations.
     */
    fun unlock()

    /**
     * Write the given message.
     */
    fun writeMessage(subject: ScallengerSubject)

    /**
     * Read the message.
     */
    fun readMessage() : ScallengerSubject
}