package io.silv.moviemp.data.network.service.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.silv.movie.network.model.recommendation.MovieRecommendationResponse
import io.silv.movie.network.model.recommendation.TVSeriesRecommendationResponse

interface TMDBRecommendationService {

    @GET("movie/{movie_id}/recommendations")
    suspend fun movieRecommendations(
        @Path("movie_id") id: Int,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): MovieRecommendationResponse

    @GET("tv/{series_id}/recommendations")
    suspend fun showRecommendations(
        @Path("series_id") id: Int,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): TVSeriesRecommendationResponse
}