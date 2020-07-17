package cc.solart.nestedceiling.app.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cc.solart.nestedceiling.app.R
import cc.solart.nestedceiling.app.adapter.BannerImageAdapter
import cc.solart.nestedceiling.app.model.BannerData
import com.drakeet.multitype.ItemViewBinder
import com.youth.banner.Banner
import com.youth.banner.indicator.RectangleIndicator

class BannerItem : ItemViewBinder<BannerData, BannerItem.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val banner: Banner<String, BannerImageAdapter> = itemView.findViewById(R.id.banner)

        init {
            banner.let {
                it.indicator = RectangleIndicator(banner.context)
                it.setBannerRound(20f)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: BannerData) {
        val adapter = BannerImageAdapter(item.imageUrls)
        holder.banner.adapter = adapter
        holder.banner.start()
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_banner, parent, false))
    }
}