package io.silv.moviemp

import co.touchlab.kermit.Logger
import co.touchlab.kermit.NoTagFormatter
import co.touchlab.kermit.platformLogWriter
import io.silv.moviemp.data.content.contentModule
import io.silv.moviemp.data.network.networkModule
import io.silv.moviemp.database.databaseModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Level.DEBUG
import org.koin.core.logger.Level.ERROR
import org.koin.core.logger.Level.INFO
import org.koin.core.logger.Level.NONE
import org.koin.core.logger.Level.WARNING
import org.koin.core.logger.MESSAGE
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

expect val platformModule: Module

internal object Log {

    val logger = object : org.koin.core.logger.Logger() {
        override fun display(level: Level, msg: MESSAGE) {
            with(Logger) {
                when(level) {
                    DEBUG -> d { msg }
                    INFO -> i { msg }
                    WARNING -> w() { msg }
                    ERROR -> e() { msg }
                    NONE -> Unit
                }
            }
        }
    }
}

fun initKoin(additionalModules: List<Module>): KoinApplication {

    Logger.setLogWriters(platformLogWriter(NoTagFormatter))

    val koinApplication = startKoin {
        logger(
            logger = object : org.koin.core.logger.Logger() {
                override fun display(level: Level, msg: MESSAGE) {
                    with(Logger) {
                        when(level) {
                            DEBUG -> d { msg }
                            INFO -> i { msg }
                            WARNING -> w() { msg }
                            ERROR -> e() { msg }
                            NONE -> Unit
                        }
                    }
                }
            }
        )
        modules(
            additionalModules + platformModule + coreModule + contentModule + databaseModule + networkModule
        )
    }

    return koinApplication
}

private val coreModule = module {

}
