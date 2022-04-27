package com.ifcompany.githubrepository.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ifcompany.githubrepository.data.dao.SearchHistoryDao
import com.ifcompany.githubrepository.data.entity.GithubRepoEntity

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao

}
