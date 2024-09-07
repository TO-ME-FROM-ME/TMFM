package com.example.to_me_from_me

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.to_me_from_me.Signup.SignupPwdActivity
import com.google.firebase.auth.FirebaseAuth

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        auth = FirebaseAuth.getInstance()

        val emailLink = intent.data?.toString()
        if (emailLink != null) {
            verifyEmailLink(emailLink)
        }
    }

    private fun verifyEmailLink(emailLink: String) {
        val email = getSavedEmail() // Retrieve saved email
        if (email != null) {
            auth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Navigate to next activity
                        startActivity(Intent(this, SignupPwdActivity::class.java))
                        finish()
                    } else {
                        // Handle error
                        Toast.makeText(this, "Failed to verify email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun getSavedEmail(): String? {
        val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("email", null)
    }
}
