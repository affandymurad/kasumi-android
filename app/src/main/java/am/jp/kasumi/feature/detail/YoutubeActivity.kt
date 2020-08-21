package am.jp.kasumi.feature.detail

import am.jp.kasumi.R
import am.jp.kasumi.model.Videos
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.activity_video.*

class YoutubeActivity : AppCompatActivity() {

    private var ytPlayer: YouTubePlayer? = null
    private var isPlaying = false

    companion object {
        var video: Videos ?= null
        var summary = ""
        fun start(context: Context, videos: Videos, summaries: String): Intent {
            val intent = Intent(context, YoutubeActivity::class.java)
            video = videos
            summary = summaries
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        setupToolbar()

        val youTubePlayerView: YouTubePlayerView = findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView)


        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                ytPlayer = youTubePlayer
                youTubePlayer.loadVideo(video?.key!!, 0f)

                txtTitle.text = video!!.name
                txtDesc.text = summary

                btnPause.setOnClickListener {
                    if (ytPlayer != null) {
                        if (isPlaying) {
                            ytPlayer!!.pause()
                            btnPause.setImageResource(R.drawable.ic_play)
                        } else {
                            ytPlayer!!.play()
                            btnPause.setImageResource(R.drawable.ic_pause)
                        }
                        isPlaying = !isPlaying
                    }
                }
            }
        })

    }

    private fun setupToolbar(){
        val ab = supportActionBar
        ab?.title = DetailActivity.titles
        ab?.setDisplayHomeAsUpEnabled(true)
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

}

