package com.example.to_me_from_me

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class EmojiActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji)


        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val bottomSheetFragment = EmojiFragment()
        transaction.add(bottomSheetFragment, "EmojiFragment")
        transaction.commit()


    }
}