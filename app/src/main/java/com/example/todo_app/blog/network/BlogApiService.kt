package com.example.todo_app.blog.network

import com.example.todo_app.data.BlogResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BlogApiService {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("apiKey") apiKey: String,
        @Query("country") country: String = "us",
        @Query("category") category: String,
        @Query("pageSize") pageSize: Int = 30
    ): Response<BlogResponse>
}
