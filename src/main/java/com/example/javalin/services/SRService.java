package com.example.javalin.services;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.XML;

public class SRService {
    private final String P3_LÅTLISTA = "https://api.sr.se/api/v2/playlists/rightnow?channelid=164";

    public String fetchCurrentSong() {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(P3_LÅTLISTA);
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

                String title = jsonResponse.getJSONObject("playlist")
                        .getJSONObject("previoussong")
                        .getString("title");
                String artist = jsonResponse.getJSONObject("playlist")
                        .getJSONObject("previoussong")
                        .getString("artist");

            } else {
                response.append("Gick inte att hämta data. Response code: ").append(responseCode);
            }
        } catch (Exception e) {
            response.append("Ett fel inträffade vid inhämtning av data: ").append(e.getMessage());
        }

        return response.toString();
    }
}


