package com.example.javalin;

import com.example.javalin.controllers.Index;
import com.example.javalin.controllers.QuizController;
import com.example.javalin.controllers.SRController;
import com.example.javalin.controllers.SpotifyController;
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

        app.get("/", indexController.index); // Root endpoint
        //Returnerar HTML-sidan för en specifik kanal
        app.get("/P1.html", indexController.getP1);
        app.get("/P2.html", indexController.getP2);
        app.get("/P3.html", indexController.getP3);
        app.get("/P4.html", indexController.getP4);

        //För att visa låt som spelas + föregående låt
        app.get("/P1PlayList", srController.getPlaylist);
        app.get("/P2PlayList", srController.getPlaylist);
        app.get("/P3PlayList", srController.getPlaylist);
        app.get("/P4PlayList", srController.getPlaylist);

        //För att starta quiz
        app.post("/startQuizP1", quizController.getStartQuiz);
        app.post("/startQuizP2", quizController.getStartQuiz);
        app.post("/startQuizP3", quizController.getStartQuiz);
        app.post("/startQuizP4", quizController.getStartQuiz);
    }
}