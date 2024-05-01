package io.silv.moviemp.data.content.movie.model

import io.silv.moviemp.core.SMovie
import io.silv.moviemp.core.Status
import kotlinx.datetime.Clock

data class MoviePoster(
    val id: Long,
    val title: String,
    val overview: String,
    val posterLastUpdated: Long,
    val posterUrl: String?,
    val favorite: Boolean,
    val inLibraryLists: Long
)

data class Movie(
    val id: Long,
    val title: String,
    val overview: String,
    val genres: List<String>,
    val genreIds: List<Int>,
    val originalLanguage: String,
    val popularity: Double,
    val voteCount: Int,
    val releaseDate: String,
    val externalUrl: String,
    val posterUrl: String?,
    val posterLastUpdated: Long,
    val favorite: Boolean,
    val inLists: Int,
    val status: Status?,
    val productionCompanies: List<String>?,
    val lastModifiedAt: Long,
    val favoriteModifiedAt: Long,
) {

    val needsInit by lazy { status == null || productionCompanies == null }

    val inList: Boolean
        get() = inLists >= 1

    companion object {
        fun create() = Movie(
            id = -1L,
            posterUrl = "",
            title = "",
            favorite = false,
            posterLastUpdated = Clock.System.now().epochSeconds,
            externalUrl = "",
            overview = "",
            genres = emptyList(),
            originalLanguage = "",
            popularity = 0.0,
            voteCount = 0,
            releaseDate = "",
            genreIds = emptyList(),
            status = null,
            productionCompanies = null,
            favoriteModifiedAt = 0L,
            lastModifiedAt = 0L,
            inLists = 0
       )
    }
}

fun SMovie.toDomain(): Movie {
    return Movie.create().copy(
        id = id,
        title = title,
        posterUrl = posterPath,
        overview = overview,
        genres =  genres?.map { it.second } ?: emptyList(),
        genreIds = genreIds ?: genres?.map { it.first } ?: emptyList(),
        originalLanguage = originalLanguage,
        popularity = popularity,
        voteCount = voteCount,
        releaseDate = releaseDate,
        externalUrl = url,
        status = status,
        productionCompanies = productionCompanies
    )
}


