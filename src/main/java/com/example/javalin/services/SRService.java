package com.example.javalin.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SRService {
    private final String SR_API_URL = "https://api.sr.se/api/v2/channels?name=p3&format=json";

    public String fetchP3Data() {
        StringBuilder response = new StringBuilder();
        try {

            URL url = new URL(SR_API_URL);
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
            } else {
                response.append("Gick inte att hämta data. Response code: ").append(responseCode);
            }
        } catch (IOException e) {
            response.append("Ett fel inträffade vid inhämtning av data: ").append(e.getMessage());
        }

        return response.toString();
    }
}
