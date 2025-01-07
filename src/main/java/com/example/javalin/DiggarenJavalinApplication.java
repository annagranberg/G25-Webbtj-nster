package com.example.javalin;

import com.example.javalin.controllers.Index;
import com.example.javalin.controllers.SRController;
import com.example.javalin.controllers.SpotifyController;
import com.example.javalin.services.SRService;
import com.example.javalin.services.SpotifyService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.util.List;

public class DiggarenJavalinApplication {
    public static void main(String[] args) {
        // Detta startar javalin-servern
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
                config.staticFiles.add("/static", Location.CLASSPATH);

            });
        }).start(5008); // Servern körs på denna porten (5008)

        // Spotify
        SpotifyService spotifyService = new SpotifyService();
        SpotifyController spotifyController = new SpotifyController(spotifyService);

        // Sveriges radio
        SRService srService = new SRService();
        SRController srController = new SRController(srService, spotifyController);
        Index indexController = new Index();

        // Lägger till endpoints
        app.get("/", indexController.index); // Root endpoint
        app.get("/P1.html", indexController.getP1);
        app.get("/P2.html", indexController.getP2);
        app.get("/P3.html", indexController.getP3);
        app.get("/P4.html", indexController.getP4);
        app.get("/P3PlayList", srController.getP3PlayList);
        app.get("/P3SongQuiz", srController.getCurrentSongForQuiz);
       // app.get("/P3SpotifySongs", spotifyController.getSpotifyService().getRecommendations(srService.fetchCurrentSong()));
        app.get("/P3SpotifySongs", ctx -> {
            // Hämta den aktuella låtlistan från Sveriges Radio
            String srResponse = srService.fetchCurrentSong();

            // Hämta Spotify-rekommendationer baserat på Sveriges Radio-låten
            List<String> recommendations = spotifyController.getSpotifyService().getSimilarSongs(srResponse);

            // Skicka tillbaka rekommendationerna som ett JSON-svar
            ctx.json(recommendations);
        });

        // @todo: skapa endpoint för att starta quiz.
        // app.get("/startQuiz", );
    }
}