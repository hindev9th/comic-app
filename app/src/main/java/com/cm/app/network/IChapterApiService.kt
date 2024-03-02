package com.cm.app.network

import com.cm.app.models.ChapterResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IChapterApiService {
    @GET("Comic/Services/ComicService.asmx/ProcessChapterList")
    suspend fun getChapter(@Query("comicId") id: String) : Response<String>
}