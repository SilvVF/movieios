package io.silv.moviemp.data.content.trailers

import io.silv.moviemp.core.STrailer
import io.silv.moviemp.data.network.model.toSTrailer
import io.silv.moviemp.data.network.service.tmdb.TMDBMovieService
import io.silv.moviemp.data.network.service.tmdb.TMDBTVShowService


class GetRemoteTrailers(
    private val tmdbMovieService: TMDBMovieService,
    private val tmdbtvShowService: TMDBTVShowService
) {

    suspend fun awaitMovie(movieId: Long): List<STrailer> {
        return runCatching {
            tmdbMovieService.videos(movieId)
                .results
                .map { result ->
                    result.toSTrailer()
                }
        }
            .getOrDefault(emptyList())
    }

    suspend fun awaitShow(showId: Long): List<STrailer> {
        return runCatching {
            tmdbtvShowService.videos(showId)
                .results
                .map { result ->
                    result.toSTrailer()
                }
        }
            .getOrDefault(emptyList())
    }
}

class GetTVShowTrailers(
    private val trailerRepository: TrailerRepository
) {
    suspend fun await(showId: Long): List<Trailer> {
        return trailerRepository.getTrailersByShowId(showId)
    }
}

class GetMovieTrailers(
    private val trailerRepository: TrailerRepository
) {

    suspend fun await(movieId: Long): List<Trailer> {
        return trailerRepository.getTrailersByMovieId(movieId)
    }
}