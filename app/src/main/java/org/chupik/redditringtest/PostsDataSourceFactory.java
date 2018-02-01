package org.chupik.redditringtest;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

public class PostsDataSourceFactory implements DataSource.Factory<String, Post> {

    RedditApi api;
    MutableLiveData<PostsDataSource> sourceMutableLiveData = new MutableLiveData<>();

    public PostsDataSourceFactory(RedditApi api) {
        this.api = api;
    }

    @Override
    public DataSource<String, Post> create() {
        PostsDataSource dataSource = new PostsDataSource(api);
        sourceMutableLiveData.postValue(dataSource);
        return dataSource;
    }

    public void refresh(){
        PostsDataSource dataSource = sourceMutableLiveData.getValue();
        if (dataSource != null)
            dataSource.invalidate();
    }
}
