package cc.solart.nestedceiling.app.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cc.solart.nestedceiling.app.R
import cc.solart.nestedceiling.app.adapter.ViewPagerAdapter
import cc.solart.nestedceiling.app.model.Last
import cc.solart.nestedceiling.app.page.LinearINormalFragment
import cc.solart.nestedceiling.app.page.StaggeredFragment
import cc.solart.nestedceiling.widget.NestedParentRecyclerView
import cc.solart.nestedceiling.widget.OnChildAttachStateListener
import com.drakeet.multitype.ItemViewBinder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import java.util.*

class LastItem(private val recyclerView: NestedParentRecyclerView) : ItemViewBinder<Last, LastItem.ViewHolder>() {

    class ViewHolder(itemView: View, recyclerView: NestedParentRecyclerView) : RecyclerView.ViewHolder(itemView) {

        init {
            val viewPager: ViewPager2 = itemView.findViewById(R.id.view_pager)
            val tabLayout: TabLayout = itemView.findViewById(R.id.tab_layout)

            val pagerAdapter = ViewPagerAdapter(itemView.context as FragmentActivity, getPageFragments())
            viewPager.adapter = pagerAdapter
            val labels = arrayOf("精选", "直播", "新品推荐")
            TabLayoutMediator(tabLayout, viewPager, TabConfigurationStrategy { tab, position ->
                tab.text = labels[position]
            }).attach()
            recyclerView.addOnChildAttachStateListener(object : OnChildAttachStateListener {
                override fun onChildDetachedFromTop() {
                    pagerAdapter.notifyChildToTop()
                }

                override fun onChildAttachedToTop() {
                }

            })
        }

        private fun getPageFragments(): List<Fragment> {
            val data: MutableList<Fragment> = ArrayList()
            data.add(StaggeredFragment())
            data.add(StaggeredFragment())
            data.add(LinearINormalFragment())
            return data
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Last) {

    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_last, parent, false), recyclerView)
    }

}