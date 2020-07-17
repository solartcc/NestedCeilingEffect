package cc.solart.nestedceiling.app.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cc.solart.nestedceiling.app.R
import com.drakeet.multitype.ItemViewBinder

class NormalItem : ItemViewBinder<Int, NormalItem.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.item_pic)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Int) {
        holder.image.setImageResource(item)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_normal, parent, false))
    }

}

