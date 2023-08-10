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
import com.posturedetection.android.data.model.AccountSettings
import com.posturedetection.android.databinding.ActivityAccountSettingsBinding
import com.posturedetection.android.util.ActivityCollector
import com.posturedetection.android.util.AccountSettingsUtil
import com.posturedetection.android.widget.TitleLayout

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding

    private lateinit var titleLayout: TitleLayout

    private lateinit var mbtgAIDevice: MaterialButtonToggleGroup
    private lateinit var mbtgThemeAppearance: MaterialButtonToggleGroup
    private lateinit var mbtgCamera: MaterialButtonToggleGroup
    private lateinit var mbtgLanguage: MaterialButtonToggleGroup
    private lateinit var ssPomoTimer: SwitchCompat
    private var language = "en"

    //PomoTimer
//    private lateinit var timer: CountDownTimer
//    private var isTimerRunning = false
//    private var timeLeftInMillis = 0L
//    //test try 30s
//    private var workingTime = 30000L // 25 minutes in milliseconds
//    //private var workingTime = 1500000L // 25 minutes in milliseconds
//    private var relaxationTime = 300000L // 5 minutes in milliseconds



    //AccountSettings
    private var accountSettings: AccountSettings = AccountSettings(0, 0, 0, 0, false)

    //SharedPreferences
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCollector.addActivity(this)

        titleLayout = binding.tlTitle
        var gson = Gson()
        titleLayout.setTextView_title("Account Settings")

        mbtgAIDevice = binding.mbtgAiDevice
        mbtgThemeAppearance = binding.mbtgThemeAppearance
        mbtgCamera = binding.mbtgCamera
        mbtgLanguage = binding.mbtgLanguage
        //ssPomoTimer = binding.ssPomoTimer


        sp = getSharedPreferences("account_settings", MODE_PRIVATE)
        var account_settings_json = sp.getString("account_settings", null)
      //  AccountSettingsUtil().init(account_settings_json?:"")

        if (account_settings_json != null){
            //from sp to get AccountSettings
            accountSettings = gson.fromJson(account_settings_json, AccountSettings::class.java)
            if (accountSettings != null) {
                mbtgAIDevice.check(mbtgAIDevice.getChildAt(accountSettings.aiDevice).id)
                mbtgThemeAppearance.check(mbtgThemeAppearance.getChildAt(accountSettings.themeAppearance).id)
                mbtgCamera.check(mbtgCamera.getChildAt(accountSettings.camera).id)
                mbtgLanguage.check(mbtgLanguage.getChildAt(accountSettings.language).id)
                //ssPomoTimer.isChecked = accountSettings.pomoTimer
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
            }
        }

//        ssPomoTimer.setOnCheckedChangeListener { buttonView, isChecked ->
//            accountSettings.pomoTimer = isChecked
//
//            if (isChecked) {
//                startTimer()
//            } else {
//                stopTimer()
//            }
//        }

        titleLayout.iv_save.setOnClickListener {
            AccountSettingsUtil.init(accountSettings)
            sp.edit().putString("account_settings", gson.toJson(accountSettings)).apply()
            //show Toast
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }


//    private fun startTimer() {
//        if (accountSettings.pomoTimer) {
//            // Start the working time (25 minutes)
//            timeLeftInMillis = workingTime
//            timer = createTimer(workingTime)
//        } else {
//            // Start the relaxation time (5 minutes)
//            timeLeftInMillis = relaxationTime
//            timer = createTimer(relaxationTime)
//        }
//
//        timer.start()
//        isTimerRunning = true
//    }
//
//    private fun stopTimer() {
//        timer.cancel()
//        isTimerRunning = false
//        timeLeftInMillis = 0
//        updateTimer()
//    }
//
//    private fun updateTimer() {
//        val minutes = (timeLeftInMillis / 1000) / 60
//        val seconds = (timeLeftInMillis / 1000) % 60
////        val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
////        tvTimer.text = timeLeftFormatted
//    }
//
//    private fun createTimer(timeInMillis: Long): CountDownTimer {
//        return object : CountDownTimer(timeInMillis, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                timeLeftInMillis = millisUntilFinished
//               // updateTimer()
//            }
//
//            override fun onFinish() {
//                isTimerRunning = false
//                //updateTimer()
//
//                // Show a notification
//                if (accountSettings.pomoTimer) {
//                    showNotification(R.string.working_time_is_over.toString())
//                } else {
//                    showNotification(R.string.break_time_is_over.toString())
//                }
//
//                // Play an alarm sound
//                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//                val ringtone = RingtoneManager.getRingtone(applicationContext, notification)
//                ringtone?.play()
//            }
//        }
//    }
//
//    private fun showNotification(message: String) {
//        val channelId = "PomodoroChannel"
//        val notificationId = 1 // Use a unique ID for each notification
//
//        // Create an intent to open the PomodoroActivity when the notification is clicked
////        val intent = Intent(this, HomeActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        // Create a notification
//        val builder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp)
//            .setContentTitle(R.string.pomodoro_alarm.toString())
//            .setContentText(message)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        // Show the notification
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // For Android 8.0 and above, create a notification channel
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId, R.string.pomodoro_alarm.toString(), NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(notificationId, builder.build())
//    }




}