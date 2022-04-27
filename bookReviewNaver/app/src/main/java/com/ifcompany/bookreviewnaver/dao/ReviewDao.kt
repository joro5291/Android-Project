package com.ifcompany.bookreviewnaver.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifcompany.bookreviewnaver.model.Review

@Dao
interface ReviewDao {

    @Query("SELECT * FROM review WHERE isbn = :isbn")
    fun getOne(isbn: String): Review

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReview(review: Review)

}
