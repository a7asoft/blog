package com.asoft.blog.domain.common

import com.asoft.blog.data.remote.Post
import com.asoft.blog.domain.repository.BlogRepository
import com.asoft.blog.utils.BaseResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddPostsUseCase @Inject constructor(private val blogRepository: BlogRepository){
    suspend operator fun invoke(post: Post): Flow<BaseResult<String, String, Exception>> {
        return blogRepository.addPost(post)
    }
}