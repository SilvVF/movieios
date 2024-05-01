package io.silv.moviemp

import app.cash.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.silv.moviemp.database.SqlDelightDriverFactory
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.Koin
import org.koin.core.module.factory
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import platform.darwin.cache_create

@BetaInteropApi
actual val platformModule = module {
    single<SqlDriver> { SqlDelightDriverFactory().createDriver() }

    single<HttpClientEngine> {
        Darwin.create()
    }

//    val baseKermit = Logger(config = StaticConfig(logWriterList = listOf(NSLogWriter())), tag = "movie")
//    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}

@BetaInteropApi
fun Koin.get(objCClass: ObjCClass, qualifier: Qualifier?, parameter: Any): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, qualifier) { parametersOf(parameter) }
}

@BetaInteropApi
fun Koin.get(objCClass: ObjCClass, parameter: Any): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, null) { parametersOf(parameter) }
}

@BetaInteropApi
fun Koin.get(objCClass: ObjCClass, qualifier: Qualifier?): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, qualifier, null)
}

@BetaInteropApi
fun Koin.get(objCClass: ObjCClass): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, null)
}

@BetaInteropApi
fun Koin.get(objCProtocol: ObjCProtocol, qualifier: Qualifier?): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCProtocol)) { "Could not get original kotlin class for $objCProtocol." }
    return get(kClazz, qualifier, null)
}