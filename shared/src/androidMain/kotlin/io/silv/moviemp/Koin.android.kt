package io.silv.moviemp

import app.cash.sqldelight.db.SqlDriver
import io.silv.moviemp.database.SqlDelightDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single<SqlDriver> { SqlDelightDriverFactory(get()).createDriver() }
}