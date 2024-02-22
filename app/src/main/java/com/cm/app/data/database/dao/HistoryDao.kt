package com.cm.app.data.database.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.cm.app.data.database.entities.History
import com.cm.app.data.database.schema.HistoryContact
import com.cm.app.data.database.schema.DbHelper

class HistoryDao(context: Context) {
    private val dbHelper = DbHelper(context)


    fun insertHistory(
        id: String,
        name: String,
        url: String,
        urlImage: String,
        chapterId: String,
        chapterName: String,
        chapterUrl: String,
        createdAt: String,
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(HistoryContact.COLUMN_ID, id)
            put(HistoryContact.COLUMN_NAME, name)
            put(HistoryContact.COLUMN_URL, url)
            put(HistoryContact.COLUMN_IMAGE_URL, urlImage)
            put(HistoryContact.COLUMN_CHAPTER_ID, chapterId)
            put(HistoryContact.COLUMN_CHAPTER_NAME, chapterName)
            put(HistoryContact.COLUMN_CHAPTER_URL, chapterUrl)
            put(HistoryContact.COLUMN_CREATED_AT, createdAt)
        }
        val newRowId = db.insert(HistoryContact.TABLE_NAME, null, values)
        db.close()
        return newRowId
    }

    fun insertOrUpdate(history: History) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(HistoryContact.COLUMN_ID, history.id)
            put(HistoryContact.COLUMN_NAME, history.name)
            put(HistoryContact.COLUMN_URL, history.url)
            put(HistoryContact.COLUMN_IMAGE_URL, history.urlImage)
            put(HistoryContact.COLUMN_CHAPTER_ID, history.chapterId)
            put(HistoryContact.COLUMN_CHAPTER_NAME, history.chapterName)
            put(HistoryContact.COLUMN_CHAPTER_URL, history.chapterUrl)
            put(HistoryContact.COLUMN_CREATED_AT, history.createdAt)
        }

        // Insert or update the user based on the existence of the user's ID
        db.insertWithOnConflict(
            HistoryContact.TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )

        db.close()
    }

    fun getHistory(): ArrayList<History> {
        val histories = ArrayList<History>()
        val db = dbHelper.readableDatabase
        val limit = 5
//        val offset = limit * (pageNumber - 1)

        val sortOrder = "${HistoryContact.COLUMN_CREATED_AT} DESC"
        try {
            val cursor = db.query(
                HistoryContact.TABLE_NAME,
                arrayOf(
                    HistoryContact.COLUMN_ID,
                    HistoryContact.COLUMN_NAME,
                    HistoryContact.COLUMN_URL,
                    HistoryContact.COLUMN_IMAGE_URL,
                    HistoryContact.COLUMN_CHAPTER_ID,
                    HistoryContact.COLUMN_CHAPTER_NAME,
                    HistoryContact.COLUMN_CHAPTER_URL,
                    HistoryContact.COLUMN_CREATED_AT,
                ),
                null,
                null,
                null,
                null,
                sortOrder,
            )
            with(cursor) {
                while (moveToNext()) {
                    val id = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_ID))
                    val name = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_NAME))
                    val url = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_URL))
                    val imageUrl = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_IMAGE_URL))
                    val chapterId = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_CHAPTER_ID))
                    val chapterName =
                        getString(getColumnIndexOrThrow(HistoryContact.COLUMN_CHAPTER_NAME))
                    val chapterUrl = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_CHAPTER_URL))
                    val createdAt = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_CREATED_AT))
                    histories.add(History(id, name, url, imageUrl, chapterId, chapterName, chapterUrl,createdAt))
                }
                close()
            }
        }catch (e :Exception){}

        return histories
    }

    fun getById(id: String): History? {
        val db = dbHelper.readableDatabase
        val selection = "${HistoryContact.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id)
        var history: History? = null

        try {
            val cursor = db.query(
                HistoryContact.TABLE_NAME,
                arrayOf(
                    HistoryContact.COLUMN_ID,
                    HistoryContact.COLUMN_NAME,
                    HistoryContact.COLUMN_URL,
                    HistoryContact.COLUMN_IMAGE_URL,
                    HistoryContact.COLUMN_CHAPTER_ID,
                    HistoryContact.COLUMN_CHAPTER_NAME,
                    HistoryContact.COLUMN_CHAPTER_URL,
                    HistoryContact.COLUMN_CREATED_AT,
                ),
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            with(cursor) {
                if (moveToFirst()) {
                    val id = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_ID))
                    val name = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_NAME))
                    val url = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_URL))
                    val imageUrl = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_IMAGE_URL))
                    val chapterId = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_CHAPTER_ID))
                    val chapterName =
                        getString(getColumnIndexOrThrow(HistoryContact.COLUMN_CHAPTER_NAME))
                    val chapterUrl = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_CHAPTER_URL))
                    val createdAt = getString(getColumnIndexOrThrow(HistoryContact.COLUMN_CREATED_AT))


                    history = History(id, name, url, imageUrl, chapterId, chapterName, chapterUrl,createdAt)
                }
                close()
            }
        }catch (e:Exception){}

        return history
    }

    fun deleteHistoryById(id: String) {
        val db = dbHelper.writableDatabase
        val selection = "${HistoryContact.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id)
        db.delete(HistoryContact.TABLE_NAME, selection, selectionArgs)
        db.close()
    }

    fun deleteAllHistories() {
        val db = dbHelper.writableDatabase
        db.delete(HistoryContact.TABLE_NAME, null, null)
        db.close()
    }
}