package io.silv.moviemp.data.content.movie.interactor

import io.silv.moviemp.data.content.Filters
import io.silv.moviemp.data.content.GenreMode
import io.silv.moviemp.data.content.movie.SourceMoviePagingSource
import io.silv.moviemp.core.SMovie
import io.silv.moviemp.data.network.model.toSMovie
import io.silv.moviemp.data.network.service.tmdb.TMDBConstants
import io.silv.moviemp.data.network.service.tmdb.TMDBConstants.JOIN_MODE_MASK_AND
import io.silv.moviemp.data.network.service.tmdb.TMDBConstants.JOIN_MODE_MASK_OR
import io.silv.moviemp.data.network.service.tmdb.TMDBMovieService

data class MoviesPage(val movies: List<SMovie>, val hasNextPage: Boolean)

class DiscoverMoviesPagingSource(
    private val filters: Filters,
    private val movieService: TMDBMovieService
): SourceMoviePagingSource() {
    override suspend fun getNextPage(page: Int): MoviesPage {
        val response = movieService.discover(
            page = page,
            genres = TMDBConstants.genresString(
                filters.genres.map { it.name },
                if(filters.genreMode == GenreMode.Or) JOIN_MODE_MASK_OR else JOIN_MODE_MASK_AND
            ),
            sortBy = filters.sortingOption.sort,
            companies = filters.companies.value.ifBlank { null },
            people = filters.people.value.ifBlank { null },
            keywords = filters.keywords.value.ifBlank { null },
            year = filters.year.value.toIntOrNull(),
            voteAverage = filters.voteAverage.value.toFloatOrNull(),
            voteCount = filters.voteCount.value.toFloatOrNull()
        )

        return MoviesPage(
            movies = response.results.map { it.toSMovie() },
            hasNextPage = response.page < response.totalPages
        )
    }
}

class SearchMoviePagingSource(
    private val query: String,
    private val movieService: TMDBMovieService
): SourceMoviePagingSource() {

    override suspend fun getNextPage(page: Int): MoviesPage {
        val response = movieService.search(
            query = query,
            page = page
        )

        return MoviesPage(
            movies = response.results.map { it.toSMovie() },
            hasNextPage = response.page < response.totalPages
        )
    }
}


class NowPlayingMoviePagingSource(
    private val movieService: TMDBMovieService
): SourceMoviePagingSource() {

    override suspend fun getNextPage(page: Int): MoviesPage {
        val response = movieService.movieList(
            type = TMDBMovieService.MovieType.NowPlaying.toString(),
            page = page
        )

        return MoviesPage(
            movies = response.results.map { it.toSMovie() },
            hasNextPage = response.page < response.totalPages
        )
    }
}


class TopRatedMoviePagingSource(
    private val movieService: TMDBMovieService
): SourceMoviePagingSource() {

    override suspend fun getNextPage(page: Int): MoviesPage {
        val response = movieService.movieList(
            type = TMDBMovieService.MovieType.TopRated.toString(),
            page = page
        )

        return MoviesPage(
            movies = response.results.map { it.toSMovie() },
            hasNextPage = response.page < response.totalPages
        )
    }
}

class UpcomingMoviePagingSource(
    private val movieService: TMDBMovieService
): SourceMoviePagingSource() {

    override suspend fun getNextPage(page: Int): MoviesPage {
        val response = movieService.movieList(
            type = TMDBMovieService.MovieType.Upcoming.toString(),
            page = page
        )

        return MoviesPage(
            movies = response.results.map { it.toSMovie() },
            hasNextPage = response.page < response.totalPages
        )
    }
}

class PopularMoviePagingSource(
    private val movieService: TMDBMovieService
): SourceMoviePagingSource() {

    override suspend fun getNextPage(page: Int): MoviesPage {

        val response = movieService.movieList(
            type = TMDBMovieService.MovieType.Popular.toString(),
            page = page
        )

        return MoviesPage(
            movies = response.results.map { it.toSMovie() },
            hasNextPage = response.page < response.totalPages
        )
    }
}
