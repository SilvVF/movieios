package io.silv.moviemp.data.content

import io.silv.moviemp.core.SGenre

data class Genre(
    val name: String,
    val id: Long? = null
)

fun SGenre.toDomain(): Genre {
    return Genre(name, id)
}
