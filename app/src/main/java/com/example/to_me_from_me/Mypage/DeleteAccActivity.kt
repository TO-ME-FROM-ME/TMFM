package com.example.to_me_from_me.Mypage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.to_me_from_me.LoginActivity
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth

class
DeleteAccActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deleteacc)

        val backButton: ImageView = findViewById(R.id.back_iv)
        val deleteButton : Button = findViewById(R.id.next_btn)

        // FirebaseAuth 초기화
        auth = FirebaseAuth.getInstance()

        backButton.setOnClickListener {
            //MyPageFragment로 이동하기
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToLoad", "MyPageFragment")
            startActivity(intent)
            finish()
        }

        deleteButton.setOnClickListener {
            val user = auth.currentUser
            val uid = user?.uid

            Log.d("deleteButton", "user $user")


            user?.let {
                // 회원 탈퇴 시도
                it.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 회원 탈퇴 성공
                        Toast.makeText(this, "계정이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()

                        // LoginActivity로 이동
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // 회원 탈퇴 실패
                        Toast.makeText(this, "계정 삭제에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                // 사용자가 null일 경우
                Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            }

        }

    }
}