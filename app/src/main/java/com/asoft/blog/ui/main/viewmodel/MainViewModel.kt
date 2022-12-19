package com.asoft.blog.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asoft.blog.data.local.BlogDao
import com.asoft.blog.data.remote.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

enum class Status { LOADING, SUCCESS, ERROR, EMPTY }

@HiltViewModel
class MainViewModel @Inject constructor(
    private val reference: DatabaseReference,
    private val blogDao: BlogDao
) : ViewModel() {

    private var _tempList: List<Post> = emptyList()

    private val _title = MutableStateFlow("")
    private val _description = MutableStateFlow("")

    private var _internet = MutableLiveData(true)
    val internet: LiveData<Boolean> get() = _internet

    private var _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status> get() = _status

    fun setTitle(data: String) {
        _title.value = data
    }

    fun setInternet(data: Boolean) {
        _internet.value = data
    }

    fun setDescription(data: String) {
        _description.value = data
    }

    val titleMessages = combine(_title) { title ->
        val isTitleCorrect = title.isNotEmpty()
        return@combine if (!isTitleCorrect) "Este campo es obligatorio" else ""
    }

    val descMessages = combine(_description) { description ->
        val isDescCorrect = description.isNotEmpty()
        return@combine if (!isDescCorrect) "Este campo es obligatorio" else ""
    }

    val isSubmitEnabled =
        combine(_title, _description) { title, description ->
            val isTitleCorrect = title.isNotEmpty()
            val isDescriptionCorrect = description.isNotEmpty()

            return@combine isTitleCorrect and isDescriptionCorrect
        }

    fun getPosts() {
        viewModelScope.launch {
            val dataFetchEventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val list = mutableListOf<Post>()
                    for (snap in dataSnapshot.children) {
                        list.add(snap.getValue(Post::class.java)!!)
                    }

                    if (list.size == 0) {
                        _status.value = Status.EMPTY
                    }


                    //remove all from db and add this
                    CoroutineScope(Dispatchers.Default).launch {
                        blogDao.deleteAllPosts()
                        for (item in list) {
                            val resul = blogDao.insert(item)
                        }
                        val blogResultDB = blogDao.getArticles()
                        withContext(Dispatchers.Main) {
                            _posts.value = blogResultDB
                            _tempList = blogResultDB
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    _status.value = Status.ERROR
                }
            }

            if (_internet.value == true) {
                reference.addValueEventListener(dataFetchEventListener)
                delay(5000L)
                if (_internet.value == true) {
                    CoroutineScope(Dispatchers.Default).launch {
                        val blogResultDB = blogDao.getArticles()
                        withContext(Dispatchers.Main) {
                            if (blogResultDB.isEmpty() && _posts.value?.isEmpty() == true) {
                                reference.removeEventListener(dataFetchEventListener)
                                _status.value = Status.EMPTY
                            }
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.Default).launch {
                        val blogResultDB = blogDao.getArticles()
                        withContext(Dispatchers.Main) {
                            _posts.value = blogResultDB
                        }
                    }
                    reference.removeEventListener(dataFetchEventListener)
                }
            } else {
                CoroutineScope(Dispatchers.Default).launch {
                    val blogResultDB = blogDao.getArticles()
                    withContext(Dispatchers.Main) {
                        _posts.value = blogResultDB
                    }
                }
                reference.removeEventListener(dataFetchEventListener)
            }
        }
    }

    fun addPost(post: Post) {
        _status.value = Status.LOADING
        viewModelScope.launch {
            reference.push().setValue(post).addOnCompleteListener {
                if (it.isSuccessful) {
                    _status.value = Status.SUCCESS
                } else {
                    _status.value = Status.ERROR
                }
            }.addOnFailureListener {
                _status.value = Status.ERROR
            }
        }
    }

    fun filterData(s: String) {
        if (s == "") {
            _posts.value = _tempList
        } else {
            val filtered = _posts.value?.filter {
                it.title!!.lowercase().contains(s.lowercase()) or it.description!!.lowercase()
                    .contains(s.lowercase())
            }

            if (filtered != null) {
                if (filtered.isNotEmpty()) {
                    _posts.value = filtered!!
                } else {
                    _posts.value = emptyList()
                }
            } else {
                _posts.value = emptyList()
            }
        }
    }
}
