package org.chupik.redditringtest;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private ArrayList<Post> posts;

    public PostsAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final ImageView thumbnail;
        private final TextView author;
        private final TextView comments;
        private final TextView date;
        private String url;
        private String imageUrl;


        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            author = itemView.findViewById(R.id.author);
            comments = itemView.findViewById(R.id.comments);
            date = itemView.findViewById(R.id.date);
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageUrl != null && imageUrl.startsWith("http")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                        try {
                            v.getContext().startActivity(intent);
                        } catch (ActivityNotFoundException e) {

                        }
                    }
                }
            });
        }

        public void setData(Post post){
            title.setText(post.title);
            author.setText(post.author);
            comments.setText(post.commentsNumber + "");
            date.setText(DateUtils.getRelativeTimeSpanString(post.date));
            if (post.thumbnail  != null && post.thumbnail.startsWith("http")) {
                thumbnail.setVisibility(View.VISIBLE);
                Picasso.with(thumbnail.getContext())
                        .load(post.thumbnail)
                        .into(thumbnail);
            } else {
                thumbnail.setVisibility(View.GONE);
                Log.d("thumbnail", post.thumbnail);
//                thumbnail.setImageResource(R.drawable.ic_launcher_background);
            }
            imageUrl = post.fullImage;
        }
    }
}
