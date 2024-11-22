package com.example.to_me_from_me.SetTest

import android.annotation.SuppressLint
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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.example.to_me_from_me.MainActivity
import com.example.to_me_from_me.Mypage.AlarmReceiver
import com.example.to_me_from_me.SetTest.TestAlarmReceiver
import java.util.Calendar

class SETestFinActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setestfin)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val totalScore = intent.getIntExtra("totalScore", 0)
        Log.d("totalScore", "totalScore : $totalScore")

        val saveButton = findViewById<ImageView>(R.id.save_iv)
        val dialog = TestquitDialogFragment()

        val backButton = findViewById<ImageView>(R.id.back_iv)
        backButton.setOnClickListener {
            finish()
        }

        val finButton = findViewById<Button>(R.id.test_fin)
        finButton.setOnClickListener {
            saveTotalScoreToFirestore(totalScore)
            saveTestDateToFirestore()
        }

    }

    private fun saveTotalScoreToFirestore(totalScore: Int) {
        val user = auth.currentUser

        if (user != null) {
            val userRef = firestore.collection("users").document(user.uid)
            userRef.update("totalScore", totalScore)
                .addOnSuccessListener {
                    Log.d("Firestore", "totalScore successfully updated!")
                    Toast.makeText(this, "검사결과가 저장되었습니다!", Toast.LENGTH_SHORT).show()
                    //startActivity(Intent(this, CoachMarkActivity::class.java))
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

            val currentYear = SimpleDateFormat("YYYY", Locale.getDefault()).format(Date()).toInt()
            val currentMonth = SimpleDateFormat("MM", Locale.getDefault()).format(Date())
            val documentName = "$currentYear-$currentMonth"

            // 현재 날짜 형식 생성
            val timestamp = Date()

            val scoreData = hashMapOf(
                "score" to totalScore,
                "timestamp" to timestamp
            )

            firestore.collection("users")
                .document(user.uid)
                .collection("scores")
                .document(documentName)
                .set(scoreData)
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error updating totalScore", e)
                    Toast.makeText(this, "검사결과 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTestDateToFirestore() {
        val user = auth.currentUser
        if (user != null) {
            val userRef = firestore.collection("users").document(user.uid)

            // 현재 날짜 형식 생성
            val testDate = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(Date())

            Log.d("Firestore", "저장할 testDate 값: $testDate")

            // testDate 필드에 현재 날짜 저장
            userRef.update("testDate", testDate)
                .addOnSuccessListener {
                    Log.d("Firestore", "testDate successfully updated!")
                }
        }
    }


}
