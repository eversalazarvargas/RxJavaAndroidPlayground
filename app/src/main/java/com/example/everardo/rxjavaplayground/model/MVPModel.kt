package com.example.everardo.rxjavaplayground.model

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MVPModel {

    fun addSuffix(prefix: String): Observable<String> {
        return Observable.create<String>({ emitter ->
            emitter.onNext(prefix + "Suffix")
            emitter.onComplete()
        }).delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
    }
}