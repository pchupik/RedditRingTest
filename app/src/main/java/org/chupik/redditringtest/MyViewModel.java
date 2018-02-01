package org.chupik.redditringtest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;


public class MyViewModel extends ViewModel {

    public final LiveData<PagedList<Post>> posts;
    private final PostsDataSourceFactory postsDataSourceFactory;
    public final LiveData<Boolean> isRefreshing;

    public MyViewModel(RedditApi api) {
        postsDataSourceFactory = new PostsDataSourceFactory(api);
        isRefreshing = Transformations.switchMap(postsDataSourceFactory.sourceMutableLiveData, __ -> __.isLoading);

        posts = new LivePagedListBuilder<>(postsDataSourceFactory, 50)
                .build();
    }

    public void refresh(){
        postsDataSourceFactory.refresh();
    }

}
