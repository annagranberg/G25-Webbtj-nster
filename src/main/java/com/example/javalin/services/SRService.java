package com.example.javalin.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SRService {
    private final String SRurl = "https://api.sr.se/api/v2/playlists/rightnow?channelid=";

    public String fetchCurrentSong() {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(this.SRurl);
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


    public String fetchCurrentSong(String channelId) {
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(this.SRurl + channelId);
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
                InputStream is = new ByteArrayInputStream(fetchCurrentSong().getBytes());

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(is);

                NodeList songs = doc.getElementsByTagName("song");
                if (songs.getLength() > 0) {
                    Element element = (Element) songs.item(0);
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    String artist = element.getElementsByTagName("artist").item(0).getTextContent();

                    return "Current Song: " + title + " by " + artist;
                }
            }
        } catch (Exception e) {}
        return null;
    }
}


