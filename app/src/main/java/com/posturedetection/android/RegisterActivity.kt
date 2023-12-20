package com.posturedetection.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.posturedetection.android.data.LoginUser
import com.posturedetection.android.data.model.LoginRequestBody
import com.posturedetection.android.data.model.LoginResponseModel
import com.posturedetection.android.data.model.RegisterRequestBody
import com.posturedetection.android.data.model.User
import com.posturedetection.android.util.ActivityCollector
import com.posturedetection.android.util.MD5
import com.posturedetection.android.util.PhotoUtils
import org.litepal.LitePal
import org.litepal.tablemanager.Connector
import com.posturedetection.android.databinding.ActivityRegisterBinding
import com.posturedetection.android.network.RetrofitClient
import com.posturedetection.android.network.UserApiService
import com.posturedetection.android.util.ToastUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding
    private val apiService: UserApiService = RetrofitClient.instance

    private lateinit var etAccountEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var registerButton: Button
    private val toastUtils = ToastUtils()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCollector.addActivity(this)
//        val sp: SharedPreferences = getSharedPreferences("account_settings", MODE_PRIVATE)
//        var account_settings_json = sp.getString("account_settings", null)
//        ChangeLanguageUtil().changeLanguage(account_settings_json?:"")

        etAccountEmail =binding.etAccountEmail
        etPassword = binding.etPassword
        registerButton = binding.register
        Connector.getDatabase()

        // Register logic implementation
        registerButton.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.register -> {
                val email = etAccountEmail.text.toString()
                var password = etPassword.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    return
                }
                val name = email.split("@".toRegex()).toTypedArray()[0]
                register(email,password,name)

//                password = MD5.md5(password) // MD5 encryption
//                var user = LitePal.where("email==?", email).findFirst(User::class.java)
//                val mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT)

                // Check if the username already exists
//                if (user != null) {
//                    mToast.setText(R.string.user_already_exist)
//                    mToast.show()
//                } else {
//                    // If the username does not exist, create a new user
//                    user = User()
//                    user.email = email
//                    user.password = password
//                    val name = email.split("@".toRegex()).toTypedArray()[0]
//                    user.username = name
//
//                    // Default settings: not remember password and set default portrait
//                    user.imgUrl = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.profile)
//                    user.remember = 0
//                    user.save()
//
//                    register(email,password)
//
//                    mToast.setText(R.string.register_success)
//                    mToast.show()
//
//                    val intent = Intent(this, LoginActivity::class.java)
//                    startActivity(intent)
//                }
            }
        }
    }

    fun register(email:String,password:String,username:String=""){
        val loginRequestBody = RegisterRequestBody(username,email, password)
        val call: Call<LoginResponseModel> = apiService.register(loginRequestBody)
        call.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {
                Log.d("RegisterActivity", "onResponse: $response")
                if (response.isSuccessful) {
                    val result = response.body() // Your response model
                    Log.d("RegisterActivity", "onResponse: $result")
                    // Handle the response here
                    if (result != null && result.status == 200) {
                        // Login successful
                        LoginUser.getInstance().login(result.data)
                        // log result

                        toastUtils.showShort(this@RegisterActivity, "Register Success")
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Login failed, show an error message
                        toastUtils.showShort(this@RegisterActivity,"Register Fail")
                    }
                } else {
                    // Handle unsuccessful login response
                    toastUtils.showShort(this@RegisterActivity,"Register Fail")
                }
            }
            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                Log.d("LoginActivity", "onFailure: $t")
                // Handle network failure
                toastUtils.showShort(this@RegisterActivity, "Network Error")
            }
        })
    }


}
