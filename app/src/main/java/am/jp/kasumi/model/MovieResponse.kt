package am.jp.kasumi.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieResponse(
    val total_results: Int?,
    val total_pages: Int?,
    val results: List<Movie>) : Serializable {}

data class Movie(
    @SerializedName("vote_count")
    var voteCount: Int? = null,

    @SerializedName("id")
    var id: Int ?= null,

    @SerializedName("video")
    var video: Boolean ?= null,

    @SerializedName("vote_average")
    var voteAverage: Float ?= null,

    @SerializedName("title")
    var title: String ?= null,

    @SerializedName("popularity")
    var popularity: Float ?= null,

    @SerializedName("poster_path")
    var posterPath: String ?= null,

    @SerializedName("original_language")
    var originalLanguage: String ?= null,

    @SerializedName("original_title")
    var originalTitle: String ?= null,

    @SerializedName("genre_ids")
    var genreIds: Array<Int> ?= null,

    @SerializedName("backdrop_path")
    var backdropPath: String ?= null,

    @SerializedName("adult")
    var adult: Boolean ?= null,

    @SerializedName("overview")
    var overview: String?= null,

    @SerializedName("release_date")
    var releaseDate: String ?= null) : Serializable