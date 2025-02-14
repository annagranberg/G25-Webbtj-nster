package com.example.javalin.services;

import java.net.HttpURLConnection;
import java.net.URL;
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

                // Konvertera den råa XML-strängen till JSON - vi behöver inte konvertera från XML...
                JSONObject jsonResponse = new JSONObject(response.toString());

                JSONObject playlist = jsonResponse.getJSONObject("playlist");
                if (!playlist.has("song")) {
                    return null;
                }

                String title = playlist.getJSONObject("song").getString("title");
                String artist = playlist.getJSONObject("song").getString("artist");

                String startTimeRaw = playlist.getJSONObject("song").optString("starttimeutc", "N/A");

                long startTimeMillis = 0;
                if (!startTimeRaw.equals("N/A")) {
                    startTimeMillis = Long.parseLong(startTimeRaw.replaceAll("[0-9]", ""));

                    System.out.println("Nuvarande låt: " + title + "av " + artist + " Starttid (millisek): " + startTimeMillis);
                } else {
                    System.out.println("Ingen starttid tillgänglig.");
                }
                /*
                String title = jsonResponse.getJSONObject("playlist")
                        .getJSONObject("song")
                        .getString("title");
                String artist = jsonResponse.getJSONObject("playlist")
                        .getJSONObject("song")
                        .getString("artist");


                 */
                this.currentSong = new CurrentSong(title, artist, startTimeMillis);
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


