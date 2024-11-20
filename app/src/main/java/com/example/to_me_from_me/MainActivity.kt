package com.example.to_me_from_me

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.Mypage.MyPageFragment
import com.example.to_me_from_me.RandomLetter.RandomDialogFragment
import com.example.to_me_from_me.RandomLetter.RandomLetterActivity
import com.example.to_me_from_me.SetTest.TestAlarmReceiver
import com.example.to_me_from_me.StatisticalReport.StatisticalReportFragment
import com.example.to_me_from_me.databinding.ActivityMainBinding
import com.example.to_me_from_me.services.NotificationService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityMainBinding
    private lateinit var overlay: View
    private lateinit var auth: FirebaseAuth
    private val bottomNavigation by lazy { findViewById<BottomNavigationView>(R.id.bottom_navigation) }

    var selectedEmoji: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        overlay = findViewById(R.id.overlay) // 오버레이 뷰 초기화

        scheduleNotification()

        Log.d("main알람", "scheduleNotification() 실행완료")

        // SharedPreferences에서 이메일 불러오기
        val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
        val email = sharedPref.getString("userEmail", null) // 저장된 이메일 불러오기
        Log.d("UserPref", if (email != null) "사용자 이메일: $email" else "저장된 이메일이 없습니다.")

        // 리스너 연결
        bottomNavigation.setOnNavigationItemSelectedListener(this)

        // 바텀 네비게이션 바 초기화
        bottomNavigation.selectedItemId = R.id.mail
        val colorStateList = resources.getColorStateList(R.color.navigation_item_color, theme)
        bottomNavigation.itemIconTintList = null
        bottomNavigation.itemTextColor = colorStateList

        selectedEmoji = intent.getStringExtra("selectedEmoji")
        Log.d("selectedEmoji", "onCreate: $selectedEmoji")

        if (intent.getBooleanExtra("showDialog", false)) {
            showOverlayAndShowDialog()
        }


        observeTestDateChanges()
    }


    private fun showOverlayAndShowDialog() {
        binding.overlay.visibility = View.VISIBLE
        binding.overlay.alpha = 1f
        binding.overlay.animate()
            .alpha(0f)
            .setDuration(2000)
            .withEndAction {
                showRandomDialog()
                binding.overlay.visibility = View.GONE
            }
    }

    private fun scheduleNotification() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(uid).collection("letters")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        for (document in documents) {
                            val reservedDateString = document.getString("reservedate")
                            if (reservedDateString != null) {
                                val date = dateFormat.parse(reservedDateString)
                                Log.d("main알람", "date: $date")

                                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                val intent = Intent(this, NotificationService::class.java).apply {
                                    putExtra("message", " ") // 알림에 사용할 메시지
                                    putExtra("reservedDate", reservedDateString)
                                }
                                val pendingIntent = PendingIntent.getService(
                                    this, reservedDateString.hashCode(), intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                )
                                if (date.time > System.currentTimeMillis()) {
                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
                                    Log.d("main알람", "알림 예약: ${date.time} (formatted: $reservedDateString)")
                                } else {
                                    Log.d("main알람", "예약된 시간이 현재 시간보다 이전입니다. 알림을 예약하지 않습니다.")
                                }
                            }
                        }
                    } else {
                        Log.d("main알람", "Firebase : 해당 문서가 없습니다.")
                    }
                }
        }
    }

    private fun showRandomDialog() {
        val randomDialogFragment = RandomDialogFragment().apply {
            arguments = Bundle().apply {
                putString("selectedEmoji", selectedEmoji)
            }
        }
        randomDialogFragment.show(supportFragmentManager, "RandomDialogFragment")
        Log.d("selectedEmoji", "showRandomDialog : $selectedEmoji")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        resetMenuIcons()

        var selectedFragment: Fragment? = null
        when (item.itemId) {
            R.id.mail -> {
                selectedFragment = FirstFragment()
                item.setIcon(R.drawable.ic_mail_s)
                Log.d("MainActivity", "write letter 클릭")
            }
            R.id.data -> {
                loadFragment(StatisticalReportFragment())
                item.setIcon(R.drawable.ic_data_s)
                Log.d("MainActivity", "Statistical report 클릭")
                return true
            }
            R.id.profile -> {
                loadFragment(MyPageFragment())
                item.setIcon(R.drawable.ic_profile_s)
                Log.d("MainActivity", "profile 클릭")
                return true
            }
        }
        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()
            return true
        }
        return false
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun resetMenuIcons() {
        val menu = bottomNavigation.menu
        menu.findItem(R.id.mail).setIcon(R.drawable.ic_mail)
        menu.findItem(R.id.data).setIcon(R.drawable.ic_data)
        menu.findItem(R.id.profile).setIcon(R.drawable.ic_profile)
    }

    internal fun showOverlayAndStartActivity() {
        overlay.visibility = View.VISIBLE
        fadeIn(overlay)
        overlay.postDelayed({
            val intent = Intent(this, RandomLetterActivity::class.java)
            intent.putExtra("selectedEmoji", selectedEmoji)
            Log.d("selectedEmoji", "메인 : $selectedEmoji")
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }, 300)
    }

    private fun fadeIn(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        animator.duration = 600
        animator.start()
    }



    private fun observeTestDateChanges() {
        val user = auth.currentUser
        if (user != null) {
            val userRef = firestore.collection("users").document(user.uid)

            // Firestore에서 데이터 변경 감지
            userRef
                .get()
                .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val testDate = document.getString("testDate")
                    Log.d("Firestore", "testDate 변경 감지: $testDate")
                    if (!testDate.isNullOrEmpty()) {
                        setMonthlyAlarm(testDate)
                    }
                }

            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setMonthlyAlarm(testDate: String) {
        try {
            // 로그 추가: testDate 값 출력
            Log.d("알람", "받은 testDate: $testDate")

            // testDate를 Date 객체로 변환 (언더스코어 대신 콜론으로 변경된 형식)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // 수정된 포맷
            val formattedDate = testDate.replace("_", " ") // 언더스코어를 공백으로 변경
            Log.d("알람", "변경된 testDate: $formattedDate") // 수정된 testDate 출력

            val parsedDate = sdf.parse(formattedDate)
            // 로그 추가: parsedDate가 null인지 확인
            if (parsedDate != null) {
                Log.d("알람", "파싱된 날짜: $parsedDate")

                val calendar = Calendar.getInstance().apply {
                    time = parsedDate
                    add(Calendar.MINUTE, 5) // testDate 기준으로 3분 후로 설정
                }

                // 로그 추가: 예약 시간이 설정된 후
                Log.d("알람", "예약된 시간 (5분 후): ${calendar.time}")

                // 예약 시간이 현재 시간보다 이전이라면 알림을 예약하지 않음
                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    // 알림 설정
                    val intent = Intent(this, TestAlarmReceiver::class.java).apply {
                        putExtra("selected_hour", calendar.get(Calendar.HOUR_OF_DAY))
                        putExtra("selected_minute", calendar.get(Calendar.MINUTE))
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )

                    Log.d("알람", "알림 예약 완료: ${calendar.time}")
                } else {
                    Log.d("알람", "예약된 시간이 현재 시간보다 이전입니다. 알림을 예약하지 않습니다.")
                }

                // 한 달 후에 다시 알림 예약
                // 알림이 울린 후, 다시 한 달 뒤에 알림을 예약
                //scheduleNextAlarm(calendar)
                
                
            } else {
                Log.w("알람", "testDate를 파싱할 수 없습니다.")
            }
        } catch (e: Exception) {
            Log.e("알람", "setMonthlyAlarm 실행 중 오류 발생", e)
        }
    }

    // 한 달 후에 다시 알림을 예약하는 함수
    private fun scheduleNextAlarm(calendar: Calendar) {
        // 한 달 후에 알림을 울리도록 다시 예약
        calendar.add(Calendar.MONTH, 1) // 한 달 후로 설정

        val intent = Intent(this, TestAlarmReceiver::class.java).apply {
            putExtra("selected_hour", calendar.get(Calendar.HOUR_OF_DAY))
            putExtra("selected_minute", calendar.get(Calendar.MINUTE))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d("알람", "다음 한 달 후 알림 예약 완료: ${calendar.time}")
    }


}
