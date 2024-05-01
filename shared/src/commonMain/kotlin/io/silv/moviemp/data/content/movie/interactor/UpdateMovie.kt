package io.silv.moviemp.data.content.movie.interactor

import io.silv.moviemp.core.SMovie
import io.silv.moviemp.data.content.movie.model.Movie
import io.silv.moviemp.data.content.movie.model.MovieUpdate
import io.silv.moviemp.data.content.movie.repository.MovieRepository
import kotlinx.datetime.Clock

class UpdateMovie(
    private val movieRepository: MovieRepository
) {

    suspend fun await(movieUpdate: MovieUpdate): Boolean {
        return movieRepository.updateMovie(movieUpdate)
    }

    suspend fun awaitUpdateCoverLastModified(id: Long): Boolean {
        return movieRepository.updateMovie(
            MovieUpdate(
                movieId = id,
                posterLastUpdated = Clock.System.now().epochSeconds
            )
        )
    }

    suspend fun awaitUpdateFromSource(
        local: Movie,
        network: SMovie,
        manualFetch: Boolean = false
    ): Boolean {
        val remoteTitle = try {
            network.title
        } catch (_: Exception) {
            ""
        }

        // if the movie isn't a favorite, set its title from source and update in db
        val title = if (remoteTitle.isEmpty() || local.favorite) null else remoteTitle

        val coverLastModified =
            when {
                // Never refresh covers if the url is empty to avoid "losing" existing covers
                network.posterPath.isNullOrEmpty() -> null
                !manualFetch && local.posterUrl == network.posterPath -> null
//                cache.getCustomCoverFile(local.id).exists() -> {
//                    cache.deleteFromCache(local, false)
//                    null
//                }
//                else -> {
//                   // cache.deleteFromCache(local, false)
//                    Clock.System.now().toEpochMilliseconds()
//                }
                else -> null
            }

        val thumbnailUrl = network.posterPath?.takeIf { it.isNotEmpty() }

        return movieRepository.updateMovie(
            MovieUpdate(
                movieId = local.id,
                title = title,
                posterLastUpdated = coverLastModified,
                productionCompanies = network.productionCompanies,
                overview = network.overview,
                genres = network.genres?.map { it.second },
                posterUrl = thumbnailUrl,
                status = network.status,
                externalUrl = network.url,
                genreIds = network.genreIds,
                originalLanguage = network.originalLanguage,
                popularity = network.popularity,
                voteCount = network.voteCount,
                releaseDate = network.releaseDate,
                favorite = null
            ),
        )
    }
}
