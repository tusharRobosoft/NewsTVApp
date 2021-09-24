package com.example.common.`interface`

import androidx.lifecycle.LiveData
import com.example.common.models.Articles
import com.example.common.models.Root
import com.example.demo.views.db.history.HistoryEntity

import retrofit2.Response

interface NewsFeedRepository {

    suspend fun getNewsFeed(): Response<Root>

    suspend fun upsertArticle(article: Articles) : Long

    fun getSavedNews() : LiveData<List<Articles>>

    suspend fun deleteArticle(article: Articles)

    suspend fun upsertHistory(value: HistoryEntity) : Long

    fun getHistory() : LiveData<List<HistoryEntity>>
}