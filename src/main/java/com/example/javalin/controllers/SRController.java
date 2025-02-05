package com.example.javalin.controllers;

import io.javalin.http.Handler;
import com.example.javalin.services.SRService;

public class SRController {

    private SRService srService;
    private SpotifyController spotifyController;

    // Konstruktor för att starta SRService
    public SRController(SRService srService, SpotifyController spotifyController) {
        this.srService = srService;
        this.spotifyController = spotifyController;
    }

    // Endpoint för att hämta data från P3
    public Handler getPlaylist = ctx -> {
        String channelId;
        String endpoint = ctx.path();
        switch (endpoint) {
            case "/channels/1/playlist":
                channelId = "132";
                break;
            case "/channels/2/playlist":
                channelId = "163";
                break;
            case "/channels/3/playlist":
                channelId = "164";
                break;
            case "/channels/4/playlist":
                channelId = "207";
                break;
            default:
                return;
        }
        String currentSong = srService.fetchCurrentSong(channelId); // Hämtar data från service-klassen
        ctx.result(currentSong);
    };
}
