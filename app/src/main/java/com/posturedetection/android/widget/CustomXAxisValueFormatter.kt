package com.posturedetection.android.widget

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class CustomXAxisValueFormatter(private val labels: List<String>) : ValueFormatter() {


//    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
//        // Ensure that the value is a valid index for the labels list
//        val index = value.toInt()
//        return if (index >= 0 && index < labels.size) {
//            labels[index]
//        } else {
//            ""
//        }
//    }


    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return labels.getOrNull(value.toInt()) ?: value.toString()
    }
}
