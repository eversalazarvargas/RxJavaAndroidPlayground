package com.example.everardo.rxjavaplayground.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.everardo.rxjavaplayground.database.TimerContract.Companion.DB_NAME
import com.example.everardo.rxjavaplayground.database.TimerContract.Companion.DB_VERSION


internal class DatabaseClient private constructor(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private var instance: DatabaseClient? = null

        fun getInstance(context: Context): DatabaseClient {
            return instance?.let { instance } ?: DatabaseClient(context)
        }
    }

    internal val db: SQLiteDatabase = writableDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        db?.let { createTimesTable(it) }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Update any SQLite tables here
        db?.execSQL("DROP TABLE IF EXISTS [ ${TimerTable.NAME} ];")
        onCreate(db)
    }

    /**
     * Creates our 'articles' SQLite database table.
     * @param db [SQLiteDatabase]
     */
    private fun createTimesTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE [${TimerTable.NAME}] ([${TimerTable.TIME_ID}] TEXT UNIQUE PRIMARY KEY, [${TimerTable.TIME_VALUE}] INTEGER NOT NULL);")
    }
}