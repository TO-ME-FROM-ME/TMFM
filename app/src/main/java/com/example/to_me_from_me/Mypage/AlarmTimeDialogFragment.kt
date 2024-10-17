package com.example.to_me_from_me.Mypage

import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings.Global.putLong
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.to_me_from_me.LetterWrite.CustomTimePicker
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AlarmTimeDialogFragment : DialogFragment() {
    private val tag = "AlarmTimeDialogFragment"
    private lateinit var viewModel: SharedViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.alarm_time_dialog, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val closeIv: Button = view.findViewById(R.id.cancel_btn)
        closeIv.setOnClickListener {
            dismiss()
        }

        val timePicker: CustomTimePicker = view.findViewById(R.id.timePicker)
        timePicker.setIs24HourView(false)

        val okButton: Button = view.findViewById(R.id.ok_btn)
        okButton.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute

            // 현재 날짜에 선택한 시간으로 Calendar 객체 생성
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // 현재 시간보다 설정한 시간이 과거일 경우 다음 날로 설정
            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            // 알람 설정
            setDailyAlarm(requireContext(), calendar)

            // 오전/오후 정보를 별도로 저장
            val period = if (hour < 12) "오전" else "오후"
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val formattedTime = String.format("%s %02d시 %02d분", period, displayHour, minute)

            // 선택한 시간을 전달하기 위해 Intent 사용
            val intent = Intent().apply {
                putExtra("selected_time", calendar.timeInMillis)
            }
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)


            Toast.makeText(requireContext(), "알람 :  $formattedTime", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return view
    }

    private fun setDailyAlarm(context: Context, calendar: Calendar) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("selected_hour", calendar.get(Calendar.HOUR_OF_DAY))
            putExtra("selected_minute", calendar.get(Calendar.MINUTE))
        }

        // SharedPreferences에 알람 시간 저장
        val sharedPref = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("selected_hour", calendar.get(Calendar.HOUR_OF_DAY))
            putInt("selected_minute", calendar.get(Calendar.MINUTE))
            apply() // 변경 사항 적용
        }
        // AlarmActivity를 호출하는 Intent
        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("selected_hour", calendar.get(Calendar.HOUR_OF_DAY))
            putExtra("selected_minute", calendar.get(Calendar.MINUTE))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK // 새 태스크로 시작
        }


        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 매일 반복 알람 설정
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // 하루마다 반복
            pendingIntent
        )
        context.startActivity(activityIntent) // AlarmActivity 시작

        Log.d("알람", "$tag 알람이 설정되었습니다. 시간: ${calendar.time}")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ALARM_CHANNEL",
                "알람 채널",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "알람을 위한 채널"
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner_all) // 배경으로 round_corner.xml 설정
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setTitle("몇 시에 받고 싶어?")
        }
    }
}



