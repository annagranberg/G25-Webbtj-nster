package com.example.javalin.models;

import com.google.gson.annotations.SerializedName;

public class Answer {
    @SerializedName("TEXT")
    private String text;
    @SerializedName("CORRECT")
    private boolean correct;


    public Answer(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
