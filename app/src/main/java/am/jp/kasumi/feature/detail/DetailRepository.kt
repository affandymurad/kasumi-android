package am.jp.kasumi.feature.detail

import am.jp.kasumi.model.MoviesDetailResponse
import am.jp.kasumi.repository.RepositoryInstance
import am.jp.kasumi.repository.retrofit.RetrofitRepository.token
import io.reactivex.disposables.Disposable

class DetailRepository(private val state: State) {


    private var id = 0
    private var onGoingRequest: Disposable ?= null
    private val append = "videos"

    fun setup(ids: Int) {
        this.id = ids
    }

    fun fetch() {
        onGoingRequest?.dispose()
        onGoingRequest = RepositoryInstance.default.movieDetail(token, id, append).subscribe({state.movieDetailFetched(it)},{state.error(it.localizedMessage ?: "Unknown")})

    }


    interface State {
        fun error(reason: String)

        fun movieDetailFetched(movieDetail: MoviesDetailResponse)
    }
}


