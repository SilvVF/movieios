package io.silv.moviemp.database

import app.cash.sqldelight.ColumnAdapter
import io.silv.Database
import io.silv.moviemp.Movie_
import io.silv.moviemp.Show
import org.koin.dsl.module


private val listIntAdapter = object : ColumnAdapter<List<Int>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(",").mapNotNull { it.toIntOrNull() }
        }
    override fun encode(value: List<Int>) = value.joinToString(separator = ",")
}
private val listStringAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(",")
        }
    override fun encode(value: List<String>) = value.joinToString(separator = ",")
}

val databaseModule = module {

    single {
        Database(
            driver = get(),
            movie_Adapter = Movie_.Adapter(
                genreIdsAdapter = listIntAdapter,
                genresAdapter = listStringAdapter,
                production_companiesAdapter = listStringAdapter
            ),
            showAdapter = Show.Adapter(
                genre_idsAdapter = listIntAdapter,
                genresAdapter = listStringAdapter,
                production_companiesAdapter = listStringAdapter
            )
        )
    }

    single<DatabaseHandler> {
        DatabaseHandlerImpl(db = get(), driver = get())
    }
}