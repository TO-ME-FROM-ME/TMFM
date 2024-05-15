package com.example.to_me_from_me

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)


        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val bottomSheetFragment = CategoryFragment()
        transaction.add(bottomSheetFragment, "CategoryFragment")
        transaction.commit()
    }
}
