package io.silv.moviemp.data.content

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


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
    val companies: MutableStateFlow<String>,
    val keywords: MutableStateFlow<String>,
    val people: MutableStateFlow<String>,
    val year: MutableStateFlow<String>,
    val voteCount: MutableStateFlow<String>,
    val voteAverage: MutableStateFlow<String>
) {
    companion object {
        val default = Filters(
            genres = listOf(),
            genreMode = GenreMode.And,
            sortingOption = SortingOption.PopularityDesc,
            companies = MutableStateFlow(""),
            keywords = MutableStateFlow(""),
            people = MutableStateFlow(""),
            year = MutableStateFlow(""),
            voteCount = MutableStateFlow(""),
            voteAverage = MutableStateFlow("")
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
    val text: MutableStateFlow<String>,
    val label: String,
    val error: StateFlow<String?> = MutableStateFlow(null),
    val placeHolder: String = ""
)


