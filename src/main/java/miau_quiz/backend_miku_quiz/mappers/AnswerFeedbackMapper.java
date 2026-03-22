package miau_quiz.backend_miku_quiz.mappers;

import org.mapstruct.Mapper;

import miau_quiz.backend_miku_quiz.dto.AnswerFeedbackDTO;
import miau_quiz.backend_miku_quiz.entity.Answer;

@Mapper(componentModel="spring")
public interface AnswerFeedbackMapper {

	AnswerFeedbackDTO toDTO(Answer answer);
}
