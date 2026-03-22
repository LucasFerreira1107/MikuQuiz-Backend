package miau_quiz.backend_miku_quiz.dto;

import java.util.List;
import java.util.Set;

import miau_quiz.backend_miku_quiz.Enums.Difficulty;
import miau_quiz.backend_miku_quiz.Enums.QuizStatus;
import miau_quiz.backend_miku_quiz.Enums.TimeLimit;
import miau_quiz.backend_miku_quiz.Enums.Visibility;

public record QuizUpdateDTO(
		 String title,
		 String description,
		 Difficulty difficulty,
		 Set<String> tagsId,
		 TimeLimit timePerQuestion,
		 QuizStatus status,
		 Boolean allowOffline,
		 List<UpdateQuestionDTO> questions
) {

}
