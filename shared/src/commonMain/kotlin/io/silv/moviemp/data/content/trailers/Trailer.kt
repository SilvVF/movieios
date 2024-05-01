package io.silv.moviemp.data.content.trailers

import io.silv.moviemp.core.STrailer

data class Trailer(
    val id: String,
    val key: String,
    val name: String,
    val official: Boolean,
    val publishedAt: String,
    val site: String,
    val size: Int,
    val type: String
)

fun STrailer.toDomain(): Trailer {
    return Trailer(
        id = trailerId,
        key = key,
        name = name,
        official = official,
        publishedAt = publishedAt,
        site = site,
        size = size,
        type = type,
    )
}

data class TrailerUpdate(
    val id: String,
    val key: String?,
    val name: String?,
    val official: Boolean?,
    val publishedAt: String?,
    val site: String?,
    val size: Int?,
    val type: String?
)

fun Trailer.toTrailerUpdate(): TrailerUpdate {
    return TrailerUpdate(
        id = id,
        key = key,
        name = name,
        official = official,
        publishedAt = publishedAt,
        site = site,
        size = size,
        type = type,
    )
}

