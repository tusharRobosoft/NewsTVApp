package com.example.common.interfaces

import com.example.common.models.Articles

interface DashBoardListener {
    fun onClickBookmark(article: Articles)
    fun onClickArticle(article: Articles)
}