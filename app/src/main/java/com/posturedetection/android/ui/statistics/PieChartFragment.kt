package com.posturedetection.android.ui.statistics

import StatisticsDataUtils
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.posturedetection.android.databinding.FragmentPieChartBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PieChartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PieChartFragment : Fragment() {

    private var _binding: FragmentPieChartBinding? = null



    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPieChartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var counter = StatisticsDataUtils.getLastEntry(requireContext())
        val pieChart = binding.pieChart
        // Inflate the layout for this fragment

        val list:ArrayList<PieEntry> = ArrayList()

        if (counter != null) {
            list.add(PieEntry(counter.standardCounter,"Stander"))
            list.add(PieEntry(counter.crosslegCounter,"Cross leg"))
            list.add(PieEntry(counter.forwardheadCounter,"Forward Head"))
            list.add(PieEntry(counter.missingCounter,"Missing"))
        }else{
            list.add(PieEntry(4f,"Stander"))
            list.add(PieEntry(3f,"Cross leg"))
            list.add(PieEntry(2f,"Forward Head"))
            list.add(PieEntry(1f,"Missing"))
        }

        val pieDataSet= PieDataSet(list,"Sitting Posture Analysis")
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS,255)
        pieDataSet.valueTextColor= Color.BLACK
        pieDataSet.valueTextSize=15f

        val pieData= PieData(pieDataSet)

        pieChart.data= pieData

        pieChart.description.text= "Pie Chart"

        pieChart.centerText="Sitting Posture Analysis"

        pieChart.animateY(2000)

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PieChartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PieChartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}