package io.silv.moviemp.data.network.service.piped

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import io.silv.moviemp.data.network.model.Streams

interface PipedApi {

    @GET("streams/{videoId}")
    suspend fun getStreams(@Path("videoId") videoId: String): Streams
}

