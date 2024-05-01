package io.silv.moviemp.data.content.movie.repository

import androidx.paging.PagingSource
import io.silv.moviemp.data.content.Filters
import io.silv.moviemp.data.content.movie.SourceMoviePagingSource
import io.silv.moviemp.data.content.movie.interactor.DiscoverMoviesPagingSource
import io.silv.moviemp.data.content.movie.interactor.NowPlayingMoviePagingSource
import io.silv.moviemp.data.content.movie.interactor.PopularMoviePagingSource
import io.silv.moviemp.data.content.movie.interactor.SearchMoviePagingSource
import io.silv.moviemp.data.content.movie.interactor.TopRatedMoviePagingSource
import io.silv.moviemp.data.content.movie.interactor.UpcomingMoviePagingSource
import io.silv.moviemp.core.SGenre
import io.silv.moviemp.core.SMovie
import io.silv.moviemp.core.Status
import io.silv.moviemp.data.network.service.tmdb.TMDBConstants
import io.silv.moviemp.data.network.service.tmdb.TMDBMovieService

typealias MoviePagingSourceType = PagingSource<Long, SMovie>

interface SourceMovieRepository {

    suspend fun getMovie(id: Long): SMovie?

    suspend fun getSourceGenres(): List<SGenre>

    fun discoverMovies(filters: Filters): SourceMoviePagingSource

    fun searchMovies(query: String): MoviePagingSourceType

    fun getNowPlayingMovies(): MoviePagingSourceType

    fun getPopularMovies(): MoviePagingSourceType

    fun getUpcomingMovies(): MoviePagingSourceType

    fun getTopRatedMovies(): MoviePagingSourceType
}

class SourceMovieRepositoryImpl(
    private val movieService: TMDBMovieService
): SourceMovieRepository {

    override suspend fun getMovie(id: Long): SMovie? {
        val details = runCatching { movieService.details(id) }.getOrNull() ?: return null
        return SMovie.create().apply {
            this.id = id
            title = details.title
            overview = details.overview
            genres = details.genres.map { Pair(it.id, it.name) }
            genreIds = details.genres.map { it.id }
            originalLanguage = details.originalLanguage
            popularity = details.popularity
            voteCount = details.voteCount
            releaseDate = details.releaseDate
            url = "https://api.themoviedb.org/3/movie/$id"
            posterPath = "https://image.tmdb.org/t/p/original${details.posterPath}".takeIf { details.posterPath.orEmpty().isNotBlank() }
            adult = details.adult
            originalTitle = details.originalTitle
            voteAverage = details.voteAverage
            productionCompanies = details.productionCompanies.map { it.name }
            status = Status.fromString(details.status)
        }
    }

    override suspend fun getSourceGenres(): List<SGenre> {
        return TMDBConstants.genres
    }

    override fun discoverMovies(filters: Filters): SourceMoviePagingSource {
        return DiscoverMoviesPagingSource(filters, movieService)
    }

    override fun searchMovies(query: String): MoviePagingSourceType {
        return SearchMoviePagingSource(query, movieService)
    }

    override fun getNowPlayingMovies(): MoviePagingSourceType {
        return NowPlayingMoviePagingSource(movieService)
    }
    override fun getPopularMovies(): MoviePagingSourceType {
        return PopularMoviePagingSource(movieService)
    }
    override fun getUpcomingMovies(): MoviePagingSourceType {
        return UpcomingMoviePagingSource(movieService)
    }

    override fun getTopRatedMovies(): MoviePagingSourceType {
        return TopRatedMoviePagingSource(movieService)
    }
}