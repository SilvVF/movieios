package io.silv.moviemp.data.network.service.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.silv.movie.network.model.movie.MovieDetailsResponse
import io.silv.movie.network.model.movie.MovieDiscoverResponse
import io.silv.movie.network.model.movie.MovieListResponse
import io.silv.movie.network.model.movie.MovieSearchResponse
import io.silv.movie.network.model.movie.MovieVideoResponse
import io.silv.movie.network.model.person.CreditsResponse

interface TMDBMovieService {

    @GET("movie/{id}/details")
    suspend fun details(@Path("id") id: Long): MovieDetailsResponse

    @GET("movie/{type}")
    suspend fun movieList(
        @Path("type") type: String,
        @Query("language") language: String? = "en-US",
        @Query("page") page: Int = 1,
        @Query("region") region: String? = null
    ): MovieListResponse

    @GET("movie/{movie_id}/videos")
    suspend fun videos(
        @Path("movie_id") id: Long,
        @Query("language") language: String = "en-US"
    ): MovieVideoResponse

    @GET("movie/{movie_id}/credits")
    suspend fun credits(
        @Path("movie_id") id: Long
    ): CreditsResponse

    @GET("search/movie")
    suspend fun search(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieSearchResponse

    @GET("discover/movie")
    suspend fun discover(
        @Query("page") page: Int,
        @Query("primary_release_year") year: Int? = null,
        @Query("vote_average") voteAverage: Float? = null,
        @Query("vote_count") voteCount: Float? = null,
        @Query("with_keywords") keywords: String? = null,
        @Query("with_genres") genres: String? = null,
        @Query("with_people") people: String? = null,
        @Query("with_companies") companies: String? = null,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("certification") certification: String? = null,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("language") language: String? = "en-US",
        @Query("certification.gte") certificationGte: String? = null,
        @Query("certification.lte") certificationLte: String? = null,
        @Query("certification_country") certificationCountry: String? = null,
        @Query("primary_release_year") primaryReleaseYear: Int? = null,
        @Query("primary_release_date.gte") primaryReleaseDateGte: String? = null,
        @Query("primary_release_date.lte") primaryReleaseDateLte: String? = null
    ): MovieDiscoverResponse

    enum class MovieType {
        Popular { override fun toString(): String { return "popular" } },
        NowPlaying { override fun toString(): String { return "now_playing" }},
        TopRated { override fun toString(): String { return "top_rated" }},
        Upcoming { override fun toString(): String { return "upcoming" }},
    }
}