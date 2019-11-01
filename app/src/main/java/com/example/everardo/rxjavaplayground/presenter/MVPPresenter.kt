package com.example.everardo.rxjavaplayground.presenter

import android.util.Log
import com.example.everardo.rxjavaplayground.model.MVPModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MVPPresenter(private val view: MVPContract.View, private val model: MVPModel) : MVPContract.Presenter {

    private val rootEventObservable = PublishSubject.create<String>()
    private val backEventObservable = PublishSubject.create<String>()
    private val resultObservable = BehaviorSubject.create<String>()
    private val isActive = BehaviorSubject.create<Boolean>()
    private val subscriptions = CompositeDisposable()

    init {
        view.setPresenter(this)
    }

    override fun create() {
        val disposable =
                Observable.merge(rootEventObservable, backEventObservable)
                        .switchMap({ prefix ->
                            model.addSuffix(prefix)
                        })
                        .doOnNext { Log.i("PROBANDO", "presenter on next ${it}") }
                        .subscribe(resultObservable::onNext)

        subscriptions.add(disposable)

        val liveResult: Observable<String> =
                isActive.switchMap { active -> if (active) resultObservable.hide() else Observable.never() }
                        .distinctUntilChanged()

        liveResult.observeOn(AndroidSchedulers.mainThread())
                .doOnNext { Log.i("PROBANDO", "view on next ${it}") }
                .subscribe { result ->
                    view.setResult(result)
                }
    }

    override fun destroy() {
        subscriptions.clear()
    }

    override fun resume() {
        isActive.onNext(true)
    }

    override fun pause() {
        isActive.onNext(false)
    }

    override fun setRoot() {
        rootEventObservable.onNext("root")
    }

    override fun setBack() {
        backEventObservable.onNext("back")
    }


}