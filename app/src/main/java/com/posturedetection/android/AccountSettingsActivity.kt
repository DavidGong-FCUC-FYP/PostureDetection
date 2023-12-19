package com.posturedetection.android

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.gson.Gson
import com.posturedetection.android.data.model.AccountSettings
import com.posturedetection.android.databinding.ActivityAccountSettingsBinding
import com.posturedetection.android.receiver.AlarmReceiver
import com.posturedetection.android.receiver.PomoTimerAlarmReceiver
import com.posturedetection.android.util.AccountSettingsUtil
import com.posturedetection.android.util.ActivityCollector
import com.posturedetection.android.widget.TitleLayout
import java.util.Calendar

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding

    private lateinit var titleLayout: TitleLayout

    private lateinit var mbtgAIDevice: MaterialButtonToggleGroup
    private lateinit var mbtgThemeAppearance: MaterialButtonToggleGroup
    private lateinit var mbtgCamera: MaterialButtonToggleGroup
    private lateinit var mbtgLanguage: MaterialButtonToggleGroup
    private lateinit var ssPomoTimer: SwitchCompat
    private lateinit var ssReminderSwitch : SwitchCompat
    private var language = "en"

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var pomoPendingIntent: PendingIntent
    private lateinit var pvOptions:OptionsPickerView<String>

    //PomoTimer
//    private lateinit var timer: CountDownTimer
//    private var isTimerRunning = false
//    private var timeLeftInMillis = 0L
//    //test try 30s
//    private var workingTime = 30000L // 25 minutes in milliseconds
//    //private var workingTime = 1500000L // 25 minutes in milliseconds
//    private var relaxationTime = 300000L // 5 minutes in milliseconds



    //AccountSettings
    private var accountSettings: AccountSettings = AccountSettings(0, 0, 0, 0, false, "00:00", false)

    //SharedPreferences
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCollector.addActivity(this)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pomoIntent = Intent(this, PomoTimerAlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        pomoPendingIntent = PendingIntent.getBroadcast(this, 0, pomoIntent, PendingIntent.FLAG_MUTABLE)


        titleLayout = binding.tlTitle
        var gson = Gson()
        titleLayout.setTextView_title("Account Settings")

        mbtgAIDevice = binding.mbtgAiDevice
        mbtgThemeAppearance = binding.mbtgThemeAppearance
        mbtgCamera = binding.mbtgCamera
        mbtgLanguage = binding.mbtgLanguage
        ssReminderSwitch = binding.ssReminderSwitch
        ssPomoTimer = binding.ssPomoTimer


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

        ssReminderSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            accountSettings.reminder = isChecked
            if (isChecked){
                showTimePickerDialog()
            }else{
                alarmManager.cancel(pendingIntent)
            }
        }

        ssPomoTimer.setOnCheckedChangeListener { buttonView, isChecked ->
            val optionsItems_pomoTimer = arrayListOf<String>("25 minutes", "30 minutes", "35 minutes", "40 minutes", "45 minutes", "50 minutes", "55 minutes", "60 minutes")

            var workingTime = 0L
            accountSettings.pomoTimer = isChecked
            if (isChecked) {
                //性别选择器
                pvOptions = OptionsPickerBuilder(
                    this@AccountSettingsActivity
                ) { options1, option2, options3, v -> //选择了则显示并暂存LoginUser，退出时在保存至数据库
                    val tx: String = optionsItems_pomoTimer.get(options1)
                    workingTime = tx.substring(0, 2).toLong() * 60 * 1000
                    //log
                    Log.d("workingTime", workingTime.toString())
                    //set dialog per every workingTime
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.timeInMillis = System.currentTimeMillis()
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    workingTime = (1 * 60 * 1000)
                  //  calendar.add(Calendar.MINUTE, workingTime)
                    alarmManager.setInexactRepeating(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + workingTime,
                        workingTime,
                        pomoPendingIntent
                    )

                }.setCancelColor(Color.GRAY).build()
                pvOptions.setPicker(optionsItems_pomoTimer)
                pvOptions.show()
            }else{
                //cancel alarm
                alarmManager.cancel(pomoPendingIntent)
            }
        }

        titleLayout.iv_save.setOnClickListener {
            //make a AlertDialog to confirm
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle(R.string.confirm_save)
            alertDialogBuilder.setMessage(R.string.confirm_save_and_restart)
            alertDialogBuilder.setPositiveButton(R.string.yes) { dialog, which ->
                AccountSettingsUtil.init(accountSettings)
                sp.edit().putString("account_settings", gson.toJson(accountSettings)).apply()
                //show Toast
                Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
                finish()
                //restart app
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            alertDialogBuilder.setNegativeButton(R.string.no) { dialog, which ->
                //do nothing
                dialog.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                scheduleNotification(selectedHour, selectedMinute)
            },
            hour,
            minute,
            true
        )
        // Show the time picker dialog
        timePickerDialog.show()
    }

    private fun scheduleNotification(hour: Int, minute: Int) {
        Log.d("hour", hour.toString())
        Log.d("minute", minute.toString())

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        // Set the alarm to fire every day at the selected time
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
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