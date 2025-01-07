package com.example.javalin.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Quiz {
    @SerializedName("QUESTION")
    private final String QUESTION = "Vad heter låten?";
    @SerializedName("ANSWERS")
    private ArrayList<Answer> answers;

    public Quiz() {
        answers = new ArrayList<>();
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    
}
