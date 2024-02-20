package com.cm.app.data.database.schema

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class HistoryDbHelper(context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object{
        const val DATABASE_NAME = "comic.db"
        const val DATABASE_VERSION = 1

        private const val SQL_CREATE_ENTRIES = """
            CREATE TABLE ${HistoryContact.TABLE_NAME} (
                ${HistoryContact.COLUMN_ID} TEXT,
                ${HistoryContact.COLUMN_NAME} TEXT,
                ${HistoryContact.COLUMN_URL} TEXT,
                ${HistoryContact.COLUMN_IMAGE_URL} TEXT,
                ${HistoryContact.COLUMN_CHAPTER_ID} TEXT,
                ${HistoryContact.COLUMN_CHAPTER_NAME} TEXT,
                ${HistoryContact.COLUMN_CHAPTER_URL} TEXT,
                ${HistoryContact.COLUMN_CREATED_AT} TEXT
            )
        """
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${HistoryContact.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}