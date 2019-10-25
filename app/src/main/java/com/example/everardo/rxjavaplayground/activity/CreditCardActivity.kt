package com.example.everardo.rxjavaplayground.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.everardo.rxjavaplayground.R
import com.example.everardo.rxjavaplayground.model.CardType
import com.example.everardo.rxjavaplayground.util.ValidationUtils
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_credit_card.*
import java.util.regex.Pattern
import io.reactivex.functions.Function3

class CreditCardActivity: Activity() {

    var expirationDatePattern = Pattern.compile("^\\d\\d/\\d\\d$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_credit_card)

        val expirationDateObservable: Observable<String> = RxTextView.textChanges(expiration_date)
                .map { text -> text.toString() }

        val expirationDateValid: Observable<Boolean> =
                expirationDateObservable.map { date -> expirationDatePattern.matcher(date).find() }

        val creditCardNumberObservable: Observable<String> =
                RxTextView.textChanges(credit_card_number)
                        .map { text -> text.toString()}

        val cardTypeObservable: Observable<CardType> =
                creditCardNumberObservable.map { number -> CardType.fromNumber(number) }

        val cardTypeValid: Observable<Boolean> =
                cardTypeObservable.map { type -> type != CardType.UNKNOWN }

        val checkSumValid: Observable<Boolean> =
                creditCardNumberObservable.map { number -> ValidationUtils.checkCardChecksum(number) }

        val creditCardNumberValid: Observable<Boolean> =
                ValidationUtils.and(cardTypeValid, checkSumValid)

        val cvcObservable: Observable<String> =
                RxTextView.textChanges(credit_card_cvc)
                        .map { text -> text.toString() }

        val cardTypeLength: Observable<Int> =
                cardTypeObservable.map { type -> type.cvcLength }

        val cvcLength: Observable<Int> =
                cvcObservable.map { cvc -> cvc.length }

        val cvcValid:Observable<Boolean> =
                equals(cardTypeLength, cvcLength)

        val creditCardValid: Observable<Boolean> =
                Observable.combineLatest(creditCardNumberValid, expirationDateValid, cvcValid,
                        Function3<Boolean, Boolean, Boolean, Boolean> { t1, t2, t3 -> t1 && t2 && t3 })


        creditCardValid.subscribe(
                Consumer<Boolean> { next -> submit_button.isEnabled = next },
                Consumer<Throwable> { error -> Log.i("PROBANDO", "error = ${error.stackTrace}") }
        )
    }

    private fun equals(obs1: Observable<Int>, obs2: Observable<Int>): Observable<Boolean> {
        return Observable.combineLatest(obs1, obs2, BiFunction<Int, Int, Boolean> { num1, num2 -> num1 == num2 })
    }
}