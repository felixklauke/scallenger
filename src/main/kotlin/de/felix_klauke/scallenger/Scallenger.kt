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