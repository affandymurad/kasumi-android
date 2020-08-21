package am.jp.kasumi.feature.detail

import am.jp.kasumi.R
import am.jp.kasumi.base.BaseActivity
import am.jp.kasumi.base.CustomLinearLayoutManager
import am.jp.kasumi.model.Country
import am.jp.kasumi.model.Genre
import am.jp.kasumi.model.MoviesDetailResponse
import am.jp.kasumi.repository.retrofit.RetrofitRepository.baseImage
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*

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
        setupRefreshLayout()

        observe(viewModel.error, this::whenErrorChanged)
        observe(viewModel.movieDetails, this::whenMovieListChanged)
    }

    override fun onResume() {
        super.onResume()
        srlDetail.isRefreshing = true
        repository.fetch()
    }

    private fun setupToolbar(){
        val ab = supportActionBar
        ab?.title = titles
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


    private fun setupRefreshLayout() {
        srlDetail.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorOrange))
        srlDetail.setOnRefreshListener {
            repository.fetch()
        }
    }


    private fun whenErrorChanged(reason: String) {
        srlDetail.isRefreshing = false
        Snackbar.make(findViewById(android.R.id.content), reason, Snackbar.LENGTH_SHORT).show()
    }

    private fun whenMovieListChanged(movieDetail: MoviesDetailResponse) {
        srlDetail.isRefreshing = false
        Glide.with(this).load(baseImage + movieDetail.posterPath).apply(RequestOptions().centerCrop().error(R.drawable.ic_placeholder).placeholder(R.drawable.ic_placeholder)).into(ivDetailPoster)

        tvDetailTitle.text = movieDetail.title
        tvDetailOriginalTitle.text = movieDetail.originalTitle
        tvDetailCountry.text = setAllFlagEmoticonCountries(movieDetail.productionCountries ?: arrayListOf())

        val loc = Locale(movieDetail.originalLanguage)
        val lang = loc.displayLanguage
        tvDetailOriginalLanguage.text = lang
        tvDetailNetworkDuration.text =  getString(R.string.menit, movieDetail.runtime ?: 0)
        tvDetailMovieGenre.text = setAllGenres(movieDetail.genres ?: arrayListOf())
        tvDetailMovieSinopsis.text = movieDetail.overview ?: "-"
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

    private fun setAllFlagEmoticonCountries(countries: List<Country>): String {
        val country = StringBuilder()
        if (countries.isNotEmpty()) {
            for (coun in countries) {
                country.append(flagCountry(coun.iso3166_1)).append(", ")
            }
            country.deleteCharAt(country.lastIndexOf(","))
        } else {
            country.append(flagCountry("XX"))
        }
        return country.toString()
    }

    private fun flagCountry(code: String?): String {
        val flagOffset = 0x1F1E6
        val asciiOffset = 0x41

        val firstChar = Character.codePointAt(code as CharSequence, 0) - asciiOffset + flagOffset
        val secondChar = Character.codePointAt(code as CharSequence, 1) - asciiOffset + flagOffset

        return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar)) + " " + code
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

