package io.silv.moviemp.data.content.credits

import co.touchlab.kermit.Logger
import io.silv.moviemp.core.SCredit
import io.silv.moviemp.data.network.model.toSCredits
import io.silv.moviemp.data.network.service.tmdb.TMDBMovieService
import io.silv.moviemp.data.network.service.tmdb.TMDBTVShowService

class GetRemoteCredits(
    private val tmdbMovieService: TMDBMovieService,
    private val tmdbtvShowService: TMDBTVShowService
) {

    suspend fun awaitMovie(movieId: Long): List<SCredit> {
        return runCatching {
            tmdbMovieService.credits(movieId)
                .toSCredits()

        }
            .onFailure { Logger.e(it.stackTraceToString()) }
            .getOrDefault(emptyList())
    }

    suspend fun awaitShow(showId: Long): List<SCredit> {
        return runCatching {
            tmdbtvShowService.credits(showId)
                .toSCredits()
        }
            .onFailure { Logger.e(it.stackTraceToString()) }
            .getOrDefault(emptyList())
    }
}

class GetTVShowCredits(
    private val creditRepository: CreditRepository
) {
    suspend fun await(showId: Long): List<Credit> {
        return creditRepository.getByShowId(showId)
    }
}

class GetMovieCredits(
    private val creditRepository: CreditRepository
) {

    suspend fun await(movieId: Long): List<Credit> {
        return creditRepository.getByMovieId(movieId)
    }
}