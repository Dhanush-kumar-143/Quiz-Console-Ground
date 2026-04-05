package com.quiz.dto;

/** JSON body for POST /api/submit. */
public record SubmitRequest(int questionId, String selectedOption) {}
