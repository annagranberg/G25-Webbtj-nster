package com.example.javalin.models;

public class Quiz {
    private CurrentSong correctAnswer;
    private String[][] options;

    public Quiz(CurrentSong correctAnswer, String[][] options) {
        this.correctAnswer = correctAnswer;
        this.options = options;
    }

    public CurrentSong getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(CurrentSong correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String[][] getOptions() {
        return options;
    }

    public void setOptions(String[][] options) {
        this.options = options;
    }
}
