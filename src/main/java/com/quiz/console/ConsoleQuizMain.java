package com.quiz.console;

import com.quiz.QuestionBank;
import com.quiz.model.Question;
import java.util.Scanner;

/**
 * Optional terminal quiz (no Spring). Run from IDE: main on this class, or:
 * {@code mvn -q exec:java -Dexec.mainClass=com.quiz.console.ConsoleQuizMain}
 */
public final class ConsoleQuizMain {

    private ConsoleQuizMain() {}

    public static void main(String[] args) {
        Question[] questions = QuestionBank.defaultQuestions().toArray(Question[]::new);
        String[] selection = new String[questions.length];
        Scanner sc = new Scanner(System.in);

        int i = 0;
        for (Question q : questions) {
            System.out.println("Question No. : " + q.getId());
            System.out.println(q.getQuestion());
            System.out.println(q.getOpt1());
            System.out.println(q.getOpt2());
            System.out.println(q.getOpt3());
            System.out.println(q.getOpt4());
            selection[i++] = sc.nextLine();
        }

        for (String s : selection) {
            System.out.println(s);
        }

        int score = 0;
        for (i = 0; i < questions.length; i++) {
            if (questions[i].getAnswer().equals(selection[i])) {
                score++;
            }
        }
        System.out.println("Your score is :" + score);
    }
}
