package com.example.demotv.preseters

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.example.common.models.Articles

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder,
        item: Any
    ) {
        val articles = item as Articles

        viewHolder.title.text = articles.title
        viewHolder.subtitle.text = articles.content
        viewHolder.body.text = articles.description
    }
}