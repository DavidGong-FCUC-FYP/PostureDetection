package com.posturedetection.android.network
import com.posturedetection.android.data.model.LoginRequestBody
import com.posturedetection.android.data.model.LoginResponseModel
import com.posturedetection.android.data.model.RegisterRequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {

    @POST("auth/user-login")
    fun loginUser(@Body requestBody: LoginRequestBody): Call<LoginResponseModel>

    @POST("user/googlelogin")
    fun googleLoginUser(@Body requestBody: LoginRequestBody): Call<LoginResponseModel>

    @POST("user/register")
    fun register(@Body requestBody: RegisterRequestBody): Call<LoginResponseModel>


}
