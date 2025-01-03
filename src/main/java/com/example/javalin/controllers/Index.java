package com.example.javalin.controllers;

import io.javalin.http.Handler;

public class Index {

    public Handler index = ctx -> {
        ctx.result("Radiokanaler");
    };

    public Handler getP1 = ctx -> {
        ctx.result("P1");
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
