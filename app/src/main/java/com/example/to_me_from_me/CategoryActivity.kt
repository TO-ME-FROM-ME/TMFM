package com.example.to_me_from_me

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)


        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val bottomSheetFragment = CategoryFragment()
        transaction.add(bottomSheetFragment, "CategoryFragment")
        transaction.commit()

//        if (savedInstanceState==null){
//            supportFragmentManager.commit {
//                add(R.id.fragment_container_view, RecorderActivity())
//            }
//        }
    }
}