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

    public Handler index = ctx -> {
        try {
            String htmlContent = loadHtmlFromResource("/index.html");
            ctx.contentType("text/html");
            ctx.result(htmlContent);
        }catch (Exception e) {
            ctx.status(404).result("File Not Found");
        }
        ctx.result("Radiokanaler");
    };

    public Handler getP1 = ctx -> {
        try {
            String htmlContent = loadHtmlFromResource("templates/P1.html");
            ctx.contentType("text/html");
            ctx.result(htmlContent);
        } catch (IOException e) {
            ctx.status(404).result("File not found");
        }
    };

    public Handler getP2 = ctx -> {
        ctx.result("P2");
    };

    public Handler getP3 = ctx -> {
        ctx.result("P3");
    };

    public Handler getP4 = ctx -> {
        ctx.result("P4");
    };
}
