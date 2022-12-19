package com.asoft.blog.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.asoft.blog.data.remote.Post
import com.asoft.blog.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Post::class], version = 1)
abstract class BlogDatabase : RoomDatabase() {

    abstract fun getArticleDao(): BlogDao

    class Callback @Inject constructor(
        private val database: Provider<BlogDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}