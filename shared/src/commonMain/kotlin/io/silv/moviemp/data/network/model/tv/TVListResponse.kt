package io.silv.movie.network.model.tv


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TVListResponse(
    @SerialName("page")
    val page: Int = 0,
    @SerialName("results")
    val results: List<TVResult> = listOf(),
    @SerialName("total_pages")
    val totalPages: Int = 0,
    @SerialName("total_results")
    val totalResults: Int = 0
)

