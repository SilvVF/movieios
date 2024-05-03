package io.silv.moviemp.data.network


import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.silv.moviemp.data.network.service.piped.PipedApi
import io.silv.moviemp.data.network.service.tmdb.TMDBMovieService
import io.silv.moviemp.data.network.service.tmdb.TMDBPersonService
import io.silv.moviemp.data.network.service.tmdb.TMDBRecommendationService
import io.silv.moviemp.data.network.service.tmdb.TMDBTVShowService
import io.silv.moviemp.data.network.service.tmdb.TokenBucketPlugin
import io.silv.wutnextios.BuildKonfig
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

typealias TMDBClient = HttpClient

val networkModule =
    module {

        val accessToken = BuildKonfig.TMDB_ACCESS_TOKEN

        single {
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            }
        }

        single<TMDBClient> {
            HttpClient(get()) {
                install(HttpCache)
                install(Logging)
                install(ContentNegotiation) {
                    json(get())
                }
                install(TokenBucketPlugin) {
                    permits = 50
                    period = 1.seconds
                }
                install(DefaultRequest) {
                    headers.append(HttpHeaders.Authorization, accessToken)

                    parameters {
                        append("api_key", BuildKonfig.TMDB_API_KEY)
                    }
                }
            }
        }

        single<PipedApi> {
            Ktorfit.Builder()
                .baseUrl("https://pipedapi.adminforge.de/")
                .httpClient(HttpClient(engine = get()))
                .build()
                .create()
        }

        single<Ktorfit> {
            Ktorfit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .httpClient(get<TMDBClient>())
                .build()
        }

        single { get<Ktorfit>().create<TMDBRecommendationService>() }

        single { get<Ktorfit>().create<TMDBPersonService>() }

        single { get<Ktorfit>().create<TMDBTVShowService>() }

        single { get<Ktorfit>().create<TMDBMovieService>() }
    }