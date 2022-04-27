package com.ifcompany.calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ifcompany.calculator.dao.HistoryDao
import com.ifcompany.calculator.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}