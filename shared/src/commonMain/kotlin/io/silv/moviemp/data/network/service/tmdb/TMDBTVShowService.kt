package io.silv.moviemp.data.network.service.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.silv.movie.network.model.person.CreditsResponse
import io.silv.movie.network.model.tv.TVDetailsResponse
import io.silv.movie.network.model.tv.TVDiscoverResponse
import io.silv.movie.network.model.tv.TVListResponse
import io.silv.movie.network.model.tv.TVSearchResponse
import io.silv.movie.network.model.tv.TVVideoResponse


interface TMDBTVShowService {

    @GET("tv/{series_id}/videos")
    suspend fun videos(
        @Path("series_id") id: Long,
        @Query("language") language: String = "en-US"
    ): TVVideoResponse

    @GET("tv/{id}")
    suspend fun details(
        @Path("id") id: Long,
    ): TVDetailsResponse

    @GET("tv/{series_id}/credits")
    suspend fun credits(
        @Path("series_id") id: Long
    ): CreditsResponse


    @GET("tv/{type}")
    suspend fun tvList(
        @Path("type") type: String,
        @Query("language") language: String? = "en-US",
        @Query("page") page: Int = 1,
        @Query("timezone") timeZone: String? = null
    ): TVListResponse

    @GET("search/tv")
    suspend fun search(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean? = false,
        @Query("language") language: String? = "en-US",
        @Query("year") year: String? = null
    ): TVSearchResponse

    @GET("discover/tv")
    suspend fun discover(
        @Query("page") page: Int,
        @Query("first_air_date_year") year: Int? = null,
        @Query("with_genres") genres: String? = null,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String? = "en-US",
        @Query("vote_average") voteAverage: Float? = null,
        @Query("vote_count") voteCount: Float? = null,
        @Query("with_keywords") keywords: String? = null,
        @Query("with_people") people: String? = null,
        @Query("with_companies") companies: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc",
    ): TVDiscoverResponse

    enum class TVType {
        Popular { override fun toString(): String { return "popular" } },
        NowPlaying { override fun toString(): String { return "airing_today" }},
        TopRated { override fun toString(): String { return "top_rated" }},
        Upcoming { override fun toString(): String { return "on_the_air" }},
    }
}