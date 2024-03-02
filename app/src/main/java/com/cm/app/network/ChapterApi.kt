package com.cm.app.network


object ChapterApi {
    val retrofitService = ApiService.retrofit.create(IChapterApiService::class.java)
}