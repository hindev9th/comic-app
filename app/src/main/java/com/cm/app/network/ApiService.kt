package com.cm.app.network

import com.cm.app.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiService {
    companion object {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(Constants.BASE_COMIC_URL)
            .build()
    }

}