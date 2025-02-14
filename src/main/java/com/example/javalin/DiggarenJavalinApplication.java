package com.example.javalin;

import com.example.javalin.controllers.Index;
import com.example.javalin.controllers.QuizController;
import com.example.javalin.controllers.SRController;
import com.example.javalin.controllers.SpotifyController;
import com.example.javalin.models.CurrentSong;
import com.example.javalin.services.SRService;
import com.example.javalin.services.SpotifyService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

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

        // Quiz
        QuizController quizController = new QuizController(srService, spotifyService);

        app.get("/", indexController.index); // route

        app.get("/channels", indexController.getChannels); // route

        app.get("/channels/{channelId}", ctx -> { //routes
            String channelId = ctx.pathParam("channelId");
            System.out.println(channelId);
            switch (channelId) {
                case "1":
                    indexController.getP1.handle(ctx);
                    break;
                case "2":
                    indexController.getP2.handle(ctx);
                    break;
                case "3":
                    indexController.getP3.handle(ctx);
                    break;
                case "4":
                    indexController.getP4.handle(ctx);
                    break;
                default:
                    ctx.status(404).result("Channel not found");
                    break;
            }
        });

        //ENDPOINT
        app.get("/channels/{channelId}/playlist", ctx -> {
            srController.getPlaylist.handle(ctx);

        });

        //ENDPOINT
        app.post("/channels/{channelId}/quiz", ctx -> {
            quizController.getStartQuiz.handle(ctx);
        });

    }
}