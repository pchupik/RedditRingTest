package org.chupik.redditringtest;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;


public class PostsDataSource extends ItemKeyedDataSource<String, Post> {

    private RedditApi reddit;
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    @Inject
    public PostsDataSource(RedditApi reddit) {
        this.reddit = reddit;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<Post> callback) {
//        isRefreshing.postValue(true);
        List<Post> posts = reddit.requestTopPosts(params.requestedLoadSize, null);
        callback.onResult(posts);
        isLoading.postValue(false);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<Post> callback) {
        List<Post> posts = reddit.requestTopPosts(params.requestedLoadSize, params.key);
        callback.onResult(posts);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<Post> callback) {
    }

    @NonNull
    @Override
    public String getKey(@NonNull Post item) {
        return item.name;
    }
}
