package am.jp.kasumi.feature.movie

import am.jp.kasumi.model.MovieResponse
import am.jp.kasumi.repository.Repository
import am.jp.kasumi.repository.RepositoryInstance
import am.jp.kasumi.repository.retrofit.RetrofitRepository.token
import am.jp.kasumi.util.PaginationDidEnd
import io.reactivex.disposables.Disposable

class MovieRepository(private val state: State) {

    private lateinit var movieFilter: Repository.MovieFilter

    private var onGoingRequest: Disposable ?= null

    fun setup(region: String) {
        this.movieFilter = Repository.MovieFilter(region)
    }

    fun fetch(page: Int) {
        this.movieFilter.page = page
        if (state.paginationWasEnd()) {
            return
        }
        onGoingRequest?.dispose()
        onGoingRequest = RepositoryInstance.default.movie(token, movieFilter).subscribe({state.movieFetched(it)},{
            if (it is PaginationDidEnd) {
                state.paginationHasEnded()
                return@subscribe
            }
            state.error(it.localizedMessage ?: "Unknown")})

    }

    fun ongoingRequestHasFinished(): Boolean {
        return onGoingRequest?.isDisposed ?: true
    }

    fun reload() {
        state.paginationHasReloaded()
    }


    interface State {

        fun paginationHasEnded()

        fun paginationHasReloaded()

        fun paginationWasEnd(): Boolean

        fun error(reason: String)

        fun movieFetched(movie: MovieResponse)
    }
}


