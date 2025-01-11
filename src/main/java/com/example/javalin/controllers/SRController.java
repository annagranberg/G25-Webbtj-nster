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
            case "/P1Playlist":
                channelId = "132";
                break;
            case "/P2PlayList":
                channelId = "163";
                break;
            case "/P3PlayList":
                channelId = "164";
                break;
            case "/P4PlayList":
                channelId = "207";
                break;
            default:
                return;
        }
        String currentSong = srService.fetchCurrentSong(channelId); // Hämtar data från service-klassen
        ctx.result(currentSong);
    };

    public Handler getCurrentSongForQuiz = ctx -> {
        String channelId;
        String endpoint = ctx.path();
        switch (endpoint) {
            case "/P1Playlist":
                channelId = "132";
                break;
            case "/P2Playlist":
                channelId = "163";
                break;
            case "/P3Playlist":
                channelId = "164";
                break;
            case "/P4Playlist":
                channelId = "207";
                break;
            default:
                return;
        }

        String currentSong = srService.fetchCurrentSong(channelId);
        ctx.result(currentSong);
    };
}
