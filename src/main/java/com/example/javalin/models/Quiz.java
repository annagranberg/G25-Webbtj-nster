package com.example.javalin.models;

import java.util.ArrayList;

public class Quiz {
    private final String QUESTION = "Vad heter låten?";
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

    @Override
    public String toString() {
        return "Quiz{" +
            "QUESTION='" + QUESTION + '\'' +
            ", answers=" + answers +
            '}';
    }
}
