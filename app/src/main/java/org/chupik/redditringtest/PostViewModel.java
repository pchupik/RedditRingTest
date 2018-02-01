package org.chupik.redditringtest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;


public class PostViewModel extends ViewModel {

    private static final int PAGE_SIZE = 10;
    public final LiveData<PagedList<Post>> posts;
    private final PostsDataSourceFactory postsDataSourceFactory;
    public final LiveData<Boolean> isRefreshing;

    public PostViewModel(RedditApi api) {
        postsDataSourceFactory = new PostsDataSourceFactory(api);
        isRefreshing = Transformations.switchMap(postsDataSourceFactory.sourceMutableLiveData, __ -> __.isLoading);

        posts = new LivePagedListBuilder<>(postsDataSourceFactory, PAGE_SIZE)
                .build();
    }

    public void refresh(){
        postsDataSourceFactory.refresh();
    }

}
