package miau_quiz.backend_miku_quiz.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import miau_quiz.backend_miku_quiz.dto.PersonalInfoDTO;
import miau_quiz.backend_miku_quiz.entity.User;

@Mapper(componentModel="spring")
public interface PersonalInfoMapper {

	
	
	
	@Mapping(target = "quizzesCreated", ignore = true)
	@Mapping(target = "quizzesPlayed", ignore = true)
	@Mapping(target = "totalPoints", ignore = true)
	@Mapping(target = "accuracyPercent", ignore = true)
	@Mapping(target = "maxStreak", ignore = true)
	PersonalInfoDTO toDTO(User user);
	
}
