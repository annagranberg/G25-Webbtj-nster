package com.example.javalin.models;

public class Track {
    private String title;
    private String artist;
    private String album;
    private String spotifyUrl;

    public Track(String title, String artist, String album, String spotifyUrl) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.spotifyUrl = spotifyUrl;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }
}