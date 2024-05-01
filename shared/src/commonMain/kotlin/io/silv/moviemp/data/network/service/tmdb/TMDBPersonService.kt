package io.silv.moviemp.data.network.service.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import io.silv.movie.network.model.person.CombinedCreditsResponse
import io.silv.movie.network.model.person.PersonDetailsResponse

interface TMDBPersonService {

    @GET("person/{person_id}/combined_credits")
    suspend fun combinedCredits(
        @Path("person_id") id: String,
    ): CombinedCreditsResponse

    @GET("person/{person_id}")
    suspend fun details(
        @Path("person_id") id: String,
    ): PersonDetailsResponse
}