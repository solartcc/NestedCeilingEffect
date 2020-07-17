package cc.solart.nestedceiling.app.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cc.solart.nestedceiling.app.R
import com.drakeet.multitype.ItemViewBinder
import kotlin.text.StringBuilder


class StaggerItem : ItemViewBinder<Int, StaggerItem.ViewHolder>() {

    private val items = arrayOf(
            R.drawable.drawable_item_1,
            R.drawable.drawable_item_2,
            R.drawable.drawable_item_3,
            R.drawable.drawable_item_4
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_pic)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Int) {
        holder.image.setImageResource(items[item % 4])
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_stagger, parent, false))
    }
}

