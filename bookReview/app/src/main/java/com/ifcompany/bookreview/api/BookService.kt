package com.ifcompany.bookreview.api

import com.ifcompany.bookreview.model.BestSellerDto
import com.ifcompany.bookreview.model.SearchBooksDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {

    @GET("/api/search.api?output=json")
    fun getBooksByName(
            @Query("key") apikey: String,
            @Query("query") keyword: String,
    ): Call<SearchBooksDto>

    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBestSellerBooks(
            @Query("key") apikey: String
    ): Call<BestSellerDto>

}