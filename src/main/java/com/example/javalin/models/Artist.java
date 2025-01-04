package com.example.javalin.models;

public class Artist {
    private String id;
    private String name;
    private int followers;

    public Artist(String id, String name, int followers) {
        this.id = id;
        this.name = name;
        this.followers = followers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
