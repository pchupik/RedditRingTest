package org.chupik.redditringtest;

public class Post {

    String name;
    String title;
    String author;
    long date;
    String thumbnail;
    String fullImage;
    long commentsNumber;

    public Post(String name, String title, String author, long date, String thumbnail, String fullImage, long commentsNumber) {
        this.name = name;
        this.title = title;
        this.author = author;
        this.date = date;
        this.thumbnail = thumbnail;
        this.fullImage = fullImage;
        this.commentsNumber = commentsNumber;
    }
}
