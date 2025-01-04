package com.example.javalin.models;

public class Track {
    private String id;
    private String titleName;
    private String artistName;
    private String albumName;
    private String spotifyUrl;

    public Track(String id, String title, String artist, String album, String spotifyUrl) {
        this.id = id;
        this.titleName = title;
        this.artistName = artist;
        this.albumName = album;
        this.spotifyUrl = spotifyUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }
}