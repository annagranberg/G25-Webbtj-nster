package com.example.javalin.models;

import java.util.List;

public class Quiz {
    private String[][] correctAnswer;
    private String[][] options;

    public Quiz(String[][] correctAnswer, String[][] options) {
        this.correctAnswer = correctAnswer;
        this.options = options;
    }

    public String[][] getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String[][] correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String[][] getOptions() {
        return options;
    }

    public void setOptions(String[][] options) {
        this.options = options;
    }
}
