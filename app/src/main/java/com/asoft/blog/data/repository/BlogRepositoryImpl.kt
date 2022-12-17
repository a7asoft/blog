package com.asoft.blog.data.repository

import com.asoft.blog.data.remote.Post
import com.asoft.blog.domain.repository.BlogRepository
import com.asoft.blog.utils.BaseResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BlogRepositoryImpl @Inject constructor(private val db: FirebaseFirestore) : BlogRepository {
    override suspend fun getPosts(): Flow<BaseResult<List<Post>, String, Exception>> {
        return flow {
            try {
                val task = db.collection("posts").get()
                val res = task.await()
                if (res != null) {
                    if (res.documents.isNotEmpty()) {
                        val docs = res.documents.map {
                            it.toObject(Post::class.java)!!
                        }
                        emit(BaseResult.Success(docs))
                    } else {
                        emit(BaseResult.Success(emptyList()))
                    }
                } else {
                    emit(BaseResult.Error("Error ocurred"))
                }
            } catch (e: Exception) {
                emit(BaseResult.Exception(e))
            }
        }
    }

    override suspend fun addPost(post: Post): Flow<BaseResult<String, String, Exception>> {
        return flow {
            try {
                val task = db.collection("posts").add(post)
                val res = task.await()
                if (res != null) {
                    if (res.id != "") {
                        emit(BaseResult.Success(res.id))
                    } else {
                        emit(BaseResult.Success(""))
                    }
                } else {
                    emit(BaseResult.Error("Error ocurred"))
                }
            } catch (e: Exception) {
                emit(BaseResult.Exception(e))
            }
        }
    }
}