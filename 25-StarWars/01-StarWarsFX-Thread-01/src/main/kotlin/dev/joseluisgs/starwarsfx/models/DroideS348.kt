package dev.joseluisgs.starwarsfx.models

import java.util.*
import kotlin.math.roundToInt


class DroideS348(model: TipoDroide, energia: Int) : Droide(model, energia), Defensa {
    var numSerie = UUID.randomUUID().toString().substring(0, 5)

    override fun toString(): String {
        return "M: $model, E: $energia, NS: $numSerie"
    }

    override fun toArea(): String {
        return "S" // color rojo
    }

    override fun defender(ataque: Int): Int {
        val defensa = (Math.random() * (12 - 3) + 3).roundToInt()
        return if (defensa > ataque) {
            0
        } else {
            ataque - defensa
        }
    }
}

