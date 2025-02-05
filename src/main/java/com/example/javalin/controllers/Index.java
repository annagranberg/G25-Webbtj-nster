package com.example.javalin.controllers;

import io.javalin.http.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Index {

    private String loadHtmlFromResource(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("File not found: " + resourcePath);
        }

        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    public Handler getChannels = ctx -> {
        try {
            String htmlContent = loadHtmlFromResource(ctx.pathParam("templates/Channels.html"));
            ctx.contentType("text/html");
            ctx.result(htmlContent);
        }catch (Exception e){
            ctx.status(500).result(e.getMessage());
        }
    };

    public Handler index = ctx -> {
        try {
            String htmlContent = loadHtmlFromResource("templates/homePage.html");
            ctx.contentType("text/html");
            ctx.result(htmlContent);
        }catch (Exception e) {
            ctx.status(500).result("File Not Found");
        }
    };

    public Handler getP1 = ctx -> {
        try {
            String htmlContent = loadHtmlFromResource("templates/P1.html");
            ctx.contentType("text/html");
            ctx.result(htmlContent);
        } catch (IOException e) {
            ctx.status(500).result("File not found");
        }
    };

    public Handler getP2 = ctx -> {
        try{
            String htmlContent = loadHtmlFromResource("templates/P2.html");
            ctx.contentType("text/html");
            ctx.result(htmlContent);
        }catch (IOException e) {
            ctx.status(500).result("File not found");
        }
    };

    public Handler getP3 = ctx -> {
        try{
            String htmlContent = loadHtmlFromResource("templates/P3.html");
            ctx.contentType("text/html");
            ctx.result(htmlContent);
        } catch (IOException e) {
            ctx.status(500).result("File not found");
        }
    };

    public Handler getP4 = ctx -> {
        try{
            String htmlContent = loadHtmlFromResource("templates/P4.html");
            ctx.contentType("text/html");
            ctx.result(htmlContent);
        } catch (IOException e) {
            ctx.status(500).result("File not found");
        }
    };
}
