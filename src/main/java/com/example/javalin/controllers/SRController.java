package com.example.javalin.controllers;

import com.example.javalin.models.CurrentSong;
import io.javalin.http.Handler;
import com.example.javalin.services.SRService;

public class SRController {

    private SRService srService;
    private SpotifyController spotifyController;

    // Konstruktor för att injicera SRService
    public SRController(SRService srService, SpotifyController spotifyController) {
        this.srService = srService;
        this.spotifyController = spotifyController;
    }

    // Endpoint för att hämta data från P3
    public Handler getP3PlayList = ctx -> {
        String currentSong = srService.fetchCurrentSong("164"); // Hämtar data från service-klassen
        ctx.result(currentSong);
    };

    public Handler getCurrentSongForQuiz = ctx -> {
        String currentSong = srService.fetchCurrentSong("164");
        ctx.result(currentSong); //@Todo: ta reda på hur listan med låtar ska returneras på rätt sätt.
    };
}
