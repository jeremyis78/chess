package com.jeremybrooks.chess.eval;

public class Term {

    protected int score;
    protected String description;

    public Term() {
        super();
    }

    public Term(int score, String description) {
        super();
        this.score = score;
        this.description = description;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}