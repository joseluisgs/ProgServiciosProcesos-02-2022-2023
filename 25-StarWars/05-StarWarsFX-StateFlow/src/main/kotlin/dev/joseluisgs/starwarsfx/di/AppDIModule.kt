package dev.joseluisgs.starwarsfx.di

import dev.joseluisgs.starwarsfx.factories.DroideGenerator
import dev.joseluisgs.starwarsfx.viewmodel.StarWarsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val AppDIModule = module {
    // Lo voy a definir todo como Singleton
    // https://insert-koin.io/docs/reference/koin-core/dsl
    singleOf(::DroideGenerator)
    singleOf(::StarWarsViewModel) // B (A) --> Lo hace automáticamente
}
