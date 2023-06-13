package dev.joseluisgs.expedientesacademicos.repositories

import dev.joseluisgs.expedientesacademicos.models.Alumno
import kotlinx.coroutines.flow.Flow

interface AlumnosRepository {
    suspend fun findAll(): Flow<List<Alumno>>
    suspend fun findById(id: Long): Alumno?
    suspend fun save(alumno: Alumno): Alumno
    suspend fun deleteById(id: Long)
    suspend fun deleteAll()
    suspend fun saveAll(alumnos: List<Alumno>): Flow<List<Alumno>>
}