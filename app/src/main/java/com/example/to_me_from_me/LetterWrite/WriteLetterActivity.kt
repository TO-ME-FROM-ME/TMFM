package com.example.to_me_from_me.LetterWrite


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R

class WriteLetterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_letter)

        // 초기 프래그먼트 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SituationFragment()).commit()

        val backButton: ImageView = findViewById(R.id.back_iv)
        backButton.setOnClickListener {
            supportFragmentManager.popBackStack()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val saveButton: ImageView = findViewById(R.id.save_iv)
        saveButton.setOnClickListener {
            val dialogFragment = StorageDialogFragment()
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL,
                R.style.RoundedBottomSheetDialogTheme
            )
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