package com.example.javalin.controllers;

import com.example.javalin.services.SpotifyService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class SpotifyController {
    private SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    public SpotifyService getSpotifyService() {
        return spotifyService;
    }
}
