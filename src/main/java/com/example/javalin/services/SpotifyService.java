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
    private final String clientSecret = "4f968dd333794f9e9daf0995543f2827";
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

                //kontrollerar om items-arrayen innehåller objekt
                JSONArray items = responseJson.getJSONObject("tracks").getJSONArray("items");
                if (items.length() == 0) {
                    throw new Exception(("Inga låtar hittades i Spotify-svaret"));
                }

                //hämta album-id från det första objektet
                String albumId = items.getJSONObject(0).getJSONObject("album").getString("id");

                //hämta låt fr album
                return getTracksFromAlbum(albumId);
            } else {
                throw new Exception("Spotify förfrågan misslyckades: " + response.statusCode());
            }
        } catch (Exception e){
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
                JSONArray items = responseJson.getJSONArray("items");
                ArrayList<String> similarSongs = new ArrayList<>();

                //kontrollera att arrayen har tillräckligt med objekt
                int numberOfTracks = Math.min(items.length(), 5); //högst 5 låtar
                for(int i = 1; i < numberOfTracks; i++){ //börja fr index 1
                    String songTitle = items.getJSONObject(i).getString("name");
                    similarSongs.add(songTitle);
                }
                return similarSongs;
            } else{
                throw new Exception("Spotify förfrågan misslyckades: " + response.statusCode());
            }
        } catch (Exception e){
            return new ArrayList<>();
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
            String url = String.format("https://api.spotify.com/v1/search?q=track:%s+artist:%s&type=track&limit=5", encodedTrackTitle, encodedArtistName);

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
            return new ArrayList<>(); // Returnerar en tom lista om något går fel
        }
    }

    // Parsar rekommendationerna från JSON-svaret till en arraylist
    private ArrayList<String> parseSimilarSongs(String responseBody) {
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
            return new ArrayList<>();
        }

        return recommendations;
    }


    //testar lägga till metod för att först hämta artist-ID fr spotify baserat på artistens namn och sedan använda detta id
    //i anropför att få artistens top-tracks
    public String getArtistId(String artistName) {
        if (accessToken == null) {
            accessToken = getAccessToken();
        }

        try {
            String encodedArtistName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
            String url = String.format("https://api.spotify.com/v1/artists/0TnOYISbd1XYRBk9myaseg" + encodedArtistName);

            System.out.println("skickar förfrågan för att hämta artist-id" + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray artists = jsonResponse.getJSONObject("artists").getJSONArray("items");

                if (artists.length() > 0) {
                    return artists.getJSONObject(0).getString("is");
            } else {
                throw new Exception("Ingen artist hittades med namnet" + response.statusCode());
            }
        } else {
                throw new Exception("spotify api-förfrågan misslyckades med statuskod: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("kunde inte hämta artist-s för: " + artistName, e);
        }
    }

    public ArrayList<String> getArtistSongs(String srResponse) {
        if (accessToken == null) {
            accessToken = getAccessToken();
        }

        try {
            JSONObject srJson = new JSONObject(srResponse);
            String artistName = srJson.getJSONObject("playlist").getJSONObject("song").getString("artist");

            String artistId = getArtistId(artistName);
            if (artistId == null) {
                throw new Exception("kunde inte hämta artist-id för: " + artistName);
            }
            //String encodedArtistName = URLEncoder.encode(artistName, StandardCharsets.UTF_8);

            String url = String.format("https://api.spotify.com/v1/artists/%s/top-tracks?market=SE", artistId);

            System.out.println("Skicka förfrågan till spotify api: " + url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseArtistSongs(response.body());
            } else {
                throw new Exception("Spotify API-förfrågan misslyckades med statuskod: " + response.statusCode());
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private ArrayList<String> parseArtistSongs(String responseBody) {
        ArrayList<String> artistSongs = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray tracks = json.getJSONArray("tracks");

            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                String trackName = track.getString("name");
                artistSongs.add(trackName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return artistSongs;
    }

}
