package com.asoft.blog.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asoft.blog.data.remote.Post
import com.asoft.blog.domain.common.GetPostsUseCase
import com.asoft.blog.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : ViewModel() {

    //regions flow
    private val _posts = MutableStateFlow(listOf<Post>())
    val mPosts: Flow<List<Post>> get() = _posts

    //observe state get regions
    private val _postsState = MutableStateFlow<State>(
        State.Init
    )
    val mStatePosts: StateFlow<State> get() = _postsState

    //observable methods
    private fun setLoading() {
        _postsState.value = State.IsLoading(true)
    }

    private fun hideLoading() {
        _postsState.value = State.IsLoading(false)
    }

    private fun showError(error: String) {
        _postsState.value = State.ShowError(error)
    }

    private fun showException(error: Exception) {
        _postsState.value = State.ShowException(error)
    }

    fun getPosts() {
        viewModelScope.launch {
            getPostsUseCase.invoke()
                .onStart {
                    setLoading()
                }
                .catch {
                    hideLoading()
                    showException(it as Exception)
                }
                .collect { result ->
                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> {
                            _posts.tryEmit(result.data)
                        }
                        is BaseResult.Error -> {
                            showError(result.rawResponse ?: "")
                        }
                        is BaseResult.Exception -> {
                            showException(result.exception)
                        }
                    }
                }
        }
    }
}

sealed class State {
    object Init : State()
    data class IsLoading(val isLoading: Boolean) : State()
    data class ShowError(val error: String) : State()
    data class ShowException(val error: Exception) : State()
}
