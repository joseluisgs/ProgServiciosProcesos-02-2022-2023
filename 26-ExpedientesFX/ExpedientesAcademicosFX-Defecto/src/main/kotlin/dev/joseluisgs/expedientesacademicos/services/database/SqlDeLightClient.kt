package dev.joseluisgs.expedientesacademicos.services.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import database.AlumnosQueries
import dev.joseluisgs.database.AppDatabase
import dev.joseluisgs.expedientesacademicos.config.AppConfig
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files


private val logger = KotlinLogging.logger {}

class SqlDeLightClient(
    private val appConfig: AppConfig
) {
    val database: AlumnosQueries by lazy {
        JdbcSqliteDriver(appConfig.databaseUrl).let { driver ->
            // Creamos la base de datos
            logger.debug { "SqlDeLightClient.init() - Create Schemas" }
            AppDatabase.Schema.create(driver)
            AppDatabase(driver)
        }.alumnosQueries
    }

    init {
        logger.debug { "Inicializando el gestor de Bases de Datos" }
        // Borramos la base de datos
        if (appConfig.databaseInit) {
            logger.debug { "Borrando la base de datos" }
            Files.deleteIfExists(File(appConfig.databaseUrl.removePrefix("jdbc:sqlite:")).toPath())
        }

        if (appConfig.databaseRemoveData) {
            clearData()
        }
    }

    private fun clearData() {
        logger.debug { "Borrando datos de la base de datos" }
        database.transaction {
            database.deleteAll()
        }
    }


}