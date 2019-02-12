package com.example.everardo.rxjavaplayground.database

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

internal class TimerContentProvider: ContentProvider() {

    companion object {
        private const val TIMES = 1
        private const val TIME_ID = 2
    }

    private lateinit var db: SQLiteDatabase
    private lateinit var uriMatcher: UriMatcher

    override fun onCreate(): Boolean {
        db = DatabaseClient.getInstance(context).db

        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(TimerContract.CONTENT_AUTHORITY, TimerContract.PATH_TIME, TIMES)
        uriMatcher.addURI(TimerContract.CONTENT_AUTHORITY, "${TimerContract.PATH_TIME}/#", TIME_ID)

        return true
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            TIMES -> TimerTable.CONTENT_TYPE
            TIME_ID -> TimerTable.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Invalid URI!")
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        var cursor: Cursor =
                when(uriMatcher.match(uri)) {
                    TIMES -> {
                        db.query(TimerTable.NAME, projection, selection, selectionArgs, null, null, sortOrder)
                    }
                    TIME_ID -> {
                        val id = ContentUris.parseId(uri)
                        db.query(TimerTable.NAME, projection, "${TimerTable.TIME_ID}=?", arrayOf(id.toString()), null, null, sortOrder)
                    }
                    else -> throw IllegalArgumentException("Invalid URI!")
                }

        // Tell the cursor to register a content observer to observe changes to the
        // URI or its descendants.
        assert(context != null)
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val returnUri =
                when(uriMatcher.match(uri)) {
                    TIMES -> {
                        val id = db.insert(TimerTable.NAME, null, values)
                        ContentUris.withAppendedId(TimerTable.CONTENT_URI, id)
                    }
                    else -> throw IllegalArgumentException("Invalid URI!")
                }

        context.contentResolver.notifyChange(uri, null)
        return returnUri
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        var rows: Int
        when (uriMatcher.match(uri)) {
            TIMES -> rows = db.update(TimerTable.NAME, values, selection, selectionArgs)
            else -> throw IllegalArgumentException("Invalid URI!")
        }

        if (rows != 0) {
            context.contentResolver.notifyChange(uri, null)
        }
        return rows
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var rows: Int
        when (uriMatcher.match(uri)) {
            TIMES -> rows = db.delete(TimerTable.NAME, selection, selectionArgs)
            else -> throw IllegalArgumentException("Invalid URI!")
        }

        if (rows != 0) {
            context.contentResolver.notifyChange(uri, null)
        }
        return rows
    }

}