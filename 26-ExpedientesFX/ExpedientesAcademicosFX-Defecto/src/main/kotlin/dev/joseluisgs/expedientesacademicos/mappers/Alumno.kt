package dev.joseluisgs.expedientesacademicos.mappers

import database.AlumnoEntity
import dev.joseluisgs.expedientesacademicos.dto.json.AlumnoDto
import dev.joseluisgs.expedientesacademicos.models.Alumno
import dev.joseluisgs.expedientesacademicos.viewmodels.ExpedientesViewModel.AlumnoFormulario
import java.time.LocalDate

fun AlumnoDto.toModel(): Alumno {
    return Alumno(
        id,
        apellidos,
        nombre,
        email,
        LocalDate.parse(fechaNacimiento),
        calificacion,
        repetidor,
        imagen
    )
}

fun List<AlumnoDto>.toModel(): List<Alumno> {
    return map { it.toModel() }
}

fun Alumno.toDto(): AlumnoDto {
    return AlumnoDto(
        id,
        apellidos,
        nombre,
        email,
        fechaNacimiento.toString(),
        calificacion,
        repetidor,
        imagen
    )
}

fun List<Alumno>.toDto(): List<AlumnoDto> {
    return map { it.toDto() }
}

fun AlumnoEntity.toModel(): Alumno {
    return Alumno(
        id,
        apellidos,
        nombre,
        email,
        LocalDate.parse(fechaNacimiento),
        calificacion,
        repetidor == 1L,
        imagen
    )
}

fun AlumnoFormulario.toModel(): Alumno {
    return Alumno(
        id = numero,
        apellidos = apellidos,
        nombre = nombre,
        email = email,
        fechaNacimiento = fechaNacimiento,
        calificacion = calificacion,
        repetidor = repetidor,
        imagen = imagen.url ?: "sin-imagen.png"
    )
}

