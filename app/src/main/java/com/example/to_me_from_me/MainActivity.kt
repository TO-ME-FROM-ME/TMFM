package com.example.to_me_from_me

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private val bottomNavigation by lazy { findViewById<BottomNavigationView>(R.id.bottom_navigation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 가져온 값 사용 예시
        val selectedDate = intent.getStringExtra("selectedDate")
        val selectedTime = intent.getStringExtra("selectedTime")
        Log.d("MainActivity", "선택된 날짜: $selectedDate")
        Log.d("MainActivity", "선택된 시간: $selectedTime")

        // 리스너 연결
        bottomNavigation.setOnNavigationItemSelectedListener(this)

        // 바텀 네비게이션 바 초기화
        bottomNavigation.selectedItemId = R.id.mail
        val colorStateList = resources.getColorStateList(R.color.navigation_item_color, theme)
        bottomNavigation.itemIconTintList = colorStateList
        bottomNavigation.itemTextColor = colorStateList

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var selectedFragment: Fragment? = null
        when (item.itemId) {
            R.id.mail -> {
                selectedFragment = FirstFragment()
                Log.d("MainActivity", " write letter 클릭 ")
            }
            R.id.data -> {
                val intent = Intent(this, StatisticalReportActivity::class.java)
                startActivity(intent)
                Log.d("MainActivity", " Statistical report 클릭 ")
                return true
            }
            R.id.profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                Log.d("MainActivity", " profile 클릭 ")
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
}
