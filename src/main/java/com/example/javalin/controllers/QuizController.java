package com.example.javalin.controllers;

import com.example.javalin.models.Answer;
import com.example.javalin.models.Quiz;
import com.example.javalin.services.SRService;
import com.example.javalin.services.SpotifyService;
import io.javalin.http.Handler;

import java.util.ArrayList;

public class QuizController {
    //@todo skapa all logik för att hantera quiz

    private Quiz quiz;
    private SRService srService;
    private SpotifyService spotifyService;

    public QuizController(SRService srService, SpotifyService spotifyService) {
        this.srService = srService;
        this.spotifyService = spotifyService;
    }

    public Handler getStartQuiz = ctx -> {
        quiz = new Quiz();
        String currentSong = srService.fetchCurrentSong();
        Answer correctAnswer = new Answer(currentSong, true);
        quiz.addAnswer(correctAnswer);
        ArrayList<String> similarSongStrings = (ArrayList<String>) spotifyService.getSimilarSongs(currentSong);
        for (String text : similarSongStrings) {
            quiz.addAnswer(new Answer(text, false));
        }
        ctx.result(quiz.toString()); //@Todo: ta reda på hur listan med låtar ska returneras på rätt sätt.
    };
}
