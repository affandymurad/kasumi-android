package am.jp.kasumi.repository.retrofit

import am.jp.kasumi.model.MovieResponse
import am.jp.kasumi.model.MoviesDetailResponse
import am.jp.kasumi.repository.Repository
import am.jp.kasumi.util.PaginationDidEnd
import am.jp.kasumi.util.UnexpectedResponseFromServer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

object RetrofitRepository : Repository {

    const val token = ""
    const val baseURL = "https://api.themoviedb.org/3/"
    const val baseImage = "http://image.tmdb.org/t/p/w500"

    fun getYoutubeUrl(ids: String): String {
        return "http://img.youtube.com/vi/${ids}/default.jpg"
    }

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
    }.build()

    private val instance: Specification = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()
        .create(Specification::class.java)


    private interface Specification {

        @GET("movie/now_playing")
        fun getMovieList(
            @Query("api_key") api: String,
            @QueryMap queries: Map<String, String?>
        ): Observable<MovieResponse>

        @GET("movie/{id}")
        fun getMovieDetail(
            @Path("id") id: String,
            @Query("api_key") api: String,
            @Query("append_to_response") appendToResponse: String
        ): Observable<MoviesDetailResponse>

    }

    override fun movie(
        token: String,
        movieFilter: Repository.MovieFilter
    ): Observable<MovieResponse> {
        val parameters = hashMapOf<String, String>()
        parameters["page"] = movieFilter.page.toString()
        parameters["region"] = movieFilter.region
        return instance.getMovieList(token, parameters)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                val pageCount = it.total_pages ?: throw UnexpectedResponseFromServer()
                if (pageCount < movieFilter.page) {
                    throw PaginationDidEnd()
                }
                it
            }
    }

    override fun movieDetail(
        token: String,
        id: Int,
        append_to_response: String
    ): Observable<MoviesDetailResponse> {
        return instance.getMovieDetail(id.toString(),token, append_to_response)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it }
    }

}