package com.posturedetection.android

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.LocaleListCompat
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.gson.Gson
import com.posturedetection.android.data.LoginUser
import com.posturedetection.android.data.model.AccountSettings
import com.posturedetection.android.databinding.ActivityAccountSettingsBinding
import com.posturedetection.android.util.ToastUtils
import com.posturedetection.android.widget.TitleLayout
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout
import java.util.Locale

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding

    private lateinit var titleLayout: TitleLayout

    private lateinit var mbtgAIDevice: MaterialButtonToggleGroup
    private lateinit var mbtgThemeAppearance: MaterialButtonToggleGroup
    private lateinit var mbtgCamera: MaterialButtonToggleGroup
    private lateinit var mbtgLanguage: MaterialButtonToggleGroup
    private lateinit var ssPomoTimer: SwitchCompat



    //AccountSettings
    private var accountSettings: AccountSettings = AccountSettings(0, 0, 0, 0, false)

    //get LoginUser
    private var loginUser: LoginUser? = LoginUser.getInstance()

    //SharedPreferences
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        titleLayout = binding.tlTitle
        var gson = Gson()
        titleLayout.setTextView_title("Account Settings")

        mbtgAIDevice = binding.mbtgAiDevice
        mbtgThemeAppearance = binding.mbtgThemeAppearance
        mbtgCamera = binding.mbtgCamera
        mbtgLanguage = binding.mbtgLanguage
        ssPomoTimer = binding.ssPomoTimer


        sp = getSharedPreferences("account_settings", MODE_PRIVATE)
        var account_settings_json = sp.getString("account_settings", null)
        if (account_settings_json != null){
            //from sp to get AccountSettings
            accountSettings = gson.fromJson(account_settings_json, AccountSettings::class.java)
            if (accountSettings != null) {
                mbtgAIDevice.check(mbtgAIDevice.getChildAt(accountSettings.aiDevice).id)
                mbtgThemeAppearance.check(mbtgThemeAppearance.getChildAt(accountSettings.themeAppearance).id)
                mbtgCamera.check(mbtgCamera.getChildAt(accountSettings.camera).id)
                mbtgLanguage.check(mbtgLanguage.getChildAt(accountSettings.language).id)
                ssPomoTimer.isChecked = accountSettings.pomoTimer
            }
        }


        mbtgAIDevice.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                accountSettings.aiDevice = group.indexOfChild(group.findViewById(checkedId))
            }
        }

        mbtgThemeAppearance.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                accountSettings.themeAppearance = group.indexOfChild(group.findViewById(checkedId))
                when (accountSettings.themeAppearance) {
                    0 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }
        }

        mbtgCamera.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                accountSettings.camera = group.indexOfChild(group.findViewById(checkedId))
            }
        }


        mbtgLanguage.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                accountSettings.language = group.indexOfChild(group.findViewById(checkedId))
                var language = "en"
                when(accountSettings.language){
                    0 -> {
                        language = "en"
                    }
                    1 -> {
                        language = "zh"
                    }
                    2 -> {
                        language = "ms-rMY"
                    }
                }
                val locales = LocaleListCompat.forLanguageTags(language)
                AppCompatDelegate.setApplicationLocales(locales)
            }
        }

        ssPomoTimer.setOnCheckedChangeListener { buttonView, isChecked ->
            accountSettings.pomoTimer = isChecked
        }

        titleLayout.iv_save.setOnClickListener {
            sp.edit().putString("account_settings", gson.toJson(accountSettings)).apply()
            //show Toast
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
            finish()
        }
    }




}