package cc.solart.nestedceiling.app.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.solart.nestedceiling.app.R
import cc.solart.nestedceiling.app.adapter.item.NormalItem
import com.drakeet.multitype.MultiTypeAdapter
import java.util.*


class LinearINormalFragment : Fragment() {
    var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        val adapter = MultiTypeAdapter(increase())
        adapter.register(NormalItem())
        recyclerView?.adapter = adapter
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val THRESHOLD_LOAD_MORE = 3
            private var hasLoadMore = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hasLoadMore = false
                }
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING && !hasLoadMore) {
                    val lastPosition = (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
                    val offset = recyclerView.adapter!!.itemCount - lastPosition - 1
                    if (offset <= THRESHOLD_LOAD_MORE) {
                        hasLoadMore = true
                        adapter.items = increase()
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
        return view
    }

    private var i = 0
    private val items = arrayOf(
            R.drawable.drawable_item_5,
            R.drawable.drawable_item_6
    )
    private val data: MutableList<Int> = ArrayList()

    private fun increase(): List<Int> {
        val tempI = i
        while (i < tempI + 10) {
            data.add(items[i++ % 2])
        }
        return data
    }

    fun resetToTop() {
        recyclerView?.let {
            val mLayoutManager = it.layoutManager as LinearLayoutManager
            mLayoutManager.scrollToPositionWithOffset(0, 0)
        }
    }
}