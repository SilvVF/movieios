package io.silv.moviemp.data.network.service.tmdb

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.bearerAuth
import io.ktor.client.utils.CacheControl
import io.silv.moviemp.data.network.ratelimit.TokenBuckets
import io.silv.wutnextios.BuildKonfig
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext
import kotlin.properties.Delegates
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TokenBucketPluginConfig {
    var permits by Delegates.notNull<Long>()
    var period: Duration = 1.seconds
}

val TokenBucketPlugin: ClientPlugin<TokenBucketPluginConfig> =
    createClientPlugin("TokenBucketPlugin", ::TokenBucketPluginConfig) {

        val permits = pluginConfig.permits
        val period = pluginConfig.period.inWholeNanoseconds

        val bucket =
            TokenBuckets.builder()
                .withCapacity(permits)
                .withFixedIntervalRefillStrategy(permits, period)
                .build()

        onRequest { req, _->

            coroutineContext.ensureActive()

            bucket.consume(permits)

            req.bearerAuth(BuildKonfig.TMDB_ACCESS_TOKEN)

            coroutineContext.ensureActive()
        }
        onResponse {
            if(it.headers["Cache-Control"] == CacheControl.ONLY_IF_CACHED) {
                bucket.refill(1)
            }
        }
    }