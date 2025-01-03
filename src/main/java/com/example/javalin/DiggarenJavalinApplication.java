package com.example.javalin;

import com.example.javalin.controllers.Index;
import com.example.javalin.controllers.SRController;
import com.example.javalin.controllers.SpotifyController;
import com.example.javalin.services.SRService;
import com.example.javalin.services.SpotifyService;
import io.javalin.Javalin;

public class DiggarenJavalinApplication {
    public static void main(String[] args) {
        // Detta startar javalin-servern
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            });
            config.staticFiles.add("static");
        }).start(5008); // Servern körs på denna porten (5008)

        // Sveriges radio
        SRService srService = new SRService();
        SRController srController = new SRController(srService);
        Index indexController = new Index();

        //Spotify
        SpotifyService spotifyService = new SpotifyService();
        SpotifyController spotifyController = new SpotifyController(spotifyService);

        // Lägger till endpoints
        app.get("/", indexController.index); // Root endpoint
        app.get("/P1.html", indexController.getP1);
        app.get("/P2.html", indexController.getP2);
        app.get("/P3.html", indexController.getP3);
        app.get("/P4.html", indexController.getP4);
        app.get("/api/p3", srController.getP3Data);
    }
}