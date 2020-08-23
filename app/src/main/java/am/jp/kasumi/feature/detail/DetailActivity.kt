package am.jp.kasumi.feature.detail

import am.jp.kasumi.R
import am.jp.kasumi.base.BaseActivity
import am.jp.kasumi.base.CustomLinearLayoutManager
import am.jp.kasumi.model.Genre
import am.jp.kasumi.model.MoviesDetailResponse
import am.jp.kasumi.repository.retrofit.RetrofitRepository.baseImage
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : BaseActivity() {

//    private lateinit var viewModel: DetailViewModel

    private val viewModel: DetailViewModel by viewModels()

    private lateinit var repository: DetailRepository

    private val vAdapter = VideoAdapter()

    companion object {
        var titles = ""
        var id : Int = 0
        fun start(context: Context, ids: Int?, detailTitle: String?): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            id = ids ?: 0
            titles = detailTitle ?: ""
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setupToolbar()
        setupRepository(id)

        observe(viewModel.error, this::whenErrorChanged)
        observe(viewModel.movieDetails, this::whenMovieListChanged)
    }

    override fun onResume() {
        super.onResume()
        repository.fetch()
    }

    private fun setupToolbar(){
        val ab = supportActionBar
        ab?.title = "Details"
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRepository(ids: Int) {
        repository = DetailRepository(viewModel)
        repository.setup(ids)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    private fun whenErrorChanged(reason: String) {
        Snackbar.make(findViewById(android.R.id.content), reason, Snackbar.LENGTH_SHORT).show()
    }

    private fun whenMovieListChanged(movieDetail: MoviesDetailResponse) {
        Glide.with(this).load(baseImage + movieDetail.posterPath).apply(RequestOptions().centerCrop().error(R.drawable.ic_placeholder).placeholder(R.drawable.ic_placeholder)).into(ivDetailPoster)

        tvDetailTitle.text = movieDetail.title

        tvDetailNetworkDuration.text =  getString(R.string.menit, movieDetail.runtime ?: 0)
        tvDetailMovieGenre.text = setAllGenres(movieDetail.genres ?: arrayListOf())
        tvDetailOverview.text = movieDetail.overview ?: "-"
        setUpVideos(movieDetail)

        vAdapter.summary = movieDetail.overview ?: "-"
    }

    private fun setUpVideos(movieDetail: MoviesDetailResponse) {
        rvVideos.layoutManager = CustomLinearLayoutManager(this)
        rvVideos.adapter = vAdapter
        vAdapter.footerLayout = R.layout.nothing
        vAdapter.items = movieDetail.videos?.results ?: arrayListOf()
        if (vAdapter.items.count() == 0) tvEmptyVideo.visibility = View.VISIBLE else tvEmptyVideo.visibility = View.GONE
    }

    private fun setAllGenres(genres: List<Genre>): String {
        val genress = StringBuilder()
        if (genres.isNotEmpty()) {
            for (gen in genres) {
                genress.append(gen.name).append(", ")
            }
            genress.deleteCharAt(genress.lastIndexOf(","))
        } else {
            genress.append("-")
        }
        return genress.toString()
    }

}

