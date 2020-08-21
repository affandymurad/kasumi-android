package am.jp.kasumi.repository

import am.jp.kasumi.repository.retrofit.RetrofitRepository

object RepositoryInstance {
    val default: Repository = RetrofitRepository
}