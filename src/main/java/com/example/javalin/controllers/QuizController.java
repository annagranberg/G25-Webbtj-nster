package com.example.javalin.controllers;

import com.example.javalin.models.Answer;
import com.example.javalin.models.Quiz;
import com.example.javalin.services.SRService;
import com.example.javalin.services.SpotifyService;
import com.google.gson.GsonBuilder;
import io.javalin.http.Handler;
import com.google.gson.Gson;
import org.json.JSONArray;
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
        String currentSong = srService.fetchCurrentSong("164");

        JSONObject obj = new JSONObject(currentSong);
        JSONObject playlist = obj.getJSONObject("playlist");
        if(playlist != null){
            JSONObject song = playlist.getJSONObject("song");

        if (song != null){
            String currentSongText  = song.getString("title") + " - " + song.getString("artist");
            if (currentSongText != null && !currentSongText.isEmpty()) {
                Answer correctAnswer = new Answer(currentSongText, true);
                quiz.addAnswer(correctAnswer);

                ArrayList<String> similarSongStrings = spotifyService.getSongsFromSameAlbum(currentSong);
                for (String text : similarSongStrings) {
                    quiz.addAnswer(new Answer(text, false));
                }

                ArrayList<Answer> answers = quiz.getAnswers();
                Map<String, Object> result = new HashMap<>();
                result.put("Question", "Vad heter låten?");
                result.put("Answers", answers);
                ctx.result(gson.toJson(result));
            } else {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("Error", "Det gick inte att hämta låten från SR");
                ctx.result(gson.toJson(errorResult));
            }
            }
        }
    };
}
