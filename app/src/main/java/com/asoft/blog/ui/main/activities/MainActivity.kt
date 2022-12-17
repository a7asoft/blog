package com.asoft.blog.ui.main.activities

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asoft.blog.R
import com.asoft.blog.data.remote.Post
import com.asoft.blog.databinding.ActivityMainBinding
import com.asoft.blog.ui.main.adapter.PostsAdapter
import com.asoft.blog.ui.main.viewmodel.MainViewModel
import com.asoft.blog.ui.main.viewmodel.State
import com.asoft.blog.utils.FabExtendingOnScrollListener
import com.asoft.blog.utils.hideViewWithAnimation
import com.asoft.blog.utils.showViewWithAnimation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        listeners()

        setSupportActionBar(binding.toolbar)
    }

    private fun listeners() {
        binding.includedLayout.pullToRefresh.setOnRefreshListener {
            mainViewModel.getPosts()
        }

        binding.fab.setOnClickListener {
            val db = Firebase.firestore
            db.collection("posts")
                .add(Post())
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }

        }

        binding.includedLayout.rvPosts.addOnScrollListener(FabExtendingOnScrollListener(binding.fab))
    }

    private fun setupRecyclerView() {
        val mAdapter = PostsAdapter(mutableListOf(), this)
        mAdapter.setItemTapListener(object : PostsAdapter.OnItemTap {
            override fun onTap(post: Post) {

            }
        })


        binding.includedLayout.rvPosts.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    override fun onResume() {
        observe()
        mainViewModel.getPosts()
        super.onResume()
    }

    private fun observe(){
        observePosts()
        observeState()
    }

    private fun observeState() {
        mainViewModel.mStatePosts
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { state ->
                handleState(state)
            }
            .launchIn(lifecycle.coroutineScope)
    }

    private fun handleState(state: State) {
        when (state) {
            is State.IsLoading -> {
                if (state.isLoading) {
                    binding.includedLayout.pullToRefresh.hideViewWithAnimation()
                    binding.includedLayout.errorView.hideViewWithAnimation()
                    binding.includedLayout.emptyView.hideViewWithAnimation()
                    binding.includedLayout.shimmerView.showViewWithAnimation()
                } else {
                    binding.includedLayout.pullToRefresh.isRefreshing = false
                    binding.includedLayout.pullToRefresh.showViewWithAnimation()
                    binding.includedLayout.errorView.hideViewWithAnimation()
                    binding.includedLayout.emptyView.hideViewWithAnimation()
                    binding.includedLayout.shimmerView.hideViewWithAnimation()
                }
            }
            is State.ShowError -> {
                binding.includedLayout.pullToRefresh.isRefreshing = false
                binding.includedLayout.pullToRefresh.hideViewWithAnimation()
                binding.includedLayout.errorView.showViewWithAnimation()
                binding.includedLayout.emptyView.hideViewWithAnimation()
                binding.includedLayout.shimmerView.hideViewWithAnimation()
            }
            is State.Init -> {
                binding.includedLayout.pullToRefresh.isRefreshing = false
                binding.includedLayout.pullToRefresh.hideViewWithAnimation()
                binding.includedLayout.errorView.hideViewWithAnimation()
                binding.includedLayout.emptyView.hideViewWithAnimation()
                binding.includedLayout.shimmerView.showViewWithAnimation()
            }
            is State.ShowException -> {
                binding.includedLayout.pullToRefresh.isRefreshing = false
                binding.includedLayout.pullToRefresh.hideViewWithAnimation()
                binding.includedLayout.errorView.showViewWithAnimation()
                binding.includedLayout.emptyView.hideViewWithAnimation()
                binding.includedLayout.shimmerView.hideViewWithAnimation()
            }
        }

    }

    private fun observePosts() {
        mainViewModel.mPosts
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { data ->
                handlePostsResponse(data)
            }
            .launchIn(lifecycle.coroutineScope)
    }

    private fun handlePostsResponse(data: List<Post>) {
        if (data.isNotEmpty()) {
            binding.includedLayout.rvPosts.adapter?.let {
                if (it is PostsAdapter) {
                    it.updateList(data)
                }
            }
        } else {
            //TODO show empty view

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}