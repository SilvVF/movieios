package io.silv.moviemp.data.content.tv.repository

import io.silv.moviemp.data.content.tv.ShowMapper
import io.silv.moviemp.data.content.tv.model.TVShow
import io.silv.moviemp.data.content.tv.model.TVShowPoster
import io.silv.moviemp.data.content.tv.model.TVShowUpdate
import io.silv.moviemp.core.Status
import io.silv.moviemp.database.DatabaseHandler
import kotlinx.coroutines.flow.Flow

interface ShowRepository {
    suspend fun getShowById(id: Long): TVShow?
    fun observeShowPartialById(id: Long): Flow<TVShowPoster>
    fun observeShowPartialByIdOrNull(id: Long): Flow<TVShowPoster?>
    fun observeShowById(id: Long): Flow<TVShow>
    fun observeShowByIdOrNull(id: Long): Flow<TVShow?>
    suspend fun insertShow(show: TVShow): Long?
    suspend fun updateShow(update: TVShowUpdate): Boolean
    fun observeFavorites(query: String): Flow<List<TVShow>>
}

class ShowRepositoryImpl(
    private val handler: DatabaseHandler
): ShowRepository {

    override suspend fun getShowById(id: Long): TVShow? {
        return handler.awaitOneOrNull { showQueries.selectById(id, ShowMapper.mapShow) }
    }

    override fun observeShowPartialByIdOrNull(id: Long): Flow<TVShowPoster?> {
        return handler.subscribeToOneOrNull { showQueries.selectShowPartialById(id, ShowMapper.mapShowPoster) }
    }

    override fun observeShowPartialById(id: Long): Flow<TVShowPoster> {
        return handler.subscribeToOne { showQueries.selectShowPartialById(id, ShowMapper.mapShowPoster) }
    }

    override fun observeShowById(id: Long): Flow<TVShow> {
        return handler.subscribeToOne { showQueries.selectById(id, ShowMapper.mapShow) }
    }

    override fun observeShowByIdOrNull(id: Long): Flow<TVShow?> {
        return handler.subscribeToOneOrNull { showQueries.selectById(id, ShowMapper.mapShow) }
    }

    override suspend fun insertShow(show: TVShow): Long? {
        return handler.awaitOneOrNullExecutable(inTransaction = true) {
            showQueries.insert(
                show.id,
                show.title,
                show.overview,
                show.genres.ifEmpty { null },
                show.genreIds.ifEmpty { null },
                show.originalLanguage,
                show.voteCount.toLong(),
                show.releaseDate,
                show.posterUrl,
                show.posterLastUpdated,
                show.favorite,
                show.externalUrl,
                show.popularity,
                show.status?.let { Status.entries.indexOf(it).toLong() }
            )
            showQueries.lastInsertRowId()
        }
    }

    override suspend fun updateShow(update: TVShowUpdate): Boolean {
        return runCatching {
            partialUpdateShow(update)
        }
            .isSuccess
    }

    override fun observeFavorites(query: String): Flow<List<TVShow>> {
        val q = query.takeIf { it.isNotBlank() }?.let { "%$query%" } ?: ""
        return handler.subscribeToList { showQueries.selectFavorites(q, ShowMapper.mapShow) }
    }

    private suspend fun partialUpdateShow(vararg updates: TVShowUpdate) {
        return handler.await {
            updates.forEach { update ->
                showQueries.update(
                    title = update.title,
                    posterUrl = update.posterUrl,
                    posterLastUpdated = update.posterLastUpdated,
                    favorite = update.favorite,
                    externalUrl = update.externalUrl,
                    showId = update.showId,
                    overview = update.overview,
                    genreIds = update.genreIds?.joinToString(separator = ","),
                    genres = update.genres?.joinToString(separator = ","),
                    originalLanguage = update.originalLanguage,
                    voteCount = update.voteCount?.toLong(),
                    releaseDate = update.releaseDate,
                    popularity = update.popularity,
                    status = update.status?.let { Status.entries.indexOf(it).toLong() },
                    productionCompanies = update.productionCompanies?.joinToString(separator = ",")
                )
            }
        }
    }
}