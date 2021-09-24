package com.example.common.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.models.Articles
import com.example.common.models.Root
import com.example.common.data.ListItemDetails
import com.example.common.data.ListItemPopularNews
import com.example.common.data.ListItemTitle
import com.example.common.model.ListItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsViewModel : BaseViewModel() {

    val newsLists: MutableLiveData<ArrayList<ListItems>> by lazy {
        MutableLiveData<ArrayList<ListItems>>()
    }
    private val dataList = ArrayList<ListItems>()

    fun getViewListFromNewsFeed(root: Root, articles: Articles) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if(root.articles?.isNotEmpty() == true){
                    dataList.clear()
                    dataList.add(ListItemDetails(articles))
                    dataList.add(ListItemTitle("Popular News"))
                    root.articles.forEach{
                        dataList.add(ListItemPopularNews(it))
                    }
                }
                newsLists.postValue(dataList)

            }
        }
}