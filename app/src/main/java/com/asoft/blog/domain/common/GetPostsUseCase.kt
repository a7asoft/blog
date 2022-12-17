package com.asoft.blog.domain.common

import com.asoft.blog.data.remote.Post
import com.asoft.blog.domain.repository.BlogRepository
import com.asoft.blog.utils.BaseResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(private val blogRepository: BlogRepository){
    suspend operator fun invoke(): Flow<BaseResult<List<Post>, String, Exception>> {
        return blogRepository.getPosts()
    }
}