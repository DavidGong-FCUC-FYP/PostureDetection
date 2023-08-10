package com.posturedetection.android.ui.statistics

import StatisticsDataUtils
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.posturedetection.android.R
import com.posturedetection.android.databinding.FragmentBarChartBinding
import com.posturedetection.android.widget.CustomXAxisValueFormatter
import toDataArray


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BarChartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BarChartFragment : Fragment() {


    private var _binding: FragmentBarChartBinding? = null
    val binding get() = _binding!!

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

        _binding = FragmentBarChartBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var counterList = StatisticsDataUtils.getLast30Entries(requireContext())

        val barChart = binding.barChart
        val list: ArrayList<BarEntry> = ArrayList()

        //for counterList then add to list
        for (i in 0 until counterList.size) {
            list.add(BarEntry(i.toFloat(), counterList[i].toDataArray()))
        }

        val barDataSet= BarDataSet(list, "Data Set")

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS,255)
        barDataSet.valueTextColor= Color.BLACK
        barDataSet.valueTextSize= 12f
        val barData= BarData(barDataSet)


        barChart.setFitBars(true)
        barChart.description.text= ""
        barData.barWidth= 0.7f
        barChart.isDragEnabled = true
        barChart.data= barData
        barChart.setVisibleXRangeMaximum(6f)
        barChart.setScaleEnabled(false);
       // barChart.setExtraLeftOffset(10f);
        barChart.setExtraTopOffset(5f);
        barChart.setExtraBottomOffset(60f);

        barChart.animateY(1000)

        val yAxisLeft = barChart.axisLeft
        yAxisLeft.axisMinimum = 0f // Set the minimum value for the Y-axis
        val yAxisRight = barChart.axisRight
        yAxisRight.axisMinimum = 0f // Set the minimum value for the Y-axis

        var xAxis = barChart.xAxis
        xAxis.setDrawGridLines(false);
        xAxis.textSize = 11f
        xAxis.labelCount = list.size
        xAxis.granularity = 1f
       // val xLabels = listOf("Label1", "Label2", "Label3") // Replace with your labels

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = 60f

        val xLabels = generateDateLabels(list.size)
        barChart.xAxis.valueFormatter = CustomXAxisValueFormatter(xLabels)

        val m = Matrix()
        m.postScale(scaleNum(list.size), 1f);
        barChart.getViewPortHandler().refresh(m, barChart, false);
        // Inflate the layout for this fragment
        return root
    }

    private val scalePercent = 4f / 30f

    private fun scaleNum(xCount: Int): Float {
        return xCount * scalePercent
    }


    fun generateDateLabels(numDays: Int): List<String> {
        val labels = mutableListOf<String>()
        for (i in 0 until numDays) {
            if (i == 0) {
                labels.add(getString(R.string.today))
            } else if (i == 1) {
                labels.add(getString(R.string.yesterday))
            } else {
                labels.add( i.toString() +" "+ getString(R.string.day))
            }
        }

        labels.reverse()
        return labels
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BarChartFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BarChartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}