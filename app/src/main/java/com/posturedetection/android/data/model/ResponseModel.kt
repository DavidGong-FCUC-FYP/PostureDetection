package com.posturedetection.android.data.model

import java.util.Objects

data class ResponseModel(
    val status: Int,
    val message: String,
    val data: Object
)