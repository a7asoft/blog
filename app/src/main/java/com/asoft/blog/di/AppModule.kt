package com.asoft.blog.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.asoft.blog.BlogApp
import com.asoft.blog.data.local.BlogDao
import com.asoft.blog.data.local.BlogDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRealtimeDatabase(): DatabaseReference {
        val db = Firebase.database
        return db.getReference("posts")
    }

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext context: Context): BlogApp {
        return context as BlogApp
    }

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: BlogDatabase.Callback): BlogDatabase {
        return Room.databaseBuilder(application, BlogDatabase::class.java, "blog_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideArticleDao(db: BlogDatabase): BlogDao {
        return db.getArticleDao()
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope