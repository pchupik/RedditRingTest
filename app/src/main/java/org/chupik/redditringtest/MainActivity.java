package org.chupik.redditringtest;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import org.chupik.redditringtest.di.MyApp;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity {

    @Inject
    PostsViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((MyApp)getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PostsViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(PostsViewModel.class);

        PostsAdapter adapter = new PostsAdapter();
        viewModel.posts.observe(this, adapter::setList);

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipe = findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(viewModel::refresh);
        viewModel.isRefreshing.observe(this, swipe::setRefreshing);
    }
}
