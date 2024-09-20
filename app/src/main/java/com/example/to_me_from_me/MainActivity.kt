package com.example.to_me_from_me

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.to_me_from_me.Mypage.MyPageFragment
import com.example.to_me_from_me.StatisticalReport.StatisticalReportFragment
import com.example.to_me_from_me.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private val bottomNavigation by lazy { findViewById<BottomNavigationView>(R.id.bottom_navigation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 리스너 연결
        bottomNavigation.setOnNavigationItemSelectedListener(this)

        // 바텀 네비게이션 바 초기화
        bottomNavigation.selectedItemId = R.id.mail
        val colorStateList = resources.getColorStateList(R.color.navigation_item_color, theme)
        bottomNavigation.itemIconTintList = null
        bottomNavigation.itemTextColor = colorStateList
        bottomNavigation.setOnNavigationItemSelectedListener(this)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        resetMenuIcons()

        var selectedFragment: Fragment? = null
        when (item.itemId) {
            R.id.mail -> {
                selectedFragment = FirstFragment()
                item.setIcon(R.drawable.ic_mail_s)
                Log.d("MainActivity", " write letter 클릭 ")
            }
            R.id.data -> {
                loadFragment(StatisticalReportFragment())
                item.setIcon(R.drawable.ic_data_s)
                Log.d("MainActivity", " Statistical report 클릭 ")
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

}
