package com.example.everardo.rxjavaplayground

import android.util.Log
import com.example.everardo.rxjavaplayground.database.TimerCache
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TimerScheduler(private val timerCache: TimerCache) {

    private var disposable: Disposable? = null

    fun startTimer(freqInHertz: Long) {
        disposable = Observable
                .interval(1000 / freqInHertz, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    Log.d(javaClass.name, "writting time thread = ${Thread.currentThread()}")
                    timerCache.upsertTime()
                }
    }

    fun stopTimer() {
        disposable?.dispose()
    }

    fun restartTimer(): Completable {
        return timerCache.restartTimer()
    }

}