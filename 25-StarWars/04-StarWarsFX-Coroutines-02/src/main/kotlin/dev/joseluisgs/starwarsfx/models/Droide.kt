package dev.joseluisgs.starwarsfx.models


abstract class Droide(val model: TipoDroide = TipoDroide.SW348, var energia: Int = 0) {

    val isAlive: Boolean
        get() = energia > 0

    override fun toString(): String {
        return "Droide{M: $model, E: $energia}"
    }

    open fun toArea(): String {
        return "\uD83D\uDD34"
    }

    enum class TipoDroide {
        SW348,
        SW447,
        SW4421
    }
}

