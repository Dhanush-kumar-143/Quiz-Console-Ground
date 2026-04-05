package com.quiz.web;

import com.quiz.dto.QuestionResponse;
import com.quiz.dto.ResultsResponse;
import com.quiz.dto.SubmitRequest;
import com.quiz.service.QuizSessionService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizSessionService quizSessionService;

    public QuizController(QuizSessionService quizSessionService) {
        this.quizSessionService = quizSessionService;
    }

    @GetMapping("/questions")
    public List<QuestionResponse> questions() {
        return quizSessionService.startAndListQuestions();
    }

    @PostMapping("/submit")
    public ResponseEntity<Void> submit(@RequestBody SubmitRequest body) {
        try {
            quizSessionService.submit(body.questionId(), body.selectedOption());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/results")
    public ResultsResponse results() {
        return quizSessionService.results();
    }
}
