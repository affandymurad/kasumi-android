package am.jp.kasumi.feature.detail

import am.jp.kasumi.model.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailViewModel : ViewModel(), DetailRepository.State {

    val error = MutableLiveData<String>()

    val movieDetails = MutableLiveData<MoviesDetailResponse>()

    override fun error(reason: String) {
        error.value = reason
    }

    override fun movieDetailFetched(movieDetail: MoviesDetailResponse) {
        movieDetails.value = movieDetail
    }



}