package io.silv.moviemp.data.content.tv.interactor

import io.silv.moviemp.data.content.Filters
import io.silv.moviemp.data.content.GenreMode
import io.silv.moviemp.data.content.tv.SourceTVPagingSource
import io.silv.moviemp.core.STVShow
import io.silv.moviemp.data.network.model.toSTVShow
import io.silv.moviemp.data.network.service.tmdb.TMDBConstants
import io.silv.moviemp.data.network.service.tmdb.TMDBTVShowService


data class TVPage(val shows: List<STVShow>, val hasNextPage: Boolean)

class DiscoverTVPagingSource(
    private val filters: Filters,
    private val movieService: TMDBTVShowService
): SourceTVPagingSource() {

    override suspend fun getNextPage(page: Int): TVPage {
        val response = movieService.discover(
            page = page,
            genres = TMDBConstants.genresString(
                filters.genres.map { it.name },
                if(filters.genreMode == GenreMode.Or) TMDBConstants.JOIN_MODE_MASK_OR else TMDBConstants.JOIN_MODE_MASK_AND
            ),
            sortBy = filters.sortingOption.sort,
            companies = filters.companies.value.ifBlank { null },
            people = filters.people.value.ifBlank { null },
            keywords = filters.keywords.value.ifBlank { null },
            year = filters.year.value.toIntOrNull(),
            voteAverage = filters.voteAverage.value.toFloatOrNull(),
            voteCount = filters.voteCount.value.toFloatOrNull()
        )

        return TVPage(
            shows = response.results.map { it.toSTVShow() },
            hasNextPage = response.page < response.totalPages
        )
    }
}

class SearchTVPagingSource(
    private val query: String,
    private val movieService: TMDBTVShowService
): SourceTVPagingSource() {

    override suspend fun getNextPage(page: Int): TVPage {
        val response = movieService.search(
            query = query,
            page = page
        )

        return TVPage(
            shows = response.results.map { it.toSTVShow() },
            hasNextPage = response.page < response.totalPages
        )
    }
}


class NowPlayingTVPagingSource(
    private val movieService: TMDBTVShowService
): SourceTVPagingSource() {

    override suspend fun getNextPage(page: Int): TVPage {
        val response = movieService.tvList(
            type = TMDBTVShowService.TVType.NowPlaying.toString(),
            page = page
        )

        return TVPage(
            shows = response.results.map { it.toSTVShow() },
            hasNextPage = response.page < response.totalPages
        )
    }
}


class TopRatedTVPagingSource(
    private val movieService: TMDBTVShowService
): SourceTVPagingSource() {

    override suspend fun getNextPage(page: Int): TVPage {
        val response = movieService.tvList(
            type = TMDBTVShowService.TVType.TopRated.toString(),
            page = page
        )

        return TVPage(
            shows = response.results.map { it.toSTVShow() },
            hasNextPage = response.page < response.totalPages
        )
    }
}

class UpcomingTVPagingSource(
    private val movieService: TMDBTVShowService
): SourceTVPagingSource() {

    override suspend fun getNextPage(page: Int): TVPage {
        val response = movieService.tvList(
            type = TMDBTVShowService.TVType.Upcoming.toString(),
            page = page
        )

        return TVPage(
            shows = response.results.map { it.toSTVShow() },
            hasNextPage = response.page < response.totalPages
        )
    }
}

class PopularTVPagingSource(
    private val movieService: TMDBTVShowService
): SourceTVPagingSource() {

    override suspend fun getNextPage(page: Int): TVPage {

        val response = movieService.tvList(
            type = TMDBTVShowService.TVType.Popular.toString(),
            page = page
        )

        return TVPage(
            shows = response.results.map { it.toSTVShow() },
            hasNextPage = response.page < response.totalPages
        )
    }
}