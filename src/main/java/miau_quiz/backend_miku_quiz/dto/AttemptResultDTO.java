package miau_quiz.backend_miku_quiz.dto;

import java.util.List;

public record AttemptResultDTO(
		int score,
		int totalQuestion,
		double accuracy,
		int maxStreak,
		List<QuestionResultDTO> results,
		boolean levelUp
		
		) {

}
