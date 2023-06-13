package dev.joseluisgs.expedientesacademicos.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.models.Alumno
import java.time.LocalDate

fun Alumno.validate(): Result<Alumno, AlumnoError> {
    if (this.nombre.isEmpty()) {
        return Err(AlumnoError.ValidationProblem("El nombre no puede estar vacío"))
    }
    if (this.apellidos.isEmpty()) {
        return Err(AlumnoError.ValidationProblem("Los apellidos no pueden estar vacíos"))
    }
    // Expresion regular de email
    if (this.email.isEmpty() || !Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").matches(this.email)) {
        return Err(AlumnoError.ValidationProblem("El email no puede estar vacío"))
    }
    if (this.fechaNacimiento.isAfter(LocalDate.now())) {
        return Err(AlumnoError.ValidationProblem("La fecha de nacimiento no puede ser posterior a hoy"))
    }
    if (this.calificacion < 0 || this.calificacion > 10) {
        return Err(AlumnoError.ValidationProblem("La calificación debe estar entre 0 y 10"))
    }
    return Ok(this)
}