package dev.joseluisgs.expedientesacademicos.models

import java.time.LocalDate

data class Alumno(
    val id: Long = NEW_ALUMNO,
    val apellidos: String,
    val nombre: String,
    val email: String,
    val fechaNacimiento: LocalDate,
    val calificacion: Double,
    val repetidor: Boolean,
    val imagen: String
) {
    companion object {
        const val NEW_ALUMNO = -1L
    }

    val nombreCompleto: String
        get() = "$apellidos, $nombre"

    fun isNewAlumno(): Boolean = id == NEW_ALUMNO

    fun isAprobado(): Boolean = calificacion >= 5.0
}