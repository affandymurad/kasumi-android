package am.jp.kasumi.feature.movie

import am.jp.kasumi.R
import am.jp.kasumi.base.BaseActivity
import am.jp.kasumi.base.CustomLinearLayoutManager
import am.jp.kasumi.model.Movie
import am.jp.kasumi.util.RecyclerViewLoadMoreListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MovieActivity : BaseActivity() {

//    private lateinit var viewModel: MovieViewModel
    private var page = 1

    private var isFinished = false

    private val viewModel: MovieViewModel by viewModels()

    private lateinit var repository: MovieRepository

    private val adapter = MovieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPresenter()
        setupRecyclerView()
        setupRefreshLayout()

        observe(viewModel.error, this::whenErrorChanged)
        observe(viewModel.movieList, this::whenMovieListChanged)
        observe(viewModel.paginationHasEnded, this::whenPaginationHasEndedChanged)
    }

    private fun setupPresenter() {
        repository = MovieRepository(viewModel)
        repository.setup("us") //Country code
    }

    private fun setupRecyclerView() {
        rvMovie.layoutManager = CustomLinearLayoutManager(this)
        rvMovie.addOnScrollListener(RecyclerViewLoadMoreListener {
            if (repository.ongoingRequestHasFinished() && !isFinished) {
                repository.fetch(page)
                page += 1
            }
        })

        rvMovie.adapter = adapter
        adapter.footerLayout = R.layout.progress
    }

    private fun setupRefreshLayout() {
        srlMovie.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorOrange))
        srlMovie.setOnRefreshListener {
            if (repository.ongoingRequestHasFinished() && !isFinished) {
                page = 1
                repository.fetch(page)
                page += 1
            } else {
                srlMovie.isRefreshing = false
            }
        }
    }


    private fun whenErrorChanged(reason: String) {
        srlMovie.isRefreshing = false
        adapter.footerLayout = if (adapter.items.count() == 0) R.layout.empty else R.layout.nothing
        Snackbar.make(findViewById(android.R.id.content), reason, Snackbar.LENGTH_SHORT).show()
    }

    private fun whenMovieListChanged(movieList: List<Movie>) {
        srlMovie.isRefreshing = false
        adapter.items = movieList
        adapter.footerLayout = if (adapter.items.count() == 0) R.layout.empty else R.layout.nothing
        if (adapter.items.count() <= 20) {
            rvMovie.scrollToPosition(0)
        }
    }

    private fun whenPaginationHasEndedChanged(paginationHasEnded: Boolean) {
        if (paginationHasEnded) {
            isFinished = true
            adapter.footerLayout = if (adapter.items.count() == 0) R.layout.empty else R.layout.nothing
        } else {
            adapter.footerLayout = R.layout.progress
        }
    }

}

