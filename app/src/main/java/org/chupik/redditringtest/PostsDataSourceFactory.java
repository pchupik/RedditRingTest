package org.chupik.redditringtest;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import javax.inject.Inject;

public class PostsDataSourceFactory extends DataSource.Factory<String, Post> {

    RedditApi api;
    MutableLiveData<PostsDataSource> sourceMutableLiveData = new MutableLiveData<>();

    @Inject
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
