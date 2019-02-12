package com.example.everardo.rxjavaplayground

import android.app.Application
import com.example.everardo.rxjavaplayground.di.DaggerTimerComponent
import com.example.everardo.rxjavaplayground.di.TimerComponent
import com.example.everardo.rxjavaplayground.di.TimerModule

class TimerApplication: Application() {

    lateinit var timerComponent: TimerComponent

    override fun onCreate() {
        super.onCreate()

        timerComponent = DaggerTimerComponent.builder().timerModule(TimerModule(this)).build()
    }
}