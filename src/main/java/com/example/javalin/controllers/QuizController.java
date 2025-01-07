package com.example.javalin.controllers;

import com.example.javalin.models.Quiz;

public class QuizController {
    //@todo skapa all logik för att hantera quiz

    private Quiz quiz;

    public QuizController(Quiz quiz) {
        this.quiz = quiz;
    }
}
