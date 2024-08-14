package com.example.to_me_from_me

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

class SignupEmailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_email)

        auth = FirebaseAuth.getInstance()

        val emailBtn = findViewById<Button>(R.id.email_code_btn)
        val emailEditText = findViewById<EditText>(R.id.email_et)

        emailBtn.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                sendVerificationEmail(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVerificationEmail(email: String) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://www.example.com/verify")
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                "com.example.to_me_from_me", // your package name
                true, // install if not available
                "12" // minimum version
            )
            .build()

        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Save the email in preferences to handle the link later
                    saveEmailInPrefs(email)
                    Toast.makeText(this, "Verification email sent to $email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send verification email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.d("이메일 가입오류","${task.exception?.message}")
                }
            }
    }

    private fun saveEmailInPrefs(email: String) {
        val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("email", email)
            apply()
        }
    }
}
