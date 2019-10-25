package com.example.everardo.rxjavaplayground.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.example.everardo.rxjavaplayground.R
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        btnTimer.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        btnTextBindings.setOnClickListener { startActivity(Intent(this, RxTextBindingActivity::class.java)) }
        btnReactiveChain.setOnClickListener { startActivity(Intent(this, ReactiveChainActivity::class.java)) }
        btnSubject.setOnClickListener { startActivity(Intent(this, SubjectActivity::class.java)) }
        btnSearch.setOnClickListener { startActivity(Intent(this, SearchActivity::class.java)) }
        btnNews.setOnClickListener { startActivity(Intent(this, NewsFeedActivity::class.java)) }
        btnCreditCard.setOnClickListener { startActivity(Intent(this, CreditCardActivity::class.java)) }
    }
}