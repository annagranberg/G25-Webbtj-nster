package com.example.javalin.controllers;

import com.example.javalin.services.SpotifyService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class SpotifyController {
    private final String clientId = "DITTclientid";
    private final String clientSecret = "DITTclientsecret";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String accessToken = clientId + ":" + clientSecret;
    private SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    private void getAccessToken() {
        try {
            String apiUrl = "https://accounts.spotify.com/api/token";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Basic " + accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                this.accessToken = responseBody.split("\"access_token\":\"")[1].split("\"")[0];
            }else{
                throw new RuntimeException("Failed to get access token" + response.body());
            }
        }catch (Exception e) {
            throw new RuntimeException("Fel vid autentisering med Spotify: " + e.getMessage());
        }
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
