package com.example.everardo.rxjavaplayground.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.everardo.rxjavaplayground.R
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

class SearchActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val textEventObservable: Observable<CharSequence> =
                RxTextView.textChanges(editText)
                        .filter { text -> text.length >= 3}
                        .debounce(150, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())

        textEventObservable.switchMap { text ->
            search(text.toString()).toObservable()
                    .subscribeOn(Schedulers.io())
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { results ->
                    Log.i("PROBANDO", results.toString() + " " + Thread.currentThread())
                }

    }

    private fun search(searchTerm: String): Single<List<String>> {
        return Single.create { emitter ->

            Log.i("PROBANDO", "onSubscribe")

            val result = when (searchTerm) {
                "hola" -> listOf("one", "two", "three")
                "mundo" -> listOf("1", "2", "3")
                else -> listOf("a", "b", "c")
            }

            emitter.onSuccess(result)
        }
    }
}