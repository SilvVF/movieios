package io.silv.moviemp.core

interface STVShow  {

    var url: String

    var posterPath: String?

    var adult: Boolean

    var releaseDate: String

    var overview: String

    var genreIds: List<Int>?

    var genres: List<Pair<Int, String>>?

    var id: Long

    var originalLanguage: String

    var originalTitle: String

    var title: String

    var backdropPath: String?

    var popularity: Double

    var voteCount: Int

    var video: Boolean

    var voteAverage: Double

    var productionCompanies: List<String>?

    var status: Status?

    // TODO(add seasons)
    //var seasons: List

    fun copy() = create().also {
        it.url = url
        it.posterPath = posterPath
        it.title = title
        it.genreIds = genreIds
        it.adult = adult
        it.releaseDate = releaseDate
        it.overview = overview
        it.genreIds = genreIds
        it.genres = genres
        it.id = id
        it.originalTitle = originalTitle
        it.originalLanguage = originalLanguage
        it.title = title
        it.backdropPath = backdropPath
        it.popularity = popularity
        it.voteCount = voteCount
        it.video = video
        it.voteAverage = voteAverage
        it.productionCompanies = productionCompanies
        it.status = status
    }

    companion object {
        fun create(): STVShow {
            return STVShowImpl()
        }
    }
}

class STVShowImpl : STVShow {
    override var url: String = ""
    override var posterPath: String? = null
    override var adult: Boolean = false
    override var releaseDate: String = ""
    override var overview: String = ""
    override var genreIds: List<Int>? = null
    override var genres: List<Pair<Int, String>>? = null
    override var id: Long = 0
    override var originalLanguage: String = ""
    override var originalTitle: String = ""
    override var title: String = ""
    override var backdropPath: String? = null
    override var popularity: Double = 0.0
    override var voteCount: Int = 0
    override var video: Boolean = false
    override var voteAverage: Double = 0.0
    override var productionCompanies: List<String>? = null
    override var status: Status? = null
}