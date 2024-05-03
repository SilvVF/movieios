package io.silv.moviemp

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.kermit.Logger
import co.touchlab.kermit.NSLogWriter
import co.touchlab.kermit.StaticConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.silv.moviemp.database.SqlDelightDriverFactory
import io.silv.moviemp.viewmodel.MovieBrowsePresenter
import kotlinx.cinterop.BetaInteropApi
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

@BetaInteropApi
actual val platformModule = module {

    single<SqlDriver> { SqlDelightDriverFactory().createDriver() }

    single<HttpClientEngine> {
        Darwin.create()
    }

    val baseKermit = Logger(config = StaticConfig(logWriterList = listOf(NSLogWriter())), tag = "movie")

    single { MovieBrowsePresenter(get(), get(), get(), get()) }

    factory<Logger> { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}

// Koin utilities for iOS injection

fun KoinApplication.Companion.start(userDefaults: NSUserDefaults): KoinApplication = initKoin(
    listOf(
        module {
            single { BundleProvider(bundle = NSBundle.mainBundle) }
        }
    )
)

// Workaround class for injecting an `NSObject` class.
// When not used, an error "KClass of Objective-C classes is not supported." is thrown.
data class BundleProvider(val bundle: NSBundle)

val Koin.movieBrowsePresenter: MovieBrowsePresenter
    get() = get()