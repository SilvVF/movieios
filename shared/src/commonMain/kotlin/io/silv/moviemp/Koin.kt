package io.silv.moviemp

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

expect val platformModule: Module

fun initKoin(additionalModules: List<Module>): KoinApplication {
    val koinApplication = startKoin {
        modules(
            additionalModules + platformModule + coreModule
        )
    }

    return koinApplication
}

private val coreModule = module {

}
