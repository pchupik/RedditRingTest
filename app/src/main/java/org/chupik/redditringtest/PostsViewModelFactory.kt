package org.chupik.redditringtest

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class PostsViewModelFactory @Inject constructor(private val postsViewModel: PostsViewModel) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = postsViewModel as T

}