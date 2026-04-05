package com.quiz.service;

import com.quiz.QuestionBank;
import com.quiz.dto.QuestionResponse;
import com.quiz.dto.ResultsResponse;
import com.quiz.model.Question;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class QuizSessionService {

    private final List<Question> questions;
    private final Map<Integer, String> answers = new LinkedHashMap<>();

    public QuizSessionService() {
        this.questions = QuestionBank.defaultQuestions();
    }

    /** Clears answers and returns questions safe for the client (no answers). */
    public synchronized List<QuestionResponse> startAndListQuestions() {
        answers.clear();
        return questions.stream().map(this::toResponse).toList();
    }

    private QuestionResponse toResponse(Question q) {
        return new QuestionResponse(
                q.getId(),
                q.getQuestion(),
                List.of(q.getOpt1(), q.getOpt2(), q.getOpt3(), q.getOpt4()));
    }

    public synchronized void submit(int questionId, String selectedOption) {
        if (selectedOption == null || selectedOption.isBlank()) {
            throw new IllegalArgumentException("selectedOption is required");
        }
        Question q = findById(questionId);
        if (q == null) {
            throw new IllegalArgumentException("Unknown question id: " + questionId);
        }
        answers.put(questionId, selectedOption.trim());
    }

    private Question findById(int id) {
        for (Question q : questions) {
            if (q.getId() == id) {
                return q;
            }
        }
        return null;
    }

    public synchronized ResultsResponse results() {
        int score = 0;
        for (Question q : questions) {
            String user = answers.get(q.getId());
            if (user != null && user.equals(q.getAnswer())) {
                score++;
            }
        }
        return new ResultsResponse(score, questions.size());
    }
}
