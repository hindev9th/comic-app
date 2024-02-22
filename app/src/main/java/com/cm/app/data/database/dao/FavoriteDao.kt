package com.cm.app.data.database.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.cm.app.data.database.entities.Favorite
import com.cm.app.data.database.schema.DbHelper
import com.cm.app.data.database.schema.FavoriteContact

class FavoriteDao(context: Context) {
    private val dbHelper = DbHelper(context)


    fun insertFavorite(favorite: Favorite
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FavoriteContact.COLUMN_ID, favorite.id)
            put(FavoriteContact.COLUMN_NAME, favorite.name)
            put(FavoriteContact.COLUMN_URL, favorite.url)
            put(FavoriteContact.COLUMN_IMAGE_URL, favorite.urlImage)
            put(FavoriteContact.COLUMN_CHAPTER_ID, favorite.chapterId)
            put(FavoriteContact.COLUMN_CHAPTER_NAME, favorite.chapterName)
            put(FavoriteContact.COLUMN_CHAPTER_URL, favorite.chapterUrl)
            put(FavoriteContact.COLUMN_CREATED_AT, favorite.createdAt)
        }
        val newRowId = db.insert(FavoriteContact.TABLE_NAME, null, values)
        db.close()
        return newRowId
    }

    fun insertOrUpdate(favorite: Favorite) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FavoriteContact.COLUMN_ID, favorite.id)
            put(FavoriteContact.COLUMN_NAME, favorite.name)
            put(FavoriteContact.COLUMN_URL, favorite.url)
            put(FavoriteContact.COLUMN_IMAGE_URL, favorite.urlImage)
            put(FavoriteContact.COLUMN_CHAPTER_ID, favorite.chapterId)
            put(FavoriteContact.COLUMN_CHAPTER_NAME, favorite.chapterName)
            put(FavoriteContact.COLUMN_CHAPTER_URL, favorite.chapterUrl)
            put(FavoriteContact.COLUMN_CREATED_AT, favorite.createdAt)
        }

        // Insert or update the user based on the existence of the user's ID
        db.insertWithOnConflict(
            FavoriteContact.TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )

        db.close()
    }

    fun getFavorite(): ArrayList<Favorite> {
        val histories = ArrayList<Favorite>()
        val db = dbHelper.readableDatabase
        val limit = 5
//        val offset = limit * (pageNumber - 1)

        val sortOrder = "${FavoriteContact.COLUMN_CREATED_AT} DESC"
        try {
            val cursor = db.query(
                FavoriteContact.TABLE_NAME,
                arrayOf(
                    FavoriteContact.COLUMN_ID,
                    FavoriteContact.COLUMN_NAME,
                    FavoriteContact.COLUMN_URL,
                    FavoriteContact.COLUMN_IMAGE_URL,
                    FavoriteContact.COLUMN_CHAPTER_ID,
                    FavoriteContact.COLUMN_CHAPTER_NAME,
                    FavoriteContact.COLUMN_CHAPTER_URL,
                    FavoriteContact.COLUMN_CREATED_AT,
                ),
                null,
                null,
                null,
                null,
                sortOrder,
            )
            with(cursor) {
                while (moveToNext()) {
                    val id = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_ID))
                    val name = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_NAME))
                    val url = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_URL))
                    val imageUrl = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_IMAGE_URL))
                    val chapterId = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_CHAPTER_ID))
                    val chapterName =
                        getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_CHAPTER_NAME))
                    val chapterUrl = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_CHAPTER_URL))
                    val createdAt = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_CREATED_AT))
                    histories.add(Favorite(id, name, url, imageUrl, chapterId, chapterName, chapterUrl,createdAt))
                }
                close()
            }
        }catch (e:Exception){
        }

        return histories
    }

    fun getById(id: String): Favorite? {
        val db = dbHelper.readableDatabase
        val selection = "${FavoriteContact.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id)
        var favorite: Favorite? = null

        try {
            val cursor = db.query(
                FavoriteContact.TABLE_NAME,
                arrayOf(
                    FavoriteContact.COLUMN_ID,
                    FavoriteContact.COLUMN_NAME,
                    FavoriteContact.COLUMN_URL,
                    FavoriteContact.COLUMN_IMAGE_URL,
                    FavoriteContact.COLUMN_CHAPTER_ID,
                    FavoriteContact.COLUMN_CHAPTER_NAME,
                    FavoriteContact.COLUMN_CHAPTER_URL,
                    FavoriteContact.COLUMN_CREATED_AT,
                ),
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            with(cursor) {
                if (moveToFirst()) {
                    val id = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_ID))
                    val name = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_NAME))
                    val url = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_URL))
                    val imageUrl = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_IMAGE_URL))
                    val chapterId = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_CHAPTER_ID))
                    val chapterName =
                        getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_CHAPTER_NAME))
                    val chapterUrl = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_CHAPTER_URL))
                    val createdAt = getString(getColumnIndexOrThrow(FavoriteContact.COLUMN_CREATED_AT))


                    favorite = Favorite(id, name, url, imageUrl, chapterId, chapterName, chapterUrl,createdAt)
                }
                close()
            }
        }catch (e :Exception){

        }

        return favorite
    }

    fun deleteFavoriteById(id: String) {
        val db = dbHelper.writableDatabase
        val selection = "${FavoriteContact.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id)
        db.delete(FavoriteContact.TABLE_NAME, selection, selectionArgs)
        db.close()
    }

    fun deleteAllFavorites() {
        val db = dbHelper.writableDatabase
        db.delete(FavoriteContact.TABLE_NAME, null, null)
        db.close()
    }
}