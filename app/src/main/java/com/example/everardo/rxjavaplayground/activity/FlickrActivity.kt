package com.example.everardo.rxjavaplayground.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.everardo.rxjavaplayground.R
import com.example.everardo.rxjavaplayground.model.FlickrPhotoInfoResponse
import com.example.everardo.rxjavaplayground.model.FlickrSearchResponse
import com.example.everardo.rxjavaplayground.network.FlickrApi
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_flickr.*

class FlickrActivity: Activity() {

    private val flickrApi = FlickrApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_flickr)

        val searchTextObservable: Observable<String> =
                RxView.clicks(search_button)
                        .map { search_text.text.toString() }

        val searchPhotosObservable: Observable<List<FlickrSearchResponse.Photo>> =
                searchTextObservable
                        .switchMap({ text ->
                            flickrApi.searchPhotos(text, 10)
                        })

        //text expand
        val photoInfo: Observable<FlickrSearchResponse.Photo> =
                searchPhotosObservable
                        .flatMap({ photoList ->
                            Observable.fromIterable(photoList)

                        })

        photoInfo
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ photo ->
                    Log.i("PROBANDO", "photo = ${photo.title}")
                },
                        {error -> Log.e("PROBANDO", "error= ${error.message}")})


    }
}