package jamones.models

import kotlin.math.floor

class Jamon(val id: Int, val idGranja: String) {

    var peso: Int = (6..9).random()
    var lote = 0

    override fun toString(): String {
        return "Jamon{lote=$lote, id=$id, peso=$peso, idGranja=$idGranja}"
    }

}
