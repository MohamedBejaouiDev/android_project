package com.example.todo_app.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


// Data class matching the structure of an article in the API response
data class ApiArticle(
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("urlToImage") val urlToImage: String?,
    val source: Source?,
    val author: String?,
    val url: String?,
    val publishedAt: String?,
    val content: String?
)

data class Source(
    val id: String?,
    val name: String?
)

data class BlogResponse(
    val status: String?,
    val totalResults: Int?,
    val articles: List<ApiArticle>?
)


@Parcelize
data class BlogItem(
    val title: String,
    val description: String?,
    val imageUrl: String?,

    // extra details for the details page
    val sourceName: String?,
    val author: String?,
    val publishedAt: String?,
    val url: String?,
    val content: String?
) : Parcelable