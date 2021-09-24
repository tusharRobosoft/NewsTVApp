package com.example.common.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.common.interfaces.DashBoardListener
import com.example.common.model.ListItems


abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(data: ListItems)
    abstract fun setListener(listener: DashBoardListener)
}