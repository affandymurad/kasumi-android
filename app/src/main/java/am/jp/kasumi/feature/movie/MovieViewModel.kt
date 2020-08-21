package am.jp.kasumi.feature.movie

import am.jp.kasumi.model.Movie
import am.jp.kasumi.model.MovieResponse
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MovieViewModel : ViewModel(), MovieRepository.State {


    val error = MutableLiveData<String>()

    val movieList = MutableLiveData<List<Movie>>()

    val paginationHasEnded = MutableLiveData<Boolean>()

    override fun error(reason: String) {
        error.value = reason
    }

    override fun movieFetched(movie: MovieResponse) {
        val items = ArrayList<Movie>()
        val existingItems = movieList.value ?: listOf()
        items.addAll(existingItems)
        items.addAll(movie.results)
        movieList.value = items
    }

    override fun paginationHasEnded() {
        paginationHasEnded.value = true
    }

    override fun paginationHasReloaded() {
        paginationHasEnded.value = false
    }

    override fun paginationWasEnd(): Boolean {
        return paginationHasEnded.value ?: false
    }
}