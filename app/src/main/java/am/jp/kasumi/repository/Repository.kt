package am.jp.kasumi.repository

import am.jp.kasumi.model.MovieResponse
import am.jp.kasumi.model.MoviesDetailResponse
import io.reactivex.Observable

interface Repository {

    fun movie(token: String, movieFilter: MovieFilter) : Observable<MovieResponse>

    fun movieDetail(token: String, id: Int, append_to_response: String) : Observable<MoviesDetailResponse>

    data class MovieFilter(
        val region: String,
        var page: Int = 1
    )
}