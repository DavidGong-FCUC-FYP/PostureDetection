package com.posturedetection.android.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.posturedetection.android.LineChartFragment
import com.posturedetection.android.PieChartFragment
import com.posturedetection.android.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null


    private var viewPager2: ViewPager2? = null
    private var tabLayout: TabLayout? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val statisticsViewModel =
            ViewModelProvider(this).get(StatisticsViewModel::class.java)

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        val root: View = binding.root
        setupViewPager()
        setupTabLayout()





//        val textView: TextView = binding.textDashboard
//        statisticsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTabLayout() {
        var tabTitleList = arrayOf("Line Chart","Pie Chart");
        TabLayoutMediator(
            binding.mytablayout2, binding.myviepage2
        ) { tab, position -> tab.text = tabTitleList[position]
        }.attach()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        binding.myviepage2.adapter = adapter
    }






    class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> LineChartFragment()
                1 -> PieChartFragment()
                else -> LineChartFragment()
            }
        }
    }
}