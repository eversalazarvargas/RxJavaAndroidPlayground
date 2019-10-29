package com.example.everardo.rxjavaplayground.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.everardo.rxjavaplayground.R
import com.example.everardo.rxjavaplayground.model.FlickrPhotoInfoResponse
import com.example.everardo.rxjavaplayground.model.FlickrPhotosGetSizesResponse
import com.example.everardo.rxjavaplayground.model.FlickrSearchResponse
import com.example.everardo.rxjavaplayground.model.Photo
import com.example.everardo.rxjavaplayground.network.FlickrApi
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
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
        val photosWithThumbnail: Observable<List<Photo>> =
                searchPhotosObservable.flatMap({ photoList ->
                    Observable.fromIterable(photoList)
                            .concatMap({ photo ->
                                Observable.combineLatest(flickrApi.photoInfo(photo.id),
                                        flickrApi.getSizes(photo.id),
                                        BiFunction<FlickrPhotoInfoResponse, FlickrPhotosGetSizesResponse, Photo> { photoInfo, photoSizes ->
                                            Photo.createPhoto(photoInfo.photoInfo, photoSizes.sizes)
                                        })
                            })
                            .toList()
                            .toObservable()
                })

        photosWithThumbnail
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ photos ->
                    photos.forEach { photo ->
                        Log.i("PROBANDO", "photo = ${photo.title} thumbnail = ${photo.thumbnailUrl}")
                    }

                },
                        { error -> Log.e("PROBANDO", "error= ${error.message}") })


    }
}