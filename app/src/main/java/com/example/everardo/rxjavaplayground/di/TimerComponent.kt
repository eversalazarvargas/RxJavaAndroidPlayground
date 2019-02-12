package com.example.everardo.rxjavaplayground.di

import com.example.everardo.rxjavaplayground.viewmodel.TimerViewModelFactory
import dagger.Component

@Component(modules = [TimerModule::class])
interface TimerComponent {
    fun getTimerViewModelFactory(): TimerViewModelFactory
}