package com.example.javalin.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class SpotifyService {
    private final String clientId = "DITTclientid";
    private final String clientSecret = "DITTclientsecret";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String accessToken;

    // Hämtar Spotify-access-token
    public String getAccessToken() {
        try {
            String apiUrl = "https://accounts.spotify.com/api/token";

            String authHeader = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Basic " + authHeader)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                this.accessToken = responseBody.split("\"access_token\":\"")[1].split("\"")[0];
                return this.accessToken;
            } else {
                throw new RuntimeException("Misslyckades med att hämta token: " + response.body());
            }

        } catch (Exception e) {
            throw new RuntimeException("Fel vid autentisering med Spotify: " + e.getMessage());
        }
    }

    // Hämtar rekommendationer från Spotify baserat på låttitel och artist från SR
    public List<String> getSimilarSongs(String srResponse) {
        if (accessToken == null) {
            accessToken = getAccessToken(); // Hämtar token om det inte finns
        }

        try {
            // Extraherar titel och artist från Sveriges Radio svaret
            JSONObject srJson = new JSONObject(srResponse);
            String trackTitle = srJson.getJSONObject("playlist").getJSONObject("song").getString("title");
            String artistName = srJson.getJSONObject("playlist").getJSONObject("song").getString("artist");

            // Bygger Spotify API-URL med den extraherade informationen
            String url = String.format("https://api.spotify.com/v1/search?q=artist:${encodeURIComponent(currentSongArtist)}&type=track&limit=10", trackTitle, artistName);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseRecommendations(response.body());
            } else {
                throw new Exception("Spotify API-förfrågan misslyckades med statuskod: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Returnerar en tom lista om något går fel
        }
    }

    // Parsar rekommendationerna från JSON-svaret
    private List<String> parseRecommendations(String responseBody) {
        List<String> recommendations = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray tracks = json.getJSONArray("tracks");

            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                String trackName = track.getString("name");
                recommendations.add(trackName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recommendations;
    }
}
