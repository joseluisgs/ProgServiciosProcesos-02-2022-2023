package dev.joseluisgs.expedientesacademicos.dto.json

data class AlumnoDto(
    val id: Long,
    val apellidos: String,
    val nombre: String,
    val email: String,
    val fechaNacimiento: String,
    val calificacion: Double,
    val repetidor: Boolean,
    val imagen: String,
)