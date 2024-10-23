package com.example.to_me_from_me.SetTest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.to_me_from_me.CoachMark.CoachMarkActivity
import com.example.to_me_from_me.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SETestFinActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setestfin)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val totalScore = intent.getIntExtra("totalScore", 0) // 기본값 0 설정
        Log.d("totalScore" ,"totalScore : $totalScore")

        val saveButton = findViewById<ImageView>(R.id.save_iv)
        val dialog = TestquitDialogFragment()
       // dialog.show(supportFragmentManager, "TestDialogFragment")


        val backButton = findViewById<ImageView>(R.id.back_iv)
        backButton.setOnClickListener {
            // 이전 Activity로 이동
            finish()
        }

        val finButton = findViewById<Button>(R.id.test_fin)
        finButton.setOnClickListener{
            saveTotalScoreToFirestore(totalScore)

        }
    }
    private fun saveTotalScoreToFirestore(totalScore: Int) {
        val user = auth.currentUser

        if (user != null) {
            // Firestore에서 user 컬렉션의 현재 사용자 UID로 문서를 참조
            val userRef = firestore.collection("users").document(user.uid)

            // totalScore 필드를 Firestore에 저장
            userRef.update("totalScore", totalScore)
                .addOnSuccessListener {
                    // 저장 성공 시 로그 및 토스트 메시지
                    Log.d("Firestore", "totalScore successfully updated!")
                    Toast.makeText(this, "검사결과가 저장되었습니다!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, CoachMarkActivity::class.java))
                    finish() // 현재 화면 종료
                }
            val currentMonth = SimpleDateFormat("M", Locale.getDefault()).format(Date()).toInt()
            // 새로운 컬렉션에 totalScore 추가
            val scoreData = hashMapOf(
                "score" to totalScore,
                "timestamp" to FieldValue.serverTimestamp()
            )

            // scores 컬렉션에 새 문서 추가
            firestore.collection("users")
                .document(user.uid)
                .collection("scores")
                .document(currentMonth.toString()) // 문서 이름을 월 정보로 설정
                .set(scoreData)

                .addOnFailureListener { e ->
                    // 저장 실패 시 로그 및 토스트 메시지
                    Log.w("Firestore", "Error updating totalScore", e)
                    Toast.makeText(this, "검사결과 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // 사용자가 로그인되어 있지 않을 경우 처리
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

}