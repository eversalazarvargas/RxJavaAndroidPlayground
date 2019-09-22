package com.example.everardo.rxjavaplayground.activity

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.EditText
import android.widget.TextView
import com.example.everardo.rxjavaplayground.R
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import kotlinx.android.synthetic.main.activity_rx_text_bindings.*
import java.util.concurrent.TimeUnit

class RxTextBindingActivity: FragmentActivity() {

//    private val serialDisposable = SerialDisposable()
//    private lateinit var combined: Observable<String>

//    private lateinit var soonObserver: Observable<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_rx_text_bindings)

        val editText: EditText = findViewById(R.id.txtEdit)
        val textView: TextView = findViewById(R.id.tv)

        RxTextView.textChanges(editText)
                .filter { text -> text.length >= 7 }
                .subscribe { textView.text = "Text is too long" }

        val textSrc1 = RxTextView.textChanges(txtEditSource1)
        val textSrc2 = RxTextView.textChanges(txtEditSource2)

        val combined: Observable<String> = Observable.combineLatest(arrayOf(textSrc1, textSrc2)) {
            it.joinToString(separator = ",")
        }
        combined.subscribe { tvOutputCombineLatest.text = it }

        val obs = Observable.interval(1, TimeUnit.SECONDS)
                .map { it.toString() }
                .observeOn(AndroidSchedulers.mainThread())

        val serialDisposable = SerialDisposable()

        var compositeDisposable = CompositeDisposable()

        RxView.clicks(outputTimer1).subscribe {
//            serialDisposable.set(obs.subscribe { outputTimer1.text = it })
            compositeDisposable.add(obs.subscribe { outputTimer1.text = it })
        }

        outputTimer2.setOnClickListener {
//            serialDisposable.set(obs.subscribe { outputTimer2.text = it })
            compositeDisposable.add(obs.subscribe { outputTimer2.text = it })
        }

        Observable.fromIterable(listOf(1, 2))
                .flatMap { num -> Observable.just(num) }

        btnReset.setOnClickListener {
            compositeDisposable.dispose()
            compositeDisposable = CompositeDisposable()
        }
    }
}