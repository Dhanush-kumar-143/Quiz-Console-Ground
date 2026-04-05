package com.quiz.model;

/** One quiz question with four options and the correct answer text. */
public class Question {

    private final int id;
    private final String question;
    private final String opt1;
    private final String opt2;
    private final String opt3;
    private final String opt4;
    private final String answer;

    public Question(
            int id,
            String question,
            String opt1,
            String opt2,
            String opt3,
            String opt4,
            String answer) {
        this.id = id;
        this.question = question;
        this.opt1 = opt1;
        this.opt2 = opt2;
        this.opt3 = opt3;
        this.opt4 = opt4;
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getOpt1() {
        return opt1;
    }

    public String getOpt2() {
        return opt2;
    }

    public String getOpt3() {
        return opt3;
    }

    public String getOpt4() {
        return opt4;
    }

    public String getAnswer() {
        return answer;
    }
}
