package com.example.everardo.rxjavaplayground.activity

import android.app.Activity
import android.os.Bundle
import com.example.everardo.rxjavaplayground.R
import com.example.everardo.rxjavaplayground.model.MVPModel
import com.example.everardo.rxjavaplayground.presenter.MVPContract
import com.example.everardo.rxjavaplayground.presenter.MVPPresenter
import kotlinx.android.synthetic.main.activity_mvp.*

class MVPActivity: Activity(), MVPContract.View {

    private lateinit var presenter: MVPContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mvp)

        MVPPresenter(this, MVPModel())
        presenter.create()

        btnRoot.setOnClickListener { presenter.setRoot() }
        btnBack.setOnClickListener { presenter.setBack() }
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    override fun setPresenter(presenter: MVPContract.Presenter) {
        this.presenter = presenter
    }

    override fun setResult(result: String) {
        tvResult.text = result
    }
}