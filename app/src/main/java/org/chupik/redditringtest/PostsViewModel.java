package org.chupik.redditringtest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import javax.inject.Inject;


public class PostsViewModel extends ViewModel {

    private static final int PAGE_SIZE = 10;
    public final LiveData<PagedList<Post>> posts;
    private final PostsDataSourceFactory postsDataSourceFactory;
    public final LiveData<Boolean> isRefreshing;

    @Inject
    public PostsViewModel(PostsDataSourceFactory factory) {
        postsDataSourceFactory = factory;
        isRefreshing = Transformations.switchMap(postsDataSourceFactory.sourceMutableLiveData, __ -> __.isLoading);

        posts = new LivePagedListBuilder<>(postsDataSourceFactory, PAGE_SIZE)
                .build();
    }

    public void refresh(){
        postsDataSourceFactory.refresh();
    }

}
