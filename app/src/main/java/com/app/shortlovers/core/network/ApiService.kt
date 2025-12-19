package com.app.shortlovers.core.network

import com.app.shortlovers.core.models.ApiResult
import com.app.shortlovers.core.models.MainResponse
import retrofit2.http.GET

interface ApiService {
    @GET("custom/pages/home")
    suspend fun getMainData(): ApiResult<List<MainResponse>>
}