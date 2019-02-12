package com.example.everardo.rxjavaplayground.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.example.everardo.rxjavaplayground.TimerScheduler
import com.example.everardo.rxjavaplayground.database.TimerCache
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TimerViewModel(private val context: Context, private val timerScheduler: TimerScheduler, private val timerCache: TimerCache): ViewModel() {

    val time: LiveData<Int>
    private val startEnabled = MutableLiveData<Boolean>()
    private val stopEnabled = MutableLiveData<Boolean>()
    private val restartEnabled = MutableLiveData<Boolean>()
    private var isRunning = false

    init {
        time = LiveDataReactiveStreams.fromPublisher(timerCache.getTime()
                .toFlowable(BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .publish()
                .refCount())

        startEnabled.value = true
        stopEnabled.value = false
        restartEnabled.value = true
    }

    fun startTimer(freq: String) {
        if (!isRunning && !freq.isNullOrEmpty()) {
            val freqNumber = Integer.valueOf(freq)
            timerScheduler.startTimer(freqNumber.toLong())
            isRunning = true
            startEnabled.value = false
            stopEnabled.value = true
            restartEnabled.value = false
        }
    }

    fun stopTimer() {
        if (isRunning) {
            timerScheduler.stopTimer()
            isRunning = false
            startEnabled.value = true
            stopEnabled.value = false
            restartEnabled.value = true
        }
    }

    fun restartTimer() {
        if (!isRunning) {
            timerScheduler.restartTimer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        isRunning = false
                        startEnabled.value = true
                        stopEnabled.value = false
                        restartEnabled.value = true
                    }
        }
    }

    fun getStartEnabled(): LiveData<Boolean> = startEnabled

    fun getStopEnabled(): LiveData<Boolean> = stopEnabled

    fun getRestartEnabled(): LiveData<Boolean> = restartEnabled

}