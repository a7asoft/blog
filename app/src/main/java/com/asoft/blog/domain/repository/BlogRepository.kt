package com.asoft.blog.domain.repository

import com.asoft.blog.data.remote.Post
import com.asoft.blog.utils.BaseResult
import kotlinx.coroutines.flow.Flow

interface BlogRepository {
    suspend fun getPosts(): Flow<BaseResult<List<Post>, String, Exception>>
    suspend fun addPost(post: Post): Flow<BaseResult<String, String, Exception>>
}