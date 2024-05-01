package io.silv.moviemp.data.content.tv.interactor

import io.silv.moviemp.data.content.tv.model.TVShow
import io.silv.moviemp.data.content.tv.model.TVShowPoster
import io.silv.moviemp.data.content.tv.repository.ShowRepository
import kotlinx.coroutines.flow.Flow

class GetShow(
    private val showRepository: ShowRepository
) {

    suspend fun await(id: Long): TVShow? {
        return showRepository.getShowById(id)
    }

    fun subscribeOrNull(id: Long): Flow<TVShow?> {
        return showRepository.observeShowByIdOrNull(id)
    }

    fun subscribePartial(id: Long): Flow<TVShowPoster> {
        return showRepository.observeShowPartialById(id)
    }

    fun subscribePartialOrNull(id: Long): Flow<TVShowPoster?> {
        return showRepository.observeShowPartialByIdOrNull(id)
    }

    fun subscribe(id: Long): Flow<TVShow> {
        return showRepository.observeShowById(id)
    }
}