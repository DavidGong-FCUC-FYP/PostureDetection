package com.posturedetection.android.ui.statistics

import StatisticsDataUtils
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.posturedetection.android.R
import com.posturedetection.android.databinding.FragmentStatisticsBinding
import reset

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statisticsViewModel =  ViewModelProvider(this).get(StatisticsViewModel::class.java)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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
        var tabIcon = arrayOf(R.drawable.ic_pie_chart,R.drawable.ic_bar_chart);
        var sittingAnalysis = getString(R.string.sitting_analysis)
        var historicalData = getString(R.string.historical_data)
        Log.d("TAG", "setupTabLayout: "+sittingAnalysis)
        Log.d("TAG", "setupTabLayout: "+historicalData)
        var tabTitleList = arrayOf(sittingAnalysis, historicalData);
        TabLayoutMediator(
            binding.mytablayout2, binding.myviepage2
        ) { tab, position ->
            tab.text = tabTitleList[position]
            tab.setIcon(tabIcon[position])
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