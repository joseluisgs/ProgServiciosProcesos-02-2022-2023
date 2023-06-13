package dev.joseluisgs.starwarsfx.factories

import dev.joseluisgs.starwarsfx.models.Droide
import dev.joseluisgs.starwarsfx.models.Droide.TipoDroide
import dev.joseluisgs.starwarsfx.models.DroideS348
import dev.joseluisgs.starwarsfx.models.DroideT421
import dev.joseluisgs.starwarsfx.models.DroideW447

class DroideGenerator {
    fun randomDroide(): Droide {
        val random = (Math.random() * 10).toInt()
        return if (random < 3) {
            DroideW447(TipoDroide.SW447, 50)
        } else if (random < 8) {
            DroideS348(TipoDroide.SW348, 100)
        } else {
            val energia = (Math.random() * (150 - 100)).toInt() + 100
            DroideT421(TipoDroide.SW4421, energia)
        }
    }
}

