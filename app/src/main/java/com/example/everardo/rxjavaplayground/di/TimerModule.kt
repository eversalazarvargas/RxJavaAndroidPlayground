package com.example.everardo.rxjavaplayground.di

import android.content.Context
import com.example.everardo.rxjavaplayground.database.TimerCache
import com.example.everardo.rxjavaplayground.viewmodel.TimerViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class TimerModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun provideTimerViewModelFactory(context: Context, timerCache: TimerCache): TimerViewModelFactory {
        return TimerViewModelFactory(context, timerCache)
    }

//    @Provides
//    fun provideTimerScheduler(): TimerScheduler {
//        return TimerScheduler()
//    }
}