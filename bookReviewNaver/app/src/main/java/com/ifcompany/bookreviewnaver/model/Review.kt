package com.ifcompany.bookreviewnaver.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Review(
    @PrimaryKey val isbn: String,
    @ColumnInfo(name = "review") val review: String?
)
