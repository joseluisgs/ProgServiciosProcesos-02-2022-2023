package dev.joseluisgs.starwarsfx.models

import kotlin.math.roundToInt


class DroideW447(model: TipoDroide, energia: Int) : Droide(model, energia) {
    var defensa = (Math.random() * (10 - 5) + 5).roundToInt()

    override fun toString(): String {
        return "M: " + model +
                ", E: " + energia +
                ", D: " + defensa
    }

    override fun toArea(): String {
        return "W" // color azul
    }
}

