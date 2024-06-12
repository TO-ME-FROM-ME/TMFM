package com.example.to_me_from_me


import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels

class WriteLetterActivity : AppCompatActivity() {

    private val sharedViewModel: ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_letter)

        // 초기 프래그먼트 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SituationFragment()).commit()

        val backButton: ImageView = findViewById(R.id.back_iv)
        backButton.setOnClickListener {
            supportFragmentManager.popBackStack()
        }

        val saveButton: ImageView = findViewById(R.id.save_iv)
        saveButton.setOnClickListener {
            val dialogFragment = StorageDialogFragment()
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundedBottomSheetDialogTheme)
            dialogFragment.show(supportFragmentManager, "StorageDialogFragment")
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

}