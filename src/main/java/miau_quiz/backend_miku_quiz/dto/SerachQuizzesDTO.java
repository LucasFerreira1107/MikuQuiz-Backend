package miau_quiz.backend_miku_quiz.dto;

import miau_quiz.backend_miku_quiz.Enums.Difficulty;

public record SerachQuizzesDTO(
		String q,
		String difficulty, 
		String tagsId) {

}
