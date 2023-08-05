package com.posturedetection.android.data.model

import org.litepal.crud.LitePalSupport

data class AccountSettings(
    var aiDevice: Int,//0CPU 1GPU 2NPU
    var camera: Int, //0Back 1Front

    var themeAppearance: Int,  //0Light 1Dark
    var language: Int,//0English 1Chinese 2Malay
    var pomoTimer: Boolean //0Off 1On
)