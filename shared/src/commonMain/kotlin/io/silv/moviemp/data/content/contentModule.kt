package io.silv.moviemp.data.content

import io.silv.moviemp.data.content.credits.CreditRepository
import io.silv.moviemp.data.content.credits.CreditRepositoryImpl
import io.silv.moviemp.data.content.credits.GetMovieCredits
import io.silv.moviemp.data.content.credits.GetRemoteCredits
import io.silv.moviemp.data.content.credits.GetTVShowCredits
import io.silv.moviemp.data.content.credits.NetworkToLocalCredit
import io.silv.moviemp.data.content.lists.repository.ContentListRepository
import io.silv.moviemp.data.content.lists.repository.ContentListRepositoryImpl
import io.silv.moviemp.data.content.movie.interactor.GetMovie
import io.silv.moviemp.data.content.movie.interactor.GetRemoteMovie
import io.silv.moviemp.data.content.movie.interactor.NetworkToLocalMovie
import io.silv.moviemp.data.content.movie.interactor.UpdateMovie
import io.silv.moviemp.data.content.movie.repository.MovieRepository
import io.silv.moviemp.data.content.movie.repository.MovieRepositoryImpl
import io.silv.moviemp.data.content.movie.repository.SourceMovieRepository
import io.silv.moviemp.data.content.movie.repository.SourceMovieRepositoryImpl
import io.silv.moviemp.data.content.trailers.GetMovieTrailers
import io.silv.moviemp.data.content.trailers.GetRemoteTrailers
import io.silv.moviemp.data.content.trailers.GetTVShowTrailers
import io.silv.moviemp.data.content.trailers.NetworkToLocalTrailer
import io.silv.moviemp.data.content.trailers.TrailerRepository
import io.silv.moviemp.data.content.trailers.TrailerRepositoryImpl
import io.silv.moviemp.data.content.tv.interactor.GetRemoteTVShows
import io.silv.moviemp.data.content.tv.interactor.GetShow
import io.silv.moviemp.data.content.tv.interactor.NetworkToLocalTVShow
import io.silv.moviemp.data.content.tv.interactor.UpdateShow
import io.silv.moviemp.data.content.tv.repository.ShowRepository
import io.silv.moviemp.data.content.tv.repository.ShowRepositoryImpl
import io.silv.moviemp.data.content.tv.repository.SourceTVRepository
import io.silv.moviemp.data.content.tv.repository.SourceTVRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val contentModule = module {

    factoryOf(::GetRemoteTrailers)

    factoryOf(::NetworkToLocalTrailer)

    factoryOf(::NetworkToLocalCredit)

    singleOf(::CreditRepositoryImpl) { bind<CreditRepository>() }

    singleOf(::ContentListRepositoryImpl) { bind<ContentListRepository>() }

    singleOf(::TrailerRepositoryImpl) { bind<TrailerRepository>() }

    singleOf(::SourceMovieRepositoryImpl) { bind<SourceMovieRepository>() }

    singleOf(::SourceTVRepositoryImpl) { bind<SourceTVRepository>() }

    singleOf(::MovieRepositoryImpl) { bind<MovieRepository>() }

    singleOf(::ShowRepositoryImpl) { bind<ShowRepository>() }

    factoryOf(::GetTVShowTrailers)

    factoryOf(::GetTVShowCredits)

    factoryOf(::UpdateShow)

    factoryOf(::GetShow)

    factoryOf(::NetworkToLocalTVShow)

    factoryOf(::GetRemoteTVShows)

    factoryOf(::GetRemoteCredits)

    factoryOf(::UpdateMovie)

    factoryOf(::GetMovie)

    factoryOf(::GetRemoteMovie)

    factoryOf(::GetMovieCredits)

    factoryOf(::GetMovieTrailers)

    factoryOf(::NetworkToLocalMovie)
}
