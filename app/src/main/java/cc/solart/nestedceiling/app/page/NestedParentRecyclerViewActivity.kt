package cc.solart.nestedceiling.app.page

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cc.solart.nestedceiling.app.R
import cc.solart.nestedceiling.app.adapter.item.BannerItem
import cc.solart.nestedceiling.app.adapter.item.LastItem
import cc.solart.nestedceiling.app.adapter.item.NormalItem
import cc.solart.nestedceiling.app.model.BannerData
import cc.solart.nestedceiling.app.model.Last
import cc.solart.nestedceiling.widget.NestedParentRecyclerView
import cc.solart.nestedceiling.widget.OnChildAttachStateListener
import com.drakeet.multitype.MultiTypeAdapter
import java.util.*

class NestedParentRecyclerViewActivity : AppCompatActivity() {

    private val data: List<Any>
        get() {
            val data: MutableList<Any> = ArrayList()
            data.add(BannerData())
            data.add(R.drawable.drawable_icons)
            data.add(R.drawable.drawable_new)
            data.add(R.drawable.drawable_recommend)
            data.add(Last())
            return data
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nestedrecyclerview)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        val recyclerView = findViewById<NestedParentRecyclerView>(R.id.nested_rv)
        val topAnchor = findViewById<ImageView>(R.id.top_anchor)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MultiTypeAdapter(data)
        adapter.register(BannerItem())
        adapter.register(NormalItem())
        adapter.register(LastItem(recyclerView))
        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.postDelayed({ swipeRefreshLayout.isRefreshing = false }, 1000)
        }
        topAnchor.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        recyclerView.addOnChildAttachStateListener(object :OnChildAttachStateListener{
            override fun onChildDetachedFromTop() {
                topAnchor.visibility = View.GONE
            }

            override fun onChildAttachedToTop() {
                topAnchor.visibility = View.VISIBLE
            }

        })
    }
}