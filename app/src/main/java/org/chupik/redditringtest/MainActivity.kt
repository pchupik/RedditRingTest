package org.chupik.redditringtest

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import org.chupik.redditringtest.di.MyApp
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: PostsViewModelFactory

    private val viewModel by viewModels<PostsViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PostsAdapter()
        viewModel.posts.observe(this, Observer { adapter.submitList(it) })
        list.adapter = adapter
        swipe_refresh.setOnRefreshListener { viewModel.refresh() }
        viewModel.isRefreshing.observe(this, Observer { swipe_refresh.isRefreshing = it })
    }
}