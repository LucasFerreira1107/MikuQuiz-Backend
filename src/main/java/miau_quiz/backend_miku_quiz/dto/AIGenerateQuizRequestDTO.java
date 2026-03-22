package miau_quiz.backend_miku_quiz.dto;

import miau_quiz.backend_miku_quiz.Enums.Difficulty;

public record AIGenerateQuizRequestDTO(
		String title,
		String topic,
		Difficulty difficulty,
		int numberOfQuestions ) {

}
