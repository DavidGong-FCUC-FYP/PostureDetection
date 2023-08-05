package com.posturedetection.android

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.gson.Gson
import com.posturedetection.android.data.LoginUser
import com.posturedetection.android.data.model.AccountSettings
import com.posturedetection.android.databinding.ActivityAccountSettingsBinding
import com.posturedetection.android.util.ToastUtils
import com.posturedetection.android.widget.TitleLayout
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding

    private lateinit var titleLayout: TitleLayout

    private lateinit var tbAIDevice: ToggleButtonLayout
    private lateinit var tbThemeAppearance: ToggleButtonLayout
    private lateinit var tbCamera: ToggleButtonLayout
    private lateinit var tbLanguage: ToggleButtonLayout
    private lateinit var ssPomoTimer: SwitchCompat

    //ToastUtils
    private lateinit var toastUtils: ToastUtils

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

        tbAIDevice = binding.tbAiDevice
        tbThemeAppearance = binding.tbThemeAppearance
        tbCamera = binding.tbCamera
        tbLanguage = binding.tbLanguage
        ssPomoTimer = binding.ssPomoTimer


        sp = getSharedPreferences("account_settings", MODE_PRIVATE)
        var account_settings_json = sp.getString("account_settings", null)
        if (account_settings_json != null){
            //from sp to get AccountSettings
            accountSettings = gson.fromJson(account_settings_json, AccountSettings::class.java)
            if (accountSettings != null) {
                tbAIDevice.toggles[accountSettings.aiDevice].isSelected = true
                tbThemeAppearance.toggles[accountSettings.themeAppearance].isSelected = true
                tbCamera.toggles[accountSettings.camera].isSelected = true
                tbLanguage.toggles[accountSettings.language].isSelected = true
                ssPomoTimer.isChecked = accountSettings.pomoTimer
            }
        }


        tbAIDevice.onToggledListener = { toggleButtonLayout, toggle, selected ->
            if (selected) {
                accountSettings.aiDevice = toggleButtonLayout.toggles.indexOf(toggle)
            }
        }

        tbThemeAppearance.onToggledListener = { toggleButtonLayout, toggle, selected ->
            if (selected) {
                accountSettings.themeAppearance = toggleButtonLayout.toggles.indexOf(toggle)
            }
        }

        tbCamera.onToggledListener = { toggleButtonLayout, toggle, selected ->
            if (selected) {
                accountSettings.camera = toggleButtonLayout.toggles.indexOf(toggle)
            }
        }

        tbLanguage.onToggledListener = { toggleButtonLayout, toggle, selected ->
            if (selected) {
                accountSettings.language = toggleButtonLayout.toggles.indexOf(toggle)
            }
        }

        ssPomoTimer.setOnCheckedChangeListener { buttonView, isChecked ->
            accountSettings.pomoTimer = isChecked
        }

        titleLayout.iv_save.setOnClickListener {
            sp.edit().putString("account_settings", gson.toJson(accountSettings)).apply()
            toastUtils.showShort(this, R.string.save_success.toString())
            finish()
        }
    }
}