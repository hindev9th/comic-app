package com.cm.app.data.database.schema

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object{
        const val DATABASE_NAME = "comic.db"
        const val DATABASE_VERSION = 1

        private const val SQL_CREATE_HISTORY_ENTRIES = """
            CREATE TABLE ${HistoryContact.TABLE_NAME} (
                ${HistoryContact.COLUMN_ID} TEXT PRIMARY KEY ,
                ${HistoryContact.COLUMN_NAME} TEXT,
                ${HistoryContact.COLUMN_URL} TEXT,
                ${HistoryContact.COLUMN_IMAGE_URL} TEXT,
                ${HistoryContact.COLUMN_CHAPTER_ID} TEXT,
                ${HistoryContact.COLUMN_CHAPTER_NAME} TEXT,
                ${HistoryContact.COLUMN_CHAPTER_URL} TEXT,
                ${HistoryContact.COLUMN_CREATED_AT} TEXT
            )
        """

        private const val SQL_CREATE_FAVORITE_ENTRIES = """
            CREATE TABLE ${FavoriteContact.TABLE_NAME} (
                ${FavoriteContact.COLUMN_ID} TEXT PRIMARY KEY ,
                ${FavoriteContact.COLUMN_NAME} TEXT,
                ${FavoriteContact.COLUMN_URL} TEXT,
                ${FavoriteContact.COLUMN_IMAGE_URL} TEXT,
                ${FavoriteContact.COLUMN_CHAPTER_ID} TEXT,
                ${FavoriteContact.COLUMN_CHAPTER_NAME} TEXT,
                ${FavoriteContact.COLUMN_CHAPTER_URL} TEXT,
                ${FavoriteContact.COLUMN_CREATED_AT} TEXT
            )
        """
        private const val SQL_DELETE_HISTORY_ENTRIES = "DROP TABLE IF EXISTS ${HistoryContact.TABLE_NAME}"
        private const val SQL_DELETE_FAVORITE_ENTRIES = "DROP TABLE IF EXISTS ${FavoriteContact.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_HISTORY_ENTRIES)
        db?.execSQL(SQL_CREATE_FAVORITE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_HISTORY_ENTRIES)
        db?.execSQL(SQL_DELETE_FAVORITE_ENTRIES)
        onCreate(db)
    }
}