package com.example.everardo.rxjavaplayground

import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class RxBasicsTest {

    companion object {
        const val TAG = "PROBANDO"
    }

    @Test
    fun just() {

        var value = 0
        var completed = false

        val observable = Observable.just(5)

        val observer = object: Observer<Int> {
            override fun onNext(t: Int) {
                System.out.println("onNext")
                value = t
            }

            override fun onComplete() {
                System.out.println("onComplete")
                completed = true
            }

            override fun onError(e: Throwable) {
                System.out.println("onError")
            }

            override fun onSubscribe(d: Disposable) {
                System.out.println("onSubscribe")
            }
        }

        observable.subscribe(observer)

        assertEquals(5, value)
        assertTrue(completed)
    }

    @Test
    fun justLambdas() {
        var value = 0
        var completed = false

        val observable = Observable.just(5)

        observable.subscribe({ item ->
            System.out.println("onNext")
            value = item
        }, {error ->
            System.out.println("onError")
        }, {
            System.out.println("onComplete")
            completed = true
        })

        assertEquals(5, value)
        assertTrue(completed)
    }

    @Test
    fun fromExistingCollection() {
        val observable = Observable.fromIterable(getItemsFromRepository())

        var numberOfItems = 0
        var lastValue = 0

        observable.subscribe {
            numberOfItems++
            lastValue = it
        }

        assertEquals(8, numberOfItems)
        assertEquals(8, lastValue)
    }

    private fun getItemsFromRepository(): List<Int> {
        return listOf(1, 2, 3, 4, 5, 6, 7, 8)
    }

    @Test
    fun range() {
        var last = 0
        Observable.range(2, 10)
                .subscribe { next ->
                    System.out.println(next)
                    last = next
                }

        assertEquals(11, last)
    }

    @Test
    fun rangeWithUnsubscription() {

        var lastVal = 0
        var completed = false

        val subscriber = object: DisposableSubscriber<Int>() {
            override fun onComplete() {
                System.out.println("onComplete")
                completed = true
            }

            override fun onNext(t: Int?) {
                t?.let {
                    System.out.println(t)
                    lastVal = t
                    if (t == 5) {
                        dispose()
                    }
                }
            }

            override fun onError(t: Throwable?) {
                System.out.println("onError")
            }
        }

        Flowable.range(1, 10)
                .subscribe(subscriber)

        assertEquals(5, lastVal)
        assertFalse(completed)

    }

    @Test
    fun customObservable() {
        var timesSubscribed = 0

        val observable = Observable.create(object: ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                timesSubscribed++

                emitter.onNext(1)
                emitter.onNext(2)
                emitter.onNext(3)
                emitter.onNext(4)
                emitter.onNext(5)

                emitter.onComplete()
            }
        })

        var itemsReceived = 0
        var lastVal = 0

        observable.subscribe {
            itemsReceived++
            lastVal = it
        }

        assertEquals(5, itemsReceived)
        assertEquals(5, lastVal)

        itemsReceived = 0
        lastVal = 0


        observable.subscribe {
            itemsReceived++
            lastVal = it
        }

        assertEquals(5, itemsReceived)
        assertEquals(5, lastVal)

        assertEquals(2, timesSubscribed)

    }

    @Test
    fun customObservableWithCache() {
        var timesSubscribed = 0

        val observable = Observable.create<Int> { emitter ->
            timesSubscribed++

            emitter.onNext(1)
            emitter.onNext(2)
            emitter.onNext(3)
            emitter.onNext(4)
            emitter.onNext(5)

            emitter.onComplete()
        }.cache()

        var itemsReceived = 0
        var lastVal = 0

        observable.subscribe {
            itemsReceived++
            lastVal = it
        }

        assertEquals(5, itemsReceived)
        assertEquals(5, lastVal)

        itemsReceived = 0
        lastVal = 0


        observable.subscribe {
            itemsReceived++
            lastVal = it
        }

        assertEquals(5, itemsReceived)
        assertEquals(5, lastVal)

        assertEquals(1, timesSubscribed)
    }

    @Test
    fun timer() {
        val countDownLatch = CountDownLatch(1)

        var executedAfterDelay = false

        Observable.timer(5, TimeUnit.SECONDS)
                .subscribe {
                    System.out.println("executed in thread ${Thread.currentThread()}") // the thread is a background thread
                    executedAfterDelay = true
                    countDownLatch.countDown()
                }

        countDownLatch.await(10, TimeUnit.SECONDS)
        assertTrue(executedAfterDelay)
    }

    @Test
    fun shareAndCleanup() {

        val observable = Observable.create<Int> { emitter ->
            System.out.println("Opening connection")
            Thread(Runnable {
                var value = 0

                while (!emitter.isDisposed) {
                    Thread.sleep(2 * 1000) // long operation
                    System.out.println("Emitting")
                    emitter.onNext(value++)
                }
                System.out.println("Closing connection")

            }).start()
        }.publish().refCount()


        val countDownLatch = CountDownLatch(10)

        val disposable1 = observable.subscribe {
            System.out.println("subscriber 1 onNext $it")
            countDownLatch.countDown()
        }

        Thread.sleep(4 * 1000)

        val disposable2 = observable.subscribe {
            System.out.println("subscriber 2 onNext $it")
            countDownLatch.countDown()
        }

        countDownLatch.await(20, TimeUnit.SECONDS)

        disposable1.dispose()
//        disposable2.dispose()

        Thread.sleep(5 * 1000)
    }
}