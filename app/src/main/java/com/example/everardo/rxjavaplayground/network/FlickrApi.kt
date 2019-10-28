package com.example.everardo.rxjavaplayground.network

import android.util.Log
import com.example.everardo.rxjavaplayground.BuildConfig
import com.example.everardo.rxjavaplayground.model.FlickrPhotoInfoResponse
import com.example.everardo.rxjavaplayground.model.FlickrPhotosGetSizesResponse
import com.example.everardo.rxjavaplayground.model.FlickrSearchResponse
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class FlickrApi {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "https://api.flickr.com"
    private val apiKey = BuildConfig.FLICKR_API_KEY

    fun searchPhotos(search: String, limit: Int): Observable<List<FlickrSearchResponse.Photo>> {
        return Observable.create<List<FlickrSearchResponse.Photo>> { emitter ->
            try {
                val response = client.newCall(Request.Builder()
                        .url("$baseUrl/services/rest/?method=flickr.photos.getRecent&format=json&nojsoncallback=1&api_key=$apiKey&per_page=$limit&tags=$search")
                        .build()).execute()

                val searchResponse: FlickrSearchResponse = gson.fromJson(response.body().string(), FlickrSearchResponse::class.java)
                emitter.onNext(searchResponse.photos)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
                .retry(3)
                .onErrorReturn { listOf() }
    }

    fun photoInfo(photoId: String): Observable<FlickrPhotoInfoResponse> {
        return Observable.create<FlickrPhotoInfoResponse> { emitter ->
            try {
                val response = client.newCall(Request.Builder()
                        .url("$baseUrl/services/rest/?method=flickr.photos.getInfo&format=json&nojsoncallback=1&api_key=$apiKey&photo_id=$photoId")
                        .build()).execute()

                val photoInfoResponse: FlickrPhotoInfoResponse = gson.fromJson(response.body().string(), FlickrPhotoInfoResponse::class.java)
                emitter.onNext(photoInfoResponse)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
                .retry(3)
    }

    fun getSizes(photoId: String): Observable<FlickrPhotosGetSizesResponse> {
        return Observable.create<FlickrPhotosGetSizesResponse> { emitter ->
            try {
                val response = client.newCall(Request.Builder()
                        .url("$baseUrl/services/rest/?method=flickr.photos.getSizes&format=json&nojsoncallback=1&api_key=$apiKey&photo_id=$photoId")
                        .build()).execute()

                val photoSizes: FlickrPhotosGetSizesResponse = gson.fromJson(response.body().string(), FlickrPhotosGetSizesResponse::class.java)
                emitter.onNext(photoSizes)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
                .retry(3)
    }
}