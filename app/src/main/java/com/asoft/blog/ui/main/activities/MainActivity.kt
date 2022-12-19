package com.asoft.blog.ui.main.activities

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.asoft.blog.R
import com.asoft.blog.data.remote.Post
import com.asoft.blog.databinding.ActivityMainBinding
import com.asoft.blog.ui.addpost.fragment.AddPostBS
import com.asoft.blog.ui.main.adapter.PostsAdapter
import com.asoft.blog.ui.main.viewmodel.MainViewModel
import com.asoft.blog.ui.main.viewmodel.Status
import com.asoft.blog.utils.FabExtendingOnScrollListener
import com.asoft.blog.utils.NetworkStateManager
import com.asoft.blog.utils.hideViewWithAnimation
import com.asoft.blog.utils.showViewWithAnimation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    //Network state observer
    private val activeNetworkStateObserver: Observer<Boolean> =
        Observer { isConnected -> handleConnection(isConnected) }

    private fun handleConnection(connected: Boolean?) {
        mainViewModel.setInternet(connected!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerInternetObserver()
        setupRecyclerView()
        observe()
        observeStatus()
        getPosts()
        listeners()
        setSupportActionBar(binding.toolbar)
    }

    private fun registerInternetObserver() {
        //Network state observe
        NetworkStateManager.instance?.networkConnectivityStatus
            ?.observe(this, activeNetworkStateObserver)
    }

    private fun listeners() {
        binding.fab.setOnClickListener {
            val addPostBS: BottomSheetDialogFragment = AddPostBS()
            addPostBS.show(supportFragmentManager, addPostBS.tag)
        }

        binding.includedLayout.addPost.setOnClickListener {
            val addPostBS: BottomSheetDialogFragment = AddPostBS()
            addPostBS.show(supportFragmentManager, addPostBS.tag)
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

    private fun observeStatus() {
        mainViewModel.status.observe(this) { status ->
            when (status) {
                Status.ERROR -> {
                    binding.includedLayout.errorView.showViewWithAnimation()
                }
                Status.EMPTY -> {
                    //show alert
                    showEmptyView()
                }
                else -> {}
            }
        }

        mainViewModel.internet.observe(this) { status ->
            when (status) {
                true -> {
                    binding.includedLayout.errorView.hideViewWithAnimation()
                    binding.fab.showViewWithAnimation()
                    binding.includedLayout.addPost.showViewWithAnimation()
                }
                false -> {
                    binding.includedLayout.errorView.showViewWithAnimation()
                    binding.fab.hideViewWithAnimation()
                    binding.includedLayout.addPost.hideViewWithAnimation()
                }
                else -> {}
            }
        }
    }

    private fun getPosts() {
        binding.includedLayout.shimmerView.showViewWithAnimation()
        binding.includedLayout.rvPosts.hideViewWithAnimation()
        binding.includedLayout.emptyView.hideViewWithAnimation()
        mainViewModel.getPosts()
    }

    private fun observe() {
        mainViewModel.posts.observe(this) { response ->
            binding.includedLayout.shimmerView.hideViewWithAnimation()
            if (response != null) {
                if (response.isNotEmpty()) {
                    showData()
                    binding.includedLayout.rvPosts.adapter?.let {
                        if (it is PostsAdapter) {
                            it.updateList(response)
                        }
                    }
                }
            }
        }
    }

    private fun showData() {
        binding.includedLayout.rvPosts.showViewWithAnimation()
        binding.includedLayout.emptyView.hideViewWithAnimation()
    }

    private fun showEmptyView() {
        binding.includedLayout.rvPosts.hideViewWithAnimation()
        binding.includedLayout.errorView.hideViewWithAnimation()
        binding.includedLayout.emptyView.showViewWithAnimation()
        binding.includedLayout.shimmerView.hideViewWithAnimation()
        binding.fab.hideViewWithAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    mainViewModel.filterData(query)
                } else {
                    mainViewModel.filterData("")
                }
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s.isNotEmpty()) {
                    mainViewModel.filterData(s)
                } else {
                    mainViewModel.filterData("")
                }
                return false
            }
        })

        searchView.setOnCloseListener {
            mainViewModel.filterData("")
            false
        }
        return super.onCreateOptionsMenu(menu)
    }

    /*override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        Log.d("*******", "onMenuItemActionExpand")
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        Log.d("*******", "onMenuItemActionCollapse")
        return true
    }*/


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}