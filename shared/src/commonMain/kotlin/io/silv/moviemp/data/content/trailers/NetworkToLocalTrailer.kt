package io.silv.moviemp.data.content.trailers

import io.silv.moviemp.data.content.trailers.Trailer
import io.silv.moviemp.data.content.trailers.TrailerRepository

class NetworkToLocalTrailer(
    private val trailerRepository: TrailerRepository,
) {

    suspend fun await(trailer: Trailer, contentId: Long, isMovie: Boolean): Trailer {
        return when (val localTrailer = getTrailer(trailer.id)) {
            null -> {
                insertTrailer(trailer, contentId, isMovie)
                trailer
            }
            else -> localTrailer
        }
    }

    private suspend fun getTrailer(id: String): Trailer? {
        return trailerRepository.getById(id)
    }

    private suspend fun insertTrailer(trailer: Trailer, contentId: Long, isMovie: Boolean) {
        return trailerRepository.insertTrailer(trailer, contentId, isMovie)
    }
}