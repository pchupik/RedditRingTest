package org.chupik.redditringtest;

import androidx.annotation.Nullable;

public class Post {

    String name;
    String title;
    String author;
    long date;
    String thumbnail;
    @Nullable
    String fullImage;
    long commentsNumber;
    String permalink;

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
}
