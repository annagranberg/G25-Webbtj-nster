package com.example.javalin.controllers;

import com.example.javalin.models.Answer;
import com.example.javalin.models.Quiz;
import com.example.javalin.services.SRService;
import com.example.javalin.services.SpotifyService;
import com.google.gson.GsonBuilder;
import io.javalin.http.Handler;
import com.google.gson.Gson;
import org.json.JSONObject;
import static java.nio.charset.StandardCharsets.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizController {
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
                ctx.result(gson.toJson(Map.of("Error", "Invalid channel")));
                return;
        }

        String currentSong = srService.fetchCurrentSong(channelId);


        try {
            JSONObject obj = new JSONObject(currentSong);
            JSONObject playlist = obj.optJSONObject("playlist"); // Använd optJSONObject för att undvika undantag

            if (playlist == null) {
                ctx.result(gson.toJson(Map.of("Error", "Playlist missing from the response from Sveriges Radio")));
                return;
            }

            JSONObject song = playlist.optJSONObject("song"); // Använd optJSONObject för att undvika undantag
            if (song == null) {
                ctx.result(gson.toJson(Map.of("Error", "No active song found in playlist")));
                return;
            }

            String currentSongText = song.optString("title", ""); // Undvik null genom att ge en standardvärde
            if (currentSongText.isEmpty()) {
                ctx.result(gson.toJson(Map.of("Error", "Titel of the active song is missing")));
                return;
            }

            Answer correctAnswer = new Answer(currentSongText, true);
            quiz.addAnswer(correctAnswer);

            // Lägg till fler svarsalternativ
            ArrayList<String> songsFromSameAlbum = spotifyService.getSongsFromSameAlbum(currentSong);
            for (String text : songsFromSameAlbum) {
                if (!text.equals(correctAnswer.getText())) {
                    quiz.addAnswer(new Answer(text, false));
                }
            }

            if (quiz.getAnswers().size() < 3) {
                ArrayList<String> artistSongs = spotifyService.getArtistSongs(currentSong);
                for (String text : artistSongs) {
                    if (!text.equals(correctAnswer.getText())) {
                        quiz.addAnswer(new Answer(text, false));
                    }
                }
            }

            if (quiz.getAnswers().size() < 3) {
                ArrayList<String> similarSongs = spotifyService.getSimilarSongs(currentSong);
                for (String text : similarSongs) {
                    if (!text.equals(correctAnswer.getText())) {
                        quiz.addAnswer(new Answer(text, false));
                    }
                }
            }

            ArrayList<Answer> answers = quiz.getAnswers();
            if (answers.size() > 2) {
                Map<String, Object> result = new HashMap<>();
                result.put("Question", "What is the name of the song?");
                result.put("Answers", answers);
                ctx.result(gson.toJson(result));
            } else {
                Map<String, Object> result = new HashMap<>();
                String song1 = "Ida summer song";
                String song2 = "Pippi Longstocking";
                answers.add(new Answer(song1, false));
                answers.add(new Answer(song2, false));
                result.put("Question", "What is the name of the song?");
                result.put("Answers", answers);
                ctx.result(gson.toJson(result));
            }
        } catch (Exception e) {
            ctx.result(gson.toJson(Map.of("Error", "An error occurred while processing the API response: " + e.getMessage())));
        }
    };
}