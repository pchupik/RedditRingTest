package org.chupik.redditringtest;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PostViewModel viewModel = ViewModelProviders.of(this, new ViewModelFactory()).get(PostViewModel.class);

        PostsAdapter adapter = new PostsAdapter();
        viewModel.posts.observe(this, adapter::setList);

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipe = findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(viewModel::refresh);
        viewModel.isRefreshing.observe(this, swipe::setRefreshing);
    }

    private class ViewModelFactory implements ViewModelProvider.Factory {

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public ViewModel create(@NonNull Class modelClass) {
            return new PostViewModel(new RedditApi(new Prefs(MainActivity.this)));
        }
    }
}
