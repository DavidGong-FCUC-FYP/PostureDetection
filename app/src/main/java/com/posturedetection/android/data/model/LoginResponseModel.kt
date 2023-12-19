package com.posturedetection.android.data.model

data class LoginResponseModel(
    val status: Int,
    val message: String,
    val data: User
)
