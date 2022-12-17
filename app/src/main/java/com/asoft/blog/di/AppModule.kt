package com.asoft.blog.di

import android.content.Context
import com.asoft.blog.BlogApp
import com.asoft.blog.data.repository.BlogRepositoryImpl
import com.asoft.blog.domain.repository.BlogRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Singleton
    @Provides
    fun provideRepository(db: FirebaseFirestore): BlogRepository {
        return BlogRepositoryImpl(db)
    }

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext context: Context): BlogApp {
        return context as BlogApp
    }
}