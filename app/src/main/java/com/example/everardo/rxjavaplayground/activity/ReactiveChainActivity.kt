package com.example.everardo.rxjavaplayground.activity

import android.app.Activity
import android.os.Bundle
import com.example.everardo.rxjavaplayground.R
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_reactive_chain.*

class ReactiveChainActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reactive_chain)

        val buttonClickObservable = RxView.clicks(btnStart)

        val photoListObservable =
                buttonClickObservable.map { "searchQuery" }
                        .switchMap { query ->  searchPhotos(query) }

        val photoWithThumbListObservable: Observable<List<PhotoWithThumb>> =
                photoListObservable.flatMap { photos ->
                    Observable.fromIterable(photos)
                            .concatMap { photo ->
                                getThumbnail(photo)
                            }
                            .toList()
                            .toObservable()
                }

        photoWithThumbListObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {photosWithThumb ->
                    val result = photosWithThumb.joinToString(separator = " | ")
                    tvChainResult.text = result
                }

    }

    private fun searchPhotos(searchText: String): Observable<List<Photo>> {
        val observable: Observable<List<Photo>> = Observable.create {emitter ->
            val photos = listOf(Photo("1", "pic1"),
                    Photo("2", "pic2"),
                    Photo("3", "pic3"))
            emitter.onNext(photos)
            emitter.onComplete()
        }
        return observable.subscribeOn(Schedulers.io())
    }

    private fun getThumbnail(photo: Photo): Observable<PhotoWithThumb> {
        val observable: Observable<PhotoWithThumb> = Observable.create { emitter ->
            emitter.onNext(PhotoWithThumb(photo.id, photo.name, "http://mydomain/${photo.name}"))
            emitter.onComplete()
        }
        return observable.subscribeOn(Schedulers.io())
    }

    data class Photo(val id: String, val name: String)

    data class PhotoWithThumb(val id: String, val name: String, val url: String)
}