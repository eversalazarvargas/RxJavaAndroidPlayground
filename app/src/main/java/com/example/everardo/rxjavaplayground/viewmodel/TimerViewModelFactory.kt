package com.example.everardo.rxjavaplayground.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.example.everardo.rxjavaplayground.TimerScheduler
import com.example.everardo.rxjavaplayground.database.TimerCache

class TimerViewModelFactory(private val context: Context, private val timerCache: TimerCache): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        assert(modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            "Unknown view model class"
        }
        return TimerViewModel(context, TimerScheduler(timerCache), timerCache) as T
    }
}