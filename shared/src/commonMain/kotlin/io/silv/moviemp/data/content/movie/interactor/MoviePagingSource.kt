package io.silv.moviemp.data.content.movie.interactor

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.silv.movie.network.model.movie.MovieListResponse
import io.silv.moviemp.core.SMovie
import io.silv.moviemp.data.content.Filters
import io.silv.moviemp.data.content.GenreMode
import io.silv.moviemp.data.content.movie.SourceMoviePagingSource
import io.silv.moviemp.data.network.TMDBClient
import io.silv.moviemp.data.network.model.toSMovie
import io.silv.moviemp.data.network.service.tmdb.TMDBConstants
import io.silv.moviemp.data.network.service.tmdb.TMDBConstants.JOIN_MODE_MASK_AND
import io.silv.moviemp.data.network.service.tmdb.TMDBConstants.JOIN_MODE_MASK_OR
import io.silv.moviemp.data.network.service.tmdb.TMDBMovieService
import io.silv.wutnextios.BuildKonfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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

    val koin = object : KoinComponent{}
    val client by koin.inject<TMDBClient>()

    override suspend fun getNextPage(page: Int): MoviesPage {
        val response = client
            .get("https://api.themoviedb.org/3/movie/${TMDBMovieService.MovieType.TopRated}?page=$page") {
                headers {
                    append(HttpHeaders.Authorization, BuildKonfig.TMDB_ACCESS_TOKEN)
                }
            }
            .body<MovieListResponse>()

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
