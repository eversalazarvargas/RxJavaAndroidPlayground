package com.example.everardo.rxjavaplayground.database

import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import javax.inject.Inject

class TimerCache @Inject constructor(private val context: Context) {

    fun upsertTime() {
        val resolver = context.contentResolver

        val cursor = resolver.query(TimerTable.CONTENT_URI, null, null, null, null)

        if (cursor.count == 0) { // no data in database
            val contentValues = ContentValues()
            with(contentValues) {
                put(TimerTable.TIME_VALUE, 1)
            }
            resolver.insert(TimerTable.CONTENT_URI, contentValues)
        } else { // data in database
            cursor.moveToFirst()
            var lastValue = cursor.getInt(cursor.getColumnIndex(TimerTable.TIME_VALUE))

            Log.d(javaClass.name, "last value from db = $lastValue")

            val contentValues = ContentValues()
            with(contentValues) {
                put(TimerTable.TIME_VALUE, lastValue + 1)
            }
            resolver.update(TimerTable.CONTENT_URI, contentValues, "${TimerTable.TIME_VALUE}=?", arrayOf(lastValue.toString()))
        }
        cursor.close()
    }

    fun getTime(): Observable<Int> {
        return Observable.create {
            val timeContentObserver = TimeContentObserver(context, it)
            context.contentResolver.registerContentObserver(TimerTable.CONTENT_URI, true, timeContentObserver)
        }
    }

    fun restartTimer(): Completable {
        return Completable.create {

            val resolver = context.contentResolver
            val cursor = resolver.query(TimerTable.CONTENT_URI, null, null, null, null)

            val contentValues = ContentValues()
            with(contentValues) {
                put(TimerTable.TIME_VALUE, 0)
            }

            if (cursor.count == 0) { // no data in database

                resolver.insert(TimerTable.CONTENT_URI, contentValues)
            } else { // data in database

                cursor.moveToFirst()
                var lastValue = cursor.getInt(cursor.getColumnIndex(TimerTable.TIME_VALUE))
                resolver.update(TimerTable.CONTENT_URI, contentValues, "${TimerTable.TIME_VALUE}=?", arrayOf(lastValue.toString()))
            }
            cursor.close()

            it.onComplete()
        }
    }

    class TimeContentObserver(private val context: Context, private val observableEmitter: ObservableEmitter<Int>): ContentObserver(null) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {

            if (observableEmitter.isDisposed) {
                Log.d(javaClass.name, "subscriber disposed")
                context.contentResolver.unregisterContentObserver(this)
            }

            Log.d(javaClass.name, "on change uri = $uri")
            val cursor = context.contentResolver.query(TimerTable.CONTENT_URI, null, null, null, null)
            cursor.moveToFirst()
            var lastValue = cursor.getInt(cursor.getColumnIndex(TimerTable.TIME_VALUE))

            cursor.close()

            observableEmitter.onNext(lastValue)
        }
    }
}