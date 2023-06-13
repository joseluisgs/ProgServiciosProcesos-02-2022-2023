package dev.joseluisgs.starwarsfx.models


class DroideT421(model: TipoDroide, energia: Int) : Droide(model, energia) {
    var velocidad = 100

    override fun toString(): String {
        return "M: $model, E: $energia, V: $velocidad"
    }

    override fun toArea(): String {
        return "T" // color negro
    }
}

