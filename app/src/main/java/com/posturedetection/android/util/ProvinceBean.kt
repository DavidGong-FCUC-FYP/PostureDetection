package com.posturedetection.android.util

import com.contrarywind.interfaces.IPickerViewData

data class ProvinceBean(
    var id: Long,
    var name: String,
    var code: String,
    var province: String
) : IPickerViewData {
    //这个用来显示在PickerView上面的字符串,PickerView会通过getPickerViewText方法获取字符串显示出来。
    override fun getPickerViewText(): String {
        return name
    }
}
