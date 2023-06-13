package dev.joseluisgs.expedientesacademicos.di

import dev.joseluisgs.expedientesacademicos.config.AppConfig
import dev.joseluisgs.expedientesacademicos.repositories.AlumnosRepository
import dev.joseluisgs.expedientesacademicos.repositories.AlumnosRepositoryImpl
import dev.joseluisgs.expedientesacademicos.services.database.SqlDeLightClient
import dev.joseluisgs.expedientesacademicos.services.storage.StorageAlumnos
import dev.joseluisgs.expedientesacademicos.services.storage.StorageAlumnosImpl
import dev.joseluisgs.expedientesacademicos.viewmodels.ExpedientesViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val AppDIModule = module {
    singleOf(::AppConfig)
    singleOf(::SqlDeLightClient)
    singleOf(::AlumnosRepositoryImpl) {
        bind<AlumnosRepository>()
    }
    singleOf(::StorageAlumnosImpl) {
        bind<StorageAlumnos>()
    }
    singleOf(::ExpedientesViewModel)
}