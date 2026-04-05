package com.quiz.dto;

import java.util.List;

/** JSON shape for GET /api/questions (no correct answer exposed). */
public record QuestionResponse(int id, String question, List<String> options) {}
