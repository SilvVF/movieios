package io.silv.moviemp.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import app.cash.paging.filter
import app.cash.paging.map
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.silv.moviemp.data.content.ContentPagedType
import io.silv.moviemp.data.content.Filters
import io.silv.moviemp.data.content.Genre
import io.silv.moviemp.data.content.SearchItem
import io.silv.moviemp.data.content.lists.ContentItem
import io.silv.moviemp.data.content.movie.interactor.GetMovie
import io.silv.moviemp.data.content.movie.interactor.GetRemoteMovie
import io.silv.moviemp.data.content.movie.interactor.NetworkToLocalMovie
import io.silv.moviemp.data.content.movie.model.MoviePoster
import io.silv.moviemp.data.content.movie.model.toDomain
import io.silv.moviemp.data.content.movie.repository.SourceMovieRepository
import io.silv.moviemp.data.content.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MovieScreenModel(
    private val getRemoteMovie: GetRemoteMovie,
    private val networkToLocalMovie: NetworkToLocalMovie,
    private val getMovie: GetMovie,
    private val sourceRepository: SourceMovieRepository,
    savedStateContentPagedType: ContentPagedType
): ViewModel() {

    private val screenModelScope = viewModelScope

    private val mutableState = MutableStateFlow(MovieState(savedStateContentPagedType))
    val state = mutableState.asStateFlow()

    var query by mutableStateOf("")
        private set


    init {
        screenModelScope.launch {
            val genres = sourceRepository.getSourceGenres().map { it.toDomain() }
            mutableState.update {state -> state.copy(genres = genres) }
        }

        snapshotFlow { query }
            .debounce(1000)
            .onEach {
                if (it.isNotBlank()) {
                    mutableState.update {state ->
                        state.copy(listing = ContentPagedType.Search(it))
                    }
                }
            }
            .launchIn(screenModelScope)
    }

    val moviePagerFlowFlow = state.map { it.listing }
        .distinctUntilChanged()
        .flatMapLatest { listing ->
            Pager(
                PagingConfig(pageSize = 25)
            ) {
                getRemoteMovie.subscribe(listing)
            }.flow.map { pagingData ->
                val seenIds = mutableSetOf<Long>()
                pagingData.map { sMovie ->
                    networkToLocalMovie.await(sMovie.toDomain())
                        .let { localMovie -> getMovie.subscribePartial(localMovie.id) }
                        .stateIn(screenModelScope)
                }
                    .filter { seenIds.add(it.value.id) && it.value.posterUrl.isNullOrBlank().not() }
            }
                .cachedIn(screenModelScope)
        }
        .stateIn(screenModelScope, SharingStarted.Lazily, PagingData.empty())

    fun changeCategory(category: ContentPagedType) {
        screenModelScope.launch {
            if(category is ContentPagedType.Search && category.query.isBlank()) {
                return@launch
            }
            mutableState.update { state -> state.copy(listing = category) }
        }
    }

    fun changeQuery(query: String) {
        this.query = query
    }

    fun changeDialog(dialog: Dialog?) {
        screenModelScope.launch {
            mutableState.update { state ->
                state.copy(dialog = dialog)
            }
        }
    }

    fun resetFilters() {
        screenModelScope.launch {
            mutableState.update { state -> state.copy(filters = Filters.default) }
        }
    }

    fun onSearch(query: String) {
        screenModelScope.launch {
            if (query.isBlank()) {
                return@launch
            }
            mutableState.update { state -> state.copy(listing = ContentPagedType.Search(query)) }
        }
    }

    fun changeFilters(update: (Filters) -> Filters) {
        screenModelScope.launch {
            mutableState.update { state ->
                state.copy(filters = update(state.filters))
            }
        }
    }

    val searchItems = listOf(
        SearchItem(
            label = "Companies",
            text = state.value.filters.companies
        ),
        SearchItem(
            label = "Keywords",
            text = state.value.filters.keywords
        ),
        SearchItem(
            label = "People",
            text = state.value.filters.people,
        ),
        SearchItem(
            label = "Year",
            text = state.value.filters.year,
            error = derivedStateOf {
                state.value.filters.year.value
                    .takeIf { s ->
                        ((s.toIntOrNull() ?: -1) > Clock.System.now().toLocalDateTime(TimeZone.UTC).year || (s.toIntOrNull() ?: -1) < 1900 || s.any { !it.isDigit() })
                                && s.isNotBlank()
                    }
                    ?.let { "Year must be between 1900 and the current year" }
            },
        ),
        SearchItem(
            label = "Vote count",
            text = state.value.filters.voteCount,
            error = derivedStateOf {
                state.value.filters.voteCount.value
                    .takeIf { s ->
                        ((s.toFloatOrNull() ?: -1f) < 0f || s.any { !it.isDigit() && it != '.' })
                                && s.isNotBlank()
                    }
                    ?.let { "Vote count must be a number >= 0" }
            },
        ),
        SearchItem(
            label = "Vote average",
            text = state.value.filters.voteAverage,
            error = derivedStateOf {
                state.value.filters.voteAverage.value
                    .takeIf { s ->
                        ((s.toFloatOrNull() ?: -1f) < 0f || s.any { !it.isDigit() && it != '.' })
                                && s.isNotBlank()
                    }
                    ?.let { "Vote average must be a number >= 0" }
            },
        ),
    )


    @Stable
    sealed interface Dialog {

        @Stable
        data class ContentOptions(val item: ContentItem): Dialog

        @Stable
        data object Filter : Dialog

        @Stable
        data class RemoveMovie(val movie: MoviePoster) : Dialog
    }

    companion object  {
        fun create(): MovieScreenModel {
            val koin = object : KoinComponent {}
            return with(koin) {
                MovieScreenModel(get(), get(), get(), get(), ContentPagedType.Default.Popular)
            }
        }
    }
}

@Immutable
@Stable
data class MovieState(
    val listing: ContentPagedType = ContentPagedType.Default.Popular,
    val dialog: MovieScreenModel.Dialog? = null,
    val genres: List<Genre> = emptyList(),
    val filters: Filters = Filters.default
) {
    val genreItems =
        genres
            .map { Pair(filters.genres.contains(it), it) }

}