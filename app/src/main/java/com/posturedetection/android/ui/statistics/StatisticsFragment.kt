package com.posturedetection.android.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.posturedetection.android.BarChartFragment
import com.posturedetection.android.PieChartFragment
import com.posturedetection.android.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

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
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTabLayout() {
        var tabTitleList = arrayOf("Pie Chart","Bar Chart");
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
                0 -> PieChartFragment()
                1 -> BarChartFragment()
                else -> PieChartFragment()
            }
        }
    }
}