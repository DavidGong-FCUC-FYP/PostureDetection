package com.posturedetection.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.posturedetection.android.data.Camera
import com.posturedetection.android.data.Device
import com.posturedetection.android.data.LoginUser
import com.posturedetection.android.data.model.AccountSettings
import com.posturedetection.android.data.model.User
import com.posturedetection.android.databinding.ActivityLoginBinding
import com.posturedetection.android.util.ActivityCollector
import com.posturedetection.android.util.ChangeLanguageUtil
import com.posturedetection.android.util.MD5
import com.posturedetection.android.util.PhotoUtils
import com.posturedetection.android.util.ToastUtils
import org.litepal.LitePal

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient
    private var loginUser: LoginUser = LoginUser.getInstance()
    private val gson = Gson()

    private val sp: SharedPreferences by lazy {
        getSharedPreferences("Login", Context.MODE_PRIVATE)
    }

    private lateinit var etAccountEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var ivEye: ImageView
    private lateinit var ivMoreAccount: ImageView
    private lateinit var cbRemember: CheckBox
    private var passwordVisible = false
    private val toastUtils = ToastUtils()
    private var user = User()

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCollector.addActivity(this)

        val locales = LocaleListCompat.forLanguageTags("en")
        AppCompatDelegate.setApplicationLocales(locales)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        etAccountEmail = binding.etAccountEmail
        etPassword = binding.etPassword
        login = binding.login
        register = binding.register
        ivEye = binding.ivEye
        ivMoreAccount = binding.ivMoreAccount
        cbRemember = binding.cbRemember

        login.setOnClickListener(this)
        register.setOnClickListener(this)
        ivEye.setOnClickListener(this)
        ivMoreAccount.setOnClickListener(this)

        val sp = getSharedPreferences("account_settings", MODE_PRIVATE)
        var account_settings_json = sp.getString("account_settings", null)
        ChangeLanguageUtil().changeLanguage(account_settings_json?:"")

//        if (account_settings_json != null){
//            var gson = Gson()
//            var accountSettings = gson.fromJson(account_settings_json, AccountSettings::class.java)
//            if (accountSettings != null){
//                var language = "en"
//                when(accountSettings.language){
//                    0 -> {
//                        language = "en"
//                    }
//                    1 -> {
//                        language = "zh"
//                    }
//                    2 -> {
//                        language = "ms"
//                    }
//                }
//                val locales = LocaleListCompat.forLanguageTags(language)
//                AppCompatDelegate.setApplicationLocales(locales)
//            }
//        }

        val account = sp.getString("account", "")
        if (account != "") {
            user = gson.fromJson(account, User::class.java)
            Log.d("MainActivity", "onCreate: $account")
            //check if user is already logged in
            if (user != null) {
                user = LitePal.where("email=?", user.email).findFirst(User::class.java)
                if (user != null) {
                    if (user.remember == 1) {
                        //登入并存入LoginUser
                        LoginUser.getInstance().login(user)
                        //启动主界面
                        val intent1 = Intent(this, HomeActivity::class.java)
                        startActivity(intent1)
                        toastUtils.showShort(this, R.string.welcome.toString())
                        finish()
                    }
                }
            }
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.gSignInBtn.setOnClickListener() {
            signInGoogle()
        }


    }


    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * update UI with the signed-in user's information
     * will check about user information and save user information to shared preference
     * @param account
     *
     */
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val email = account.email.toString()
                val jsonUser = sp.getString("account", "")
                //check if user is already logged in or it is not new user
                if (jsonUser != "") {
                    user = LitePal.where("email=?", email).findFirst(User::class.java)
                    if (user != null) {
                        LoginUser.getInstance().login(user)
                    }
                }else {
                    user = User()
                    user.email = email
                    user.name = account.displayName.toString()
                    if (cbRemember.isChecked) {
                        user.remember = 1
                    } else {
                        user.remember = 0
                    }
                    val editor: SharedPreferences.Editor = sp.edit()
                    val userJson = gson.toJson(user)
                    editor.putString("account", userJson)
                    editor.apply()

                    user.portrait = PhotoUtils().file2byte(this, "profile.png")
                    user.save()
                    loginUser.login(user)
                }
                val intent: Intent = Intent(this, HomeActivity::class.java)
                Log.d("ProfileFragment", "onCreateView: $account")
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onStart() {
        super.onStart()
        //从本地数据库判断是否记住密码
        val users = LitePal.findAll(User::class.java)
        for (u in users) {
            if (u.remember == 1) {
                etAccountEmail.setText(u.email)
                etPassword.setText("12345678") //因为限制密码不能是全数字，利用12345678为通用密码简化验证，但会降低安全性
                cbRemember.isChecked = true
                break
            }
        }
    }

    override fun onClick(v: View) {
        val email = etAccountEmail.text.toString()
        var password = etPassword.text.toString()
        when (v.id) {
            //注册按钮的逻辑
            R.id.register -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            //登录按钮的逻辑
            R.id.login -> {
                var loginFlag = false //是否登录成功的标志
                user = LitePal.where("email=?", email).findFirst(User::class.java)

                //根据user的remember状态，判断是否需要MD5加密
                password = if (password == "12345678") user.password else MD5.md5(password)
                //密码正确则登录成功
                if (user.checkPassword(password)) {
                    //更新remember状态
                    user.remember = if (cbRemember.isChecked) 1 else 0
                    user.update(user.id)
                    //用户登入，存入LoginUser
                    LoginUser.getInstance().login(user)
                    //启动主界面
                    val intent1 = Intent(this, HomeActivity::class.java)
                    startActivity(intent1)
                    loginFlag = true
                    toastUtils.showShort(this, R.string.login_success.toString())
                } else {
                    user.remember = 0
                }

                if (!loginFlag) {
                    toastUtils.showShort(this, R.string.login_fail.toString())
                }
            }
            //隐藏密码功能
            R.id.iv_eye -> {
                if (passwordVisible) { //如果可见，则转为不可见
                    ivEye.isSelected = false
                    etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    passwordVisible = false
                } else { //如果不可见，则转为可见
                    ivEye.isSelected = true
                    etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    passwordVisible = true
                }
            }
            //显示本地所有登录信息
            R.id.iv_more_account -> {
                val users1 = LitePal.findAll(User::class.java)
                for (u in users1) Log.d("PD_User", "" + u.toString())
            }
        }
    }

}