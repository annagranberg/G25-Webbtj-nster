package com.example.javalin.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SpotifyService {
    private final String clientId = "0dfbaadbec2b44ccbd420b22d5141ff3";
    private final String clientSecret = "ebf1324bba0f464e80727e68f91549ce";
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

    // Hämtar liknande låtar från Spotify baserat på låttitel och artist från SR
    public ArrayList<String> getSimilarSongs(String srResponse) {
        if (accessToken == null) {
            accessToken = getAccessToken(); // Hämtar token om det inte finns
        }

        try {
            // Extraherar titel och artist från Sveriges Radio JSON-svaret
            JSONObject srJson = new JSONObject(srResponse);
            String trackTitle = srJson.getJSONObject("playlist").getJSONObject("song").getString("title");
            String artistName = srJson.getJSONObject("playlist").getJSONObject("song").getString("artist");

            String encodedTrackTitle = URLEncoder.encode(trackTitle, StandardCharsets.UTF_8);
            String encodedArtistName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);

            // Bygger Spotify API-URL med den extraherade informationen
            String url = String.format("https://api.spotify.com/v1/search?q=track:%s+artist:%s&type=track&limit=2", encodedTrackTitle, encodedArtistName);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseSimilarSongs(response.body());
            } else {
                throw new Exception("Spotify API-förfrågan misslyckades med statuskod: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Returnerar en tom lista om något går fel
        }
    }

    public ArrayList<String> getSongsFromSameAlbum(String srResponse) {
        if (accessToken == null) {
            accessToken = getAccessToken();
        }

        try {
            JSONObject srJson = new JSONObject(srResponse);
            String trackTitle = srJson.getJSONObject("playlist").getJSONObject("song").getString("title");
            String artistName = srJson.getJSONObject("playlist").getJSONObject("song").getString("artist");

            String encodedTrackTitle = URLEncoder.encode(trackTitle, StandardCharsets.UTF_8);
            String encodedArtistName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);

            String url = String.format("https://api.spotify.com/v1/search?q=track:%s+artist:%s&type=track&limit=1", encodedTrackTitle, encodedArtistName);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject responseJson = new JSONObject(response.body());

                String albumId = responseJson.getJSONObject("tracks").getJSONArray("items").getJSONObject(0)
                        .getJSONObject("album").getString("id");

                return getTracksFromAlbum(albumId);
            } else {
                throw new Exception("Spotify förfrågan misslyckades: " + response.statusCode());
            }
        } catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private ArrayList<String> getTracksFromAlbum(String albumId) {
        try{
            String url = String.format("https://api.spotify.com/v1/albums/%s/tracks", albumId);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject responseJson = new JSONObject(response.body());
                ArrayList<String> similarSongs = new ArrayList<>();

                for(int i = 0; i < responseJson.getJSONArray("items").length(); i++){
                    String songtitle = responseJson.getJSONArray("items").getJSONObject(i).getString("name");
                    similarSongs.add(songtitle);
                }
                return similarSongs;
            } else{
                throw new Exception("Spotify förfrågan misslyckades: " + response.statusCode());
            }
        } catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    // Parsar rekommendationerna från JSON-svaret till en arraylist
    private ArrayList<String> parseSimilarSongs(String responseBody) {
        //System.out.println(responseBody);
        ArrayList<String> recommendations = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(responseBody);
            JSONObject tracksObject = json.getJSONObject("tracks");
            JSONArray tracks = tracksObject.getJSONArray("items");

            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                String trackName = track.getString("name");
                recommendations.add(trackName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return recommendations;
    }
}
