package com.example.to_me_from_me.Mailbox

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.to_me_from_me.R

class SendMailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_mailbox)

        // 초기 프래그먼트 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SendMailBoxFragment()).commit()


        val backButton: ImageView = findViewById(R.id.back_iv)
        backButton.setOnClickListener {
            supportFragmentManager.popBackStack()
            val intent = Intent(this, MailboxActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}