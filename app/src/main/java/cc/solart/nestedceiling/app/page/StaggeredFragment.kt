package cc.solart.nestedceiling.app.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cc.solart.nestedceiling.app.R
import cc.solart.nestedceiling.app.adapter.item.NormalItem
import cc.solart.nestedceiling.app.adapter.item.StaggerItem
import com.drakeet.multitype.MultiTypeAdapter
import java.util.*


class StaggeredFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerView?.layoutManager = layoutManager
        val adapter = MultiTypeAdapter(increase())
        adapter.register(StaggerItem())
        recyclerView?.adapter = adapter
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val THRESHOLD_LOAD_MORE = 3
            private var hasLoadMore = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hasLoadMore = false
                }
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING && !hasLoadMore) {
                    val lastArray = (recyclerView.layoutManager as StaggeredGridLayoutManager?)!!.findLastVisibleItemPositions(IntArray(2))
                    val lastPosition = findMax(lastArray)
                    val offset = recyclerView.adapter!!.itemCount - lastPosition - 1
                    if (offset <= THRESHOLD_LOAD_MORE) {
                        hasLoadMore = true
                        adapter.items = increase()
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            private fun findMax(lastPositions: IntArray): Int {
                var max = lastPositions[0]
                for (value in lastPositions) {
                    if (value > max) {
                        max = value
                    }
                }
                return max
            }
        })
        return view
    }

    private var i = 0
    private val data: MutableList<Int> = ArrayList()

    private fun increase(): List<Int> {
        val tempI = i
        while (i < tempI + 10) {
            data.add(i++ % 4)
        }
        return data
    }

    fun resetToTop() {
        recyclerView?.let {
            val mLayoutManager = it.layoutManager as StaggeredGridLayoutManager
            mLayoutManager.scrollToPositionWithOffset(0, 0)
        }
    }
}