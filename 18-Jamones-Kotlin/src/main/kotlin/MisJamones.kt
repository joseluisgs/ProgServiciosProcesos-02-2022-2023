package es.joseluisgs.dam

import jamones.FabricaJamones


// Ejecutamos la fabrica de jamones, cuanto más jamones más apruebo!!!
fun main() {
    println("Jamones Kotlin")
    FabricaJamones.runFabricaSync()
    FabricaJamones.runFabricaLock()
}

