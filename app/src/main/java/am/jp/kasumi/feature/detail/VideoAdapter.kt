package am.jp.kasumi.feature.detail

import am.jp.kasumi.R
import am.jp.kasumi.base.RecyclerViewAdapter
import am.jp.kasumi.model.Videos
import am.jp.kasumi.repository.retrofit.RetrofitRepository.getYoutubeUrl
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.list_item_video.view.*
import java.util.*


class VideoAdapter : RecyclerViewAdapter<Videos>() {
    override fun getItemLayout(): Int {
        return R.layout.list_item_video
    }

    var summary = ""

    override fun onBindItem(itemView: View, position: Int) {
        val item = items[position]
        val loc = Locale(item.iso639_1 ?: "xx")
        val lang = loc.displayLanguage

        itemView.tvVideoTitle.text = item.name
        itemView.tvVideoType.text = item.type
        itemView.tvVideoSite.text = item.site
        itemView.tvVideoOriLanguage.text = lang
        itemView.cvVideo.setOnClickListener {
            itemView.context.startActivity(
                YoutubeActivity.start(itemView.context, item, summary)
            )
        }

        Glide.with(itemView.context).load(getYoutubeUrl(item.key!!)).apply(
            RequestOptions().centerCrop().error(R.drawable.ic_placeholder).placeholder(R.drawable.ic_placeholder)).into(itemView.video_thumbnail_image_view)
    }

    override fun onBindFooter(itemView: View) {

    }

    override fun areItemsTheSame(oldItem: Videos, newItem: Videos): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Videos, newItem: Videos): Boolean {
        return oldItem == newItem
    }

}