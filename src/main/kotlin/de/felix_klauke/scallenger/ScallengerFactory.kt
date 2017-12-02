package de.felix_klauke.scallenger

import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Felix Klauke <fklauke@itemis.de>
 */
object ScallengerFactory {

    /**
     * Create a new scallenger instance by its underlying parameters.
     */
    fun createScallenger(dataChunkSize: Long = 4096, path: Path = Paths.get("com.scllngr")) : Scallenger {
        return UnsafeScallenger(dataChunkSize, path)
    }
}