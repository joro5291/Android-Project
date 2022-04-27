package com.ifcompany.bookreviewnaver.api

import com.ifcompany.bookreviewnaver.model.SearchBooksDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BookAPI {

    @GET("/v1/search/book.json")
    fun getBooksByName(
        @Header("X-Naver-Client-Id") id: String,
        @Header("X-Naver-Client-Secret") secretKey: String,
        @Query("query") keyword: String
    ): Call<SearchBooksDto>

}