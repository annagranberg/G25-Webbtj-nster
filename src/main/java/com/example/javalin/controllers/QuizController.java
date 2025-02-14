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

        String currentSong = srService.fetchCurrentSong(channelId);


        try {
            JSONObject obj = new JSONObject(currentSong);
            JSONObject playlist = obj.optJSONObject("playlist"); // Använd optJSONObject för att undvika undantag

            if (playlist == null) {
                ctx.result(gson.toJson(Map.of("Error", "Playlist saknas i svaret från Sveriges Radio")));
                return;
            }

            JSONObject song = playlist.optJSONObject("song"); // Använd optJSONObject för att undvika undantag
            if (song == null) {
                ctx.result(gson.toJson(Map.of("Error", "Ingen aktuell låt hittades i playlisten")));
                return;
            }

            String currentSongText = song.optString("title", ""); // Undvik null genom att ge en standardvärde
            if (currentSongText.isEmpty()) {
                ctx.result(gson.toJson(Map.of("Error", "Titel för den aktuella låten saknas")));
                return;
            }

            Answer correctAnswer = new Answer(currentSongText, true);
            quiz.addAnswer(correctAnswer);

            // Lägg till fler svarsalternativ
            ArrayList<String> songsFromSameAlbum = spotifyService.getSongsFromSameAlbum(currentSong);
            for (String text : songsFromSameAlbum) {
                quiz.addAnswer(new Answer(text, false));
            }

            if (quiz.getAnswers().size() < 3) {
                ArrayList<String> artistSongs = spotifyService.getArtistSongs(currentSong);
                for (String text : artistSongs) {
                    quiz.addAnswer(new Answer(text, false));
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
            if (answers.size() > 1) {
                Map<String, Object> result = new HashMap<>();
                result.put("Question", "Vad heter låten?");
                result.put("Answers", answers);
                ctx.result(gson.toJson(result));
            } else {
                Map<String, Object> result = new HashMap<>();
                String song1 = "Idas Sommarvisa";
                String song2 = "Här kommer Pippi Långstrump";
                answers.add(new Answer(song1, false));
                answers.add(new Answer(song2, false));
                result.put("Question", "Vad heter låten?");
                result.put("Answers", answers);
                ctx.result(gson.toJson(result));
                //ctx.result(gson.toJson(Map.of("Error", "Det gick inte att hitta tillräckligt med låtar för quizet.")));
            }
        } catch (Exception e) {
            ctx.result(gson.toJson(Map.of("Error", "Ett fel inträffade vid bearbetning av API-svaret: " + e.getMessage())));
        }
    };
}