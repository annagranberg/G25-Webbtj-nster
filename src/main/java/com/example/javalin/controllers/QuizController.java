package com.example.javalin.controllers;

import com.example.javalin.models.Answer;
import com.example.javalin.models.CurrentSong;
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
        String channelId = ctx.pathParam("channelId"); // Hämtar channelId från URL

        switch (channelId) {
            case "1":
                channelId = "132";
                break;
            case "2":
                channelId = "163";
                break;
            case "3":
                channelId = "164";
                break;
            case "4":
                channelId = "207";
                break;
            default:
                ctx.result(gson.toJson(Map.of("Error", "Ogiltig kanal")));
                return;
        }

        //hämta aktuell låt fr SRsrvice
        CurrentSong currentSong = srService.getCurrentSong();
        //kontrollera om vi har en aktuell låt
        if (currentSong == null) {
            ctx.result(gson.toJson(Map.of("Error", "Ingen låt spelas just nu.")));
            return;
        }

        String currentSongText = currentSong.getTitle();
        long startTime = currentSong.getStartTime();
        //kontrollera att titel finns
        if (currentSongText == null || currentSongText.isEmpty()) {
            ctx.result(gson.toJson(Map.of("Error", "Titel för den aktuella låten saknas.")));
            return;
        }
        Answer correctAnswer = new Answer(currentSongText, true);
        quiz.addAnswer(correctAnswer);

        // Lägg till fler svarsalternativ
        ArrayList<String> songsFromSameAlbum = spotifyService.getSongsFromSameAlbum(currentSong.getTitle());
        for (String text : songsFromSameAlbum) {
            quiz.addAnswer(new Answer(text, false));
        }

        if (quiz.getAnswers().size() < 3) {
            ArrayList<String> artistSongs = spotifyService.getArtistSongs(currentSong.getTitle());
            for (String text : artistSongs) {
                quiz.addAnswer(new Answer(text, false));
            }
        }

        if (quiz.getAnswers().size() < 3) {
            ArrayList<String> similarSongs = spotifyService.getSimilarSongs(currentSong.getTitle());
            for (String text : similarSongs) {
                if (!text.equals(correctAnswer.getText())) {
                    quiz.addAnswer(new Answer(text, false));
                }
            }
        }

        ArrayList<Answer> answers = quiz.getAnswers();
        if (answers.size() > 1) {
            Map<String, Object> result = new HashMap<>();
            result.put("Question", "Vad heter låten?");
            result.put("Answers", answers);
            ctx.result(gson.toJson(result));
        } else {
            ctx.result(gson.toJson(Map.of("Error", "Det gick inte att hitta tillräckligt med låtar för quizet.")));
        }
    };
}