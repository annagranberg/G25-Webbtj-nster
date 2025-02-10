package com.example.javalin.services;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.example.javalin.models.CurrentSong;
import org.json.JSONObject;
import org.json.XML;


public class SRService {
    private final String SRurl = "https://api.sr.se/api/v2/playlists/rightnow?channelid=";
    private final String srUrlEnd = "&format=json&indent=true";
    private CurrentSong currentSong;

    public String fetchCurrentSong(String channelId) {
        StringBuilder response = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusSeconds(52);

        try {
            URL url = new URL(this.SRurl + channelId + srUrlEnd);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Konvertera den råa XML-strängen till JSON
                JSONObject jsonResponse = XML.toJSONObject(response.toString());
                if (jsonResponse.getJSONObject("playlist").has("previoussong")) {
                    JSONObject previousSong = jsonResponse.getJSONObject("playlist").getJSONObject("previoussong");

                    String previousTitle = previousSong.getString("title");
                    String previousArtist = previousSong.getString("artist");
                    String previousStartTime = previousSong.getString("starttimeutc");
                    String previousEndTime = previousSong.getString("stoptimeutc");

                    LocalDateTime startDateTime = LocalDateTime.parse(previousStartTime, formatter);
                    LocalDateTime endDateTime = LocalDateTime.parse(previousEndTime, formatter);
                    if(oneMinuteAgo.isAfter(startDateTime) && oneMinuteAgo.isBefore(endDateTime)) {
                        this.currentSong = new CurrentSong(previousTitle, previousArtist);
                    } else {
                        System.out.println("Låten har inte börjat spela ännu");
                    }
                }

                String title = jsonResponse.getJSONObject("playlist")
                        .getJSONObject("song")
                        .getString("title");
                String artist = jsonResponse.getJSONObject("playlist")
                        .getJSONObject("song")
                        .getString("artist");

                String startTime = jsonResponse.getJSONObject("starttimeutc")
                        .getJSONObject("date").getString("value");

                String endTime = jsonResponse.getJSONObject("stoptimeutc")
                        .getJSONObject("date").getString("value");

                LocalDateTime startDateTime = LocalDateTime.parse(startTime, formatter);
                LocalDateTime endDateTime = LocalDateTime.parse(endTime, formatter);

                if(oneMinuteAgo.isAfter(startDateTime) && oneMinuteAgo.isBefore(endDateTime)) {
                    this.currentSong = new CurrentSong(title, artist);
                } else {
                    System.out.println("Låten har inte börjat spela ännu");
                }
            } else {
                response.append("Gick inte att hämta data. Response code: ").append(responseCode);
            }
        } catch (Exception e) {
            //response.append("Ett fel inträffade vid inhämtning av data: ").append(e.getMessage());
        }

        return response.toString();
    }

    public CurrentSong getCurrentSong() {
        return currentSong;
    }
}


