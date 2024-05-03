package io.silv.moviemp.viewmodel

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import app.cash.paging.Pager
import app.cash.paging.cachedIn
import app.cash.paging.map
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import io.silv.moviemp.data.content.ContentPagedType
import io.silv.moviemp.data.content.movie.interactor.GetMovie
import io.silv.moviemp.data.content.movie.interactor.GetRemoteMovie
import io.silv.moviemp.data.content.movie.interactor.NetworkToLocalMovie
import io.silv.moviemp.data.content.movie.model.Movie
import io.silv.moviemp.data.content.movie.model.toDomain
import io.silv.moviemp.data.content.movie.repository.SourceMovieRepository
import io.silv.moviemp.data.content.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class MovieBrowsePresenter(
    private val networkToLocalMovie: NetworkToLocalMovie,
    private val getRemoteMovie: GetRemoteMovie,
    private val getMovie: GetMovie,
    private val sourceMovieRepository: SourceMovieRepository,
) {
    @NativeCoroutines
    suspend fun genres() = sourceMovieRepository.getSourceGenres().map { it.toDomain() }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @NativeCoroutines
    fun observeMoviePoster(id: Long): Flow<Movie> {
        return getMovie.subscribe(id)
    }

    @NativeCoroutines
    operator fun invoke(type: ContentPagedType): Flow<PagingData<Movie>> {
        return Pager(
            PagingConfig(pageSize = 25)
        ) {
            getRemoteMovie.subscribe(type)
        }.flow.map { pagingData ->
            val seenIds = mutableSetOf<Long>()
            pagingData
                .filter { seenIds.add(it.id) && it.posterPath.isNullOrBlank().not() }
                .map { sMovie ->
                    networkToLocalMovie.await(sMovie.toDomain())
                }
        }
            .cachedIn(scope)
    }
}

