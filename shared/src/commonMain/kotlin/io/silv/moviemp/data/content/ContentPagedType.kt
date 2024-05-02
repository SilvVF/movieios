package io.silv.moviemp.data.content

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf


sealed interface ContentPagedType {

    data class Discover(
       val filters: Filters
    ): ContentPagedType

    sealed class Default(val name: String): ContentPagedType {
        data object Popular: Default("popular")
        data object TopRated: Default("top_rated")
        data object Upcoming: Default("upcoming")
    }

    data class Search(val query: String): ContentPagedType
}

data class Filters(
    val genres: List<Genre>,
    val genreMode: GenreMode,
    val sortingOption: SortingOption,
    val companies: MutableState<String>,
    val keywords: MutableState<String>,
    val people: MutableState<String>,
    val year: MutableState<String>,
    val voteCount: MutableState<String>,
    val voteAverage: MutableState<String>
) {
    companion object {
        val default = Filters(
            genres = listOf(),
            genreMode = GenreMode.And,
            sortingOption = SortingOption.PopularityDesc,
            companies = mutableStateOf(""),
            keywords = mutableStateOf(""),
            people = mutableStateOf(""),
            year = mutableStateOf(""),
            voteCount = mutableStateOf(""),
            voteAverage = mutableStateOf("")
        )
    }
}

enum class SortingOption(val title: String, val sort: String) {
    PopularityAsc("Popularity asc", "popularity.asc"),
    PopularityDesc("Popularity desc", "popularity.desc"),
    TitleAsc("Title asc", "title.asc"),
    TitleDesc("Title desc", "title.desc"),
    RevenueAsc("Revenue asc", "revenue.asc"),
    RevenueDesc("Revenue desc", "revenue.desc"),
    VoteCountAsc("Vote count asc", "vote_count.asc"),
    VoteCountDesc("Vote count desc", "vote_count.desc"),
    VoteAverageAsc("Vote average asc", "vote_average.asc"),
    VoteAverageDsc("Vote average desc", "vote_average.desc")
}

sealed interface GenreMode {
    data object Or: GenreMode
    data object And: GenreMode
}


data class SearchItem(
    val text: MutableState<String>,
    val label: String,
    val error: State<String?> = mutableStateOf(null),
    val placeHolder: String = ""
)


