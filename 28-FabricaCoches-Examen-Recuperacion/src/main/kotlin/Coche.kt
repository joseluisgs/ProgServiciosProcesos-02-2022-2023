import java.time.LocalDateTime

data class Coche(
    val id: Int,
    val idFabrica: Int,
    val modelo: Modelo = Modelo.values().random(),
    val color: Color = Color.values().random(),
    val precio: Double = (10000..20000).random().toDouble(),
    val fechaFabricacacion: LocalDateTime = LocalDateTime.now(),
) {
    enum class Modelo {
        SUPER,
        FAMILIAR
    }

    enum class Color {
        AZUL, ROJO, NEGRO,
    }
}