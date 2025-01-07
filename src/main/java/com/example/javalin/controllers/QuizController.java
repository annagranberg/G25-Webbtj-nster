package com.example.javalin.controllers;

import com.example.javalin.models.Answer;
import com.example.javalin.models.Quiz;
import com.example.javalin.services.SRService;
import com.example.javalin.services.SpotifyService;
import com.google.gson.GsonBuilder;
import io.javalin.http.Handler;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizController {
    //@todo skapa all logik för att hantera quiz

    private Quiz quiz;
    private SRService srService;
    private SpotifyService spotifyService;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public QuizController(SRService srService, SpotifyService spotifyService) {
        this.srService = srService;
        this.spotifyService = spotifyService;
    }

    public Handler getStartQuiz = ctx -> {
        quiz = new Quiz();
        String currentSong = srService.fetchCurrentSong();
        Answer correctAnswer = new Answer(currentSong.toString(), true);
        quiz.addAnswer(correctAnswer);
        ArrayList<String> similarSongStrings = spotifyService.getSimilarSongs(currentSong.toString());
        for (String text : similarSongStrings) {
            quiz.addAnswer(new Answer(text, false));
        }

        ArrayList<Answer> answers = quiz.getAnswers();
        Map<String, Object> result = new HashMap<>();
        result.put("Question", "Vad heter låten?");
        result.put("Answers", answers);
        //ctx.result(quiz.toString()); //@Todo: ta reda på hur listan med låtar ska returneras på rätt sätt.
        ctx.result(gson.toJson(result));
    };
}
