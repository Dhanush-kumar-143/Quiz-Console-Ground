package com.quiz.dto;

/** JSON for GET /api/results (matches frontend score/total keys). */
public record ResultsResponse(int score, int total) {}
