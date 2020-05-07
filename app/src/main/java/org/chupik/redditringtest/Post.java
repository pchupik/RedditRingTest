package org.chupik.redditringtest;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Post {

    String name;
    String title;
    String author;
    @SerializedName("created_utc")
    long date;
    String thumbnail;
    @Nullable
    @SerializedName("url")
    String fullImage;
    @SerializedName("num_comments")
    long commentsNumber;
    String permalink;
    Preview preview;

    public Post(String name, String title, String author, long date, String thumbnail, @Nullable String fullImage, long commentsNumber, String permalink) {
        this.name = name;
        this.title = title;
        this.author = author;
        this.date = date;
        this.thumbnail = thumbnail;
        this.fullImage = fullImage;
        this.commentsNumber = commentsNumber;
        this.permalink = permalink;
    }

    public long getDateInMiliseconds() {
        return date * 1000;
    }

    public String getFullPermaLink() {
        return "https://www.reddit.com" + permalink;
    }

    @Nullable
    public String getFullImageIfEnabled() {
        if (preview != null && preview.getEnabled())
            return fullImage;
        else
            return null;
    }
}
