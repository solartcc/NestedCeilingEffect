package cc.solart.nestedceiling.app.page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import cc.solart.nestedceiling.app.R
import cc.solart.nestedceiling.app.adapter.ViewPagerAdapter
import cc.solart.nestedceiling.widget.NestedParentScrollView
import cc.solart.nestedceiling.widget.OnChildAttachStateListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import java.util.*

class NestedParentScrollViewActivity : AppCompatActivity() {

    private val pageFragments: List<Fragment>
        get() {
            val data: MutableList<Fragment> = ArrayList()
            data.add(LinearINormalFragment())
            data.add(LinearINormalFragment())
            data.add(LinearINormalFragment())
            return data
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nestedscrollview)
        val nestedParent = findViewById<NestedParentScrollView>(R.id.nested_parent)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val pagerAdapter = ViewPagerAdapter(this, pageFragments)
        viewPager.adapter = pagerAdapter
        val labels = arrayOf("精选", "直播", "新品推荐")
        TabLayoutMediator(tabLayout, viewPager, TabConfigurationStrategy { tab, position ->
            tab.text = labels[position]
        }).attach()
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.postDelayed({ swipeRefreshLayout.isRefreshing = false }, 1000)
        }
        nestedParent.addOnChildAttachStateListener(object :OnChildAttachStateListener{
            override fun onChildDetachedFromTop() {
                pagerAdapter.notifyChildToTop()
            }

            override fun onChildAttachedToTop() {
            }

        })
    }
}