package cc.solart.nestedceiling.app.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cc.solart.nestedceiling.app.page.LinearINormalFragment
import cc.solart.nestedceiling.app.page.StaggeredFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val data: List<Fragment>) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun notifyChildToTop() {
        for (i in data.indices){
            val fragment = data[i]
            if(fragment is LinearINormalFragment){
                fragment.resetToTop()
            } else if(fragment is StaggeredFragment) {
                fragment.resetToTop()
            }
        }
    }

}