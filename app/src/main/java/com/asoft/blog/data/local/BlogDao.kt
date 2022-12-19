package com.asoft.blog.data.local

import androidx.room.*
import com.asoft.blog.data.remote.Post

@Dao
interface BlogDao {
    @Query("SELECT * FROM post")
    suspend fun getArticles() : List<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(posts: List<Post>)

    @Delete
    suspend fun delete(post: Post)

    @Query("DELETE FROM post")
    suspend fun deleteAllPosts()
}