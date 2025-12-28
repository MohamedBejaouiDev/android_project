package com.example.todo_app.repos

import com.example.todo_app.blog.network.BlogApiService
import com.example.todo_app.data.BlogItem
import com.example.todo_app.data.TodoCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BlogRepository(
    private val apiService: BlogApiService
) {

    private val apiKey = "763459de5fa24b939cc3cfc3d829cb4a"

    suspend fun fetchBlogs(category: TodoCategory): Result<List<BlogItem>> =
        withContext(Dispatchers.IO) {
            try {
                val apiCategory = category.toNewsApiCategory()

                // Using top-headlines (NO SEARCH QUERY)
                val response = apiService.getTopHeadlines(
                    apiKey = apiKey,
                    country = "us",
                    category = apiCategory,
                    pageSize = 30
                )

                if (!response.isSuccessful) {
                    val raw = response.errorBody()?.string()
                    return@withContext Result.failure(
                        Exception("HTTP ${response.code()} ${response.message()} ${raw ?: ""}".trim())
                    )
                }

                val body = response.body()
                    ?: return@withContext Result.failure(Exception("Empty response body"))

                val items: List<BlogItem> = body.articles
                    .orEmpty()
                    .mapNotNull { a ->
                        val title = a.title?.trim()
                        if (title.isNullOrBlank()) return@mapNotNull null

                        BlogItem(
                            title = title,
                            description = a.description?.trim(),
                            imageUrl = a.urlToImage?.toHttpsUrl(),
                            sourceName = a.source?.name,
                            author = a.author,
                            publishedAt = a.publishedAt,
                            url = a.url,
                            content = a.content
                        )
                    }

                Result.success(items)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun TodoCategory.toNewsApiCategory(): String = when (this) {
        TodoCategory.WORK -> "business"
        TodoCategory.STUDY -> "technology"
        TodoCategory.GYM -> "health"
        TodoCategory.OTHERS -> "general"
    }

    private fun String.toHttpsUrl(): String =
        if (startsWith("http://")) replaceFirst("http://", "https://") else this
}
