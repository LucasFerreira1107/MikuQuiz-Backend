package miau_quiz.backend_miku_quiz.dto;

public record PersonalInfoDTO(
		String name, 
		String email,
		String bio,
		String avatarUrl, 
		Long level,
		Long experience,
		Long nextLevelExp,
		Long quizzesCreated, 
		Long quizzesPlayed, 
		Long totalPoints, 
		Double accuracyPercent, 
		Long maxStreak) {

}
