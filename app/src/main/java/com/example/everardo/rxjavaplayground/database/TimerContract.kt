package com.example.everardo.rxjavaplayground.database

import android.net.Uri

class TimerContract {
    companion object {
        internal const val CONTENT_AUTHORITY = "com.example.everardo.timer.provider"
        internal val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
        internal val PATH_TIME = "time"

        internal val DB_NAME = "timer_db"
        internal val DB_VERSION = 1
    }
}

class TimerTable {
    companion object {
        internal const val NAME = "times"
        internal const val TIME_ID = "timeId"
        internal const val TIME_VALUE = "timeValue"

        internal val CONTENT_URI = TimerContract.BASE_CONTENT_URI.buildUpon().appendPath(TimerContract.PATH_TIME).build()

        internal val CONTENT_TYPE =
                "vnd.android.cursor.dir/$CONTENT_URI/${TimerContract.PATH_TIME}"
        internal val CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/$CONTENT_URI/${TimerContract.PATH_TIME}"
    }
}