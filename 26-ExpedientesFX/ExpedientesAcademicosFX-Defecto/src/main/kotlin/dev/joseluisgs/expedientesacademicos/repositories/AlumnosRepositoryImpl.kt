package dev.joseluisgs.expedientesacademicos.repositories

import dev.joseluisgs.expedientesacademicos.mappers.toModel
import dev.joseluisgs.expedientesacademicos.models.Alumno
import dev.joseluisgs.expedientesacademicos.services.database.SqlDeLightClient
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class AlumnosRepositoryImpl(
    private val databaseClient: SqlDeLightClient
) : AlumnosRepository {

    val database = databaseClient.database


    override fun findAll(): List<Alumno> {
        logger.debug { "findAll" }
        return database.selectAll().executeAsList().map { it.toModel() }
    }

    override fun findById(id: Long): Alumno? {
        logger.debug { "findById: $id" }

        return database.selectById(id).executeAsOneOrNull()?.toModel()
    }

    override fun save(alumno: Alumno): Alumno {
        logger.debug { "save: $alumno" }
        return if (alumno.isNewAlumno()) {
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

    override fun deleteById(id: Long) {
        logger.debug { "deleteById: $id" }
        return database.delete(id)
    }

    override fun deleteAll() {
        logger.debug { "deleteAll" }
        return database.deleteAll()
    }

    override fun saveAll(alumnos: List<Alumno>): List<Alumno> {
        logger.debug { "saveAll: $alumnos" }
        return alumnos.map { save(it) }
    }
}