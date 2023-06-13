package dev.joseluisgs.expedientesacademicos.repositories

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.joseluisgs.expedientesacademicos.mappers.toModel
import dev.joseluisgs.expedientesacademicos.models.Alumno
import dev.joseluisgs.expedientesacademicos.services.database.SqlDeLightClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class AlumnosRepositoryImpl(
    private val databaseClient: SqlDeLightClient
) : AlumnosRepository {

    val database = databaseClient.database


    override suspend fun findAll(): Flow<List<Alumno>> = withContext(Dispatchers.IO) {
        logger.debug { "findAll" }
        return@withContext database.selectAll().asFlow().mapToList(Dispatchers.IO)
            .map { it.map { alumnoEntity -> alumnoEntity.toModel() } }

    }

    override suspend fun findById(id: Long): Alumno? = withContext(Dispatchers.IO) {
        logger.debug { "findById: $id" }

        return@withContext database.selectById(id).executeAsOneOrNull()?.toModel()
    }

    override suspend fun save(alumno: Alumno): Alumno = withContext(Dispatchers.IO) {
        logger.debug { "save: $alumno" }
        return@withContext if (alumno.isNewAlumno()) {
            create(alumno)
        } else {
            update(alumno)
        }
    }

    private fun create(alumno: Alumno): Alumno {
        logger.debug { "create: $alumno" }
        // Insertamos y recuperamos el ID, transacción por funcion de sqlite (mira el .sq)
        database.transaction {
            database.insert(
                apellidos = alumno.apellidos,
                nombre = alumno.nombre,
                email = alumno.email,
                fechaNacimiento = alumno.fechaNacimiento.toString(),
                calificacion = alumno.calificacion,
                repetidor = if (alumno.repetidor) 1L else 0L,
                imagen = alumno.imagen
            )
        }
        return database.selectLastInserted().executeAsOne().toModel()
    }

    private fun update(alumno: Alumno): Alumno {
        logger.debug { "update: $alumno" }
        database.update(
            id = alumno.id,
            apellidos = alumno.apellidos,
            nombre = alumno.nombre,
            email = alumno.email,
            fechaNacimiento = alumno.fechaNacimiento.toString(),
            calificacion = alumno.calificacion,
            repetidor = if (alumno.repetidor) 1L else 0L,
            imagen = alumno.imagen
        )
        return alumno
    }

    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        logger.debug { "deleteById: $id" }
        return@withContext database.delete(id)
    }

    override suspend fun deleteAll() = withContext(Dispatchers.IO) {
        logger.debug { "deleteAll" }
        return@withContext database.deleteAll()
    }

    override suspend fun saveAll(alumnos: List<Alumno>): Flow<List<Alumno>> = withContext(Dispatchers.IO) {
        logger.debug { "saveAll: $alumnos" }
        database.transaction {
            alumnos.forEach { alumno ->
                if (alumno.isNewAlumno()) {
                    create(alumno)
                } else {
                    update(alumno)
                }
            }
        }
        return@withContext findAll()
    }
}