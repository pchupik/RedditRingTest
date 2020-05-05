package org.chupik.redditringtest;

import androidx.paging.PagedListAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Objects;


public class PostsAdapter extends PagedListAdapter<Post, PostsAdapter.ViewHolder> {

    protected PostsAdapter() {
        super(new PostComparator());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final ImageView thumbnail;
        private final TextView author;
        private final TextView comments;
        private final TextView date;
        private String url;
        private String imageUrl;


        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            author = itemView.findViewById(R.id.author);
            comments = itemView.findViewById(R.id.comments);
            date = itemView.findViewById(R.id.date);
            thumbnail.setOnClickListener(v -> openUrl(imageUrl, v));
            title.setOnClickListener(v -> openUrl(url, v));
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
            }
            imageUrl = post.fullImage;
            url = post.permalink;
        }
    }

    public static class PostComparator extends DiffUtil.ItemCallback<Post>{

        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.name.equals(newItem.name);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return Objects.deepEquals(oldItem, newItem);
        }
    }

    private static void openUrl(String url, View v){
        if (url != null && url.startsWith("http")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            try {
                v.getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(v.getContext(), R.string.unable_to_open_url, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
