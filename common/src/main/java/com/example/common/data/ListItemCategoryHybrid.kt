package com.example.common.data


import com.example.common.models.Articles
import com.example.common.model.ListItems

data class ListItemMainNews(
    val article: Articles
) : ListItems

data class ListItemPopularNews(
    val articles: Articles
) : ListItems


data class ListItemDetails(
    val articles: Articles
) : ListItems

data class ListItemTitle(
    val title: String?
) : ListItems

data class ListItemBottomPager(
    val title: String?
) : ListItems


