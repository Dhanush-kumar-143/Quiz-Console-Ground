package com.quiz;

import com.quiz.model.Question;
import java.util.List;

/** Default quiz content (same as the original QuestionService). */
public final class QuestionBank {

    private QuestionBank() {}

    public static List<Question> defaultQuestions() {
        return List.of(
                new Question(
                        1,
                        "size of int",
                        "4 bytes",
                        "2 bytes",
                        "1 byte",
                        "8 bytes",
                        "4 bytes"),
                new Question(
                        2,
                        "size of String",
                        "Depends on content",
                        "1 byte",
                        "2 bytes",
                        "4 bytes",
                        "Depends on content"),
                new Question(3, "size of byte", "1", "2", "4", "8", "1"),
                new Question(
                        4,
                        "size of boolean",
                        "1 bit",
                        "1 byte",
                        "2 bytes",
                        "Depends on JVM",
                        "Depends on JVM"),
                new Question(5, "size of char", "1", "2", "4", "8", "2"));
    }
}
