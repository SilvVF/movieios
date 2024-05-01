package io.silv.moviemp.data.content.tv.repository

import androidx.paging.PagingSource
import io.silv.moviemp.data.content.Filters
import io.silv.moviemp.data.content.tv.interactor.DiscoverTVPagingSource
import io.silv.moviemp.data.content.tv.interactor.NowPlayingTVPagingSource
import io.silv.moviemp.data.content.tv.interactor.PopularTVPagingSource
import io.silv.moviemp.data.content.tv.interactor.SearchTVPagingSource
import io.silv.moviemp.data.content.tv.interactor.TopRatedTVPagingSource
import io.silv.moviemp.data.content.tv.interactor.UpcomingTVPagingSource
import io.silv.moviemp.core.SGenre
import io.silv.moviemp.core.STVShow
import io.silv.moviemp.core.Status
import io.silv.moviemp.data.network.service.tmdb.TMDBConstants
import io.silv.moviemp.data.network.service.tmdb.TMDBTVShowService

typealias TVPagingSourceType = PagingSource<Long, STVShow>

interface SourceTVRepository {

    suspend fun getShow(id: Long): STVShow?

    suspend fun getSourceGenres(): List<SGenre>

    fun discover(filters: Filters): TVPagingSourceType

    fun search(query: String): TVPagingSourceType

    fun nowPlaying(): TVPagingSourceType

    fun popular(): TVPagingSourceType

    fun upcoming(): TVPagingSourceType

    fun topRated(): TVPagingSourceType
}

class SourceTVRepositoryImpl(
    private val tvService: TMDBTVShowService
): SourceTVRepository {
    override suspend fun getShow(id: Long): STVShow? {
        val details = runCatching { tvService.details(id) }.getOrNull() ?: return null
        return STVShow.create().apply {
            this.id = id
            url = "https://api.themoviedb.org/3/tv/$id"
            posterPath = "https://image.tmdb.org/t/p/original${details.posterPath}".takeIf { details.posterPath.orEmpty().isNotBlank() }
            adult = details.adult
            releaseDate = details.firstAirDate
            overview = details.overview
            genres = details.genres.map { Pair(it.id, it.name) }
            genreIds = details.genres.map { it.id }
            originalLanguage = details.originalLanguage
            originalTitle = details.originalName
            title = details.name
            backdropPath = details.backdropPath
            popularity = details.popularity
            voteCount = details.voteCount
            voteAverage = details.voteAverage
            productionCompanies = details.productionCompanies.map { it.name }
            status = Status.fromString(details.status)
        }
    }

    override suspend fun getSourceGenres(): List<SGenre> {
        return TMDBConstants.genres
    }

    override fun discover(filters: Filters): TVPagingSourceType {
        return DiscoverTVPagingSource(filters, tvService)
    }

    override fun search(query: String): TVPagingSourceType {
        return SearchTVPagingSource(query, tvService)
    }

    override fun nowPlaying(): TVPagingSourceType {
        return NowPlayingTVPagingSource(tvService)
    }
    override fun popular(): TVPagingSourceType {
        return PopularTVPagingSource(tvService)
    }
    override fun upcoming(): TVPagingSourceType {
        return UpcomingTVPagingSource(tvService)
    }

    override fun topRated(): TVPagingSourceType {
        return TopRatedTVPagingSource(tvService)
    }
}