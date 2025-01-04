package com.example.javalin.models;

import java.util.List;

public class Quiz {
    private Track correctAnswer;
    private List<Track> options;

    public Quiz(Track correctAnswer, List<Track> options) {
        this.correctAnswer = correctAnswer;
        this.options = options;
    }

    public Track getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Track correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<Track> getOptions() {
        return options;
    }

    public void setOptions(List<Track> options) {
        this.options = options;
    }
}
