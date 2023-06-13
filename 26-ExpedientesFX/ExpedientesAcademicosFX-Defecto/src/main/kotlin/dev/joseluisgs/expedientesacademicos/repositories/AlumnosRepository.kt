package dev.joseluisgs.expedientesacademicos.repositories

import dev.joseluisgs.expedientesacademicos.models.Alumno

interface AlumnosRepository {
    fun findAll(): List<Alumno>
    fun findById(id: Long): Alumno?
    fun save(alumno: Alumno): Alumno
    fun deleteById(id: Long)
    fun deleteAll()
    fun saveAll(alumnos: List<Alumno>): List<Alumno>
}