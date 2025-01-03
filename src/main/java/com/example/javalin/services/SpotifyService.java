package com.example.javalin.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class SpotifyService {

    private final String spotifyTokenUrl = "https://accounts.spotify.com/api/token";
    private final String spotifyApiUrl = "https://api.spotify.com/v1/";
    private final String clientId = "";
    private final String clientSecret = "";

    public String fetchRecommendations(String title, String artist) {
        String token = getSpotifyAccessToken();
        if (token == null) {
            return null;
        }

        try {
            String query = "q=artist:" + artist + " track:" + title + "$type=track&limit=10";
            URL url = new URL (spotifyApiUrl + "search?" + query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + token);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getSpotifyAccessToken() {
        try {
            URL url = new URL(spotifyTokenUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

            String body = "grant_type=client_credentials";
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String tokenResponse = response.toString();
            return tokenResponse.substring(tokenResponse.indexOf(":\"") + 2, tokenResponse.indexOf("\""));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
