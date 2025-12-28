package com.example.todo_app.blog

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo_app.repos.BlogRepository
import com.example.todo_app.blog.network.BlogApiService

import com.example.todo_app.vm.BlogViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit Setup Object
object BlogModule {
    private const val BASE_URL = "https://newsapi.org/v2/" // Reverted to HTTPS

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val blogApiService: BlogApiService = retrofit.create(BlogApiService::class.java)

    private val blogRepository: BlogRepository = BlogRepository(blogApiService)

    // Factory to inject dependencies into the ViewModel
    fun provideBlogViewModelFactory(application: Application): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
                    return BlogViewModel(application, blogRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}