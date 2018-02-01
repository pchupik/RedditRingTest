package org.chupik.redditringtest;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

public class PostsDataSourceFactory implements DataSource.Factory<String, Post> {

    RedditApi api;
    MutableLiveData<TopDataSource> sourceMutableLiveData = new MutableLiveData<>();

    public PostsDataSourceFactory(RedditApi api) {
        this.api = api;
    }

    @Override
    public DataSource<String, Post> create() {
        TopDataSource dataSource = new TopDataSource(api);
        sourceMutableLiveData.postValue(dataSource);
        return dataSource;
    }

    public void refresh(){
        TopDataSource dataSource = sourceMutableLiveData.getValue();
        if (dataSource != null)
            dataSource.invalidate();
    }
}
