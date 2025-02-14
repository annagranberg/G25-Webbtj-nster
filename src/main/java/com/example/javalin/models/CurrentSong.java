package com.example.javalin.models;

public class CurrentSong {
    private String title;
    public String artist;
    private long startTime; //variabel för en låts starttid i millisekunder
    public Quiz quiz;


    public CurrentSong(String title, String artist, long startTime) {
        this.title = title;
        this.artist = artist;
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return String.format("Song {title: '%s', artist: '%s'}", title, artist);
    }
}
