package miau_quiz.backend_miku_quiz.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import miau_quiz.backend_miku_quiz.dto.QuizCreateDTO;
import miau_quiz.backend_miku_quiz.dto.QuizUpdateDTO;
import miau_quiz.backend_miku_quiz.entity.Quiz;

//  uses = {TagsService.class}
@Mapper(componentModel = "spring")
public interface QuizMapper {
	
//	@Mapping(source = "tagsId", target ="tagsID")
	Quiz toEntity(QuizCreateDTO dto);
	
	QuizCreateDTO toDTO(Quiz autor);
	
	@Mapping(target = "questions", ignore = true)
	Quiz toEntityUpdate(QuizUpdateDTO dto, @MappingTarget Quiz Quiz);
	
}