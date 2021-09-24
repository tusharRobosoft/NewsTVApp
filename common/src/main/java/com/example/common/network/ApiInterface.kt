package com.example.common.network


import com.example.common.models.Root
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET("top-headlines?country=in&category=business&apiKey=cc570323ebad4f31b95a191ae3baa6e2")
    suspend fun getNews() : Response<Root>

}