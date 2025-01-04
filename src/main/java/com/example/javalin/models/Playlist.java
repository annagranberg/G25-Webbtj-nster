package com.example.javalin.models;

import java.util.List;

public class Playlist {
    private String name;
    private List<Track> tracks;

    public Playlist(String name, List<Track> tracks) {
        this.name = name;
        this.tracks = tracks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
