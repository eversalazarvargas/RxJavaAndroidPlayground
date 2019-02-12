package com.example.everardo.rxjavaplayground

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.example.everardo.rxjavaplayground.viewmodel.TimerViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: FragmentActivity() {

    private lateinit var viewModel: TimerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val component = (application as TimerApplication).timerComponent
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = component.getTimerViewModelFactory().create(TimerViewModel::class.java)

        btnStart.setOnClickListener {
            viewModel.startTimer(txtFieldFreq.text.toString())
        }

        btnStop.setOnClickListener {
            viewModel.stopTimer()
        }

        btnRestart.setOnClickListener {
            viewModel.restartTimer()
        }

        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.time.observe(this, Observer<Int> {
            it?.let {
                txtTitle.text = it.toString()
            }
        })

        viewModel.getStartEnabled().observe(this, Observer<Boolean> {
            it?.let {
                btnStart.isEnabled = it
            }
        })

        viewModel.getStopEnabled().observe(this, Observer<Boolean> {
            it?.let {
                btnStop.isEnabled = it
            }
        })

        viewModel.getRestartEnabled().observe(this, Observer<Boolean> {
            it?.let {
                btnRestart.isEnabled = it
            }
        })
    }
}