package com.example.everardo.rxjavaplayground.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.everardo.rxjavaplayground.R
import com.example.everardo.rxjavaplayground.model.Entry
import com.example.everardo.rxjavaplayground.model.FeedParser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class NewsFeedActivity: Activity() {


    val client = OkHttpClient()
    val parser = FeedParser()
    val googleUrl = "https://news.google.com/?output=atom"
    val otherUrl = "http://www.theregister.co.uk/software/headlines.atom"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_news_feed)

        val googleFeedObservable = getFeed(googleUrl)
        val otherFeedObservable = getFeed(otherUrl)

        val combined: Observable<List<Entry>> = Observable.combineLatest(arrayOf(googleFeedObservable, otherFeedObservable)) {
            val newList = ArrayList<Entry>()
            newList.addAll(it[0] as List<Entry>)
            newList.addAll(it[1] as List<Entry>)
            newList
        }

        combined.map {
            sort(it)
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    print(it)
                }

    }

    private fun print(result: List<Entry>) {
        result.forEach {
            Log.i("PROBANDO", "title=${it.title} updated=${it.updated}")
        }
    }

    private fun sort(news: List<Entry>): List<Entry> {
        return news.sortedBy {
            it.updated
        }
    }

    private fun getFeed(url: String): Observable<List<Entry>> {
        return getNetworkCall(url)
                .map { response ->
                    response.body()?.let {
                        parser.parse(it.byteStream())
                    } ?: listOf()
                }
    }

    private fun getNetworkCall(url: String): Observable<Response> {
        return Observable.create<Response> { emitter ->
            try {
                val response = client.newCall(Request.Builder().url(url).build()).execute()
                emitter.onNext(response)
                emitter.onComplete()

            } catch (ex: Exception) {
                emitter.onError(ex)
            }
        }.subscribeOn(Schedulers.io())
    }
}